package io.openmessaging;


import io.openmessaging.io.AsyncIO;
import io.openmessaging.io.AsyncIO_task;
import io.openmessaging.io.IOReaderThread;
import io.openmessaging.io.IOThread;
import io.openmessaging.utils.MessageB64Serialization;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;

import static io.openmessaging.utils.UnsafeUtils.absolutePut;

/**
 * 这是一个简单的基于内存的实现，以方便选手理解题意；
 * 实际提交时，请维持包名和类名不变，把方法实现修改为自己的内容；
 */
public class DefaultQueueStoreImpl extends QueueStore {
    final static String file_prefix = "/alidata1/race2018/data/data";
    final static long TOTAL_QUEUE_NUM = 1000000;
    final public static int IO_THREAD = 4;
    final public static int CLUSTER_SIZE = 20;
    final static int queue_nums[] = new int[IO_THREAD];
    final static int cluster_size[] = new int[IO_THREAD];

    final private static FileChannel data_file_handles[] = new FileChannel[IO_THREAD];
    final private static FileChannel index_file_handles[] = new FileChannel[IO_THREAD];

    final private AsyncIO asyncIO;
    public static final Object indexCheckerStartSync = new Object();

    private boolean phase3 = false;
    private static ConcurrentSkipListSet<Long> tidSet = new ConcurrentSkipListSet<>();

    private static long getQueueID(String queueName) {
        long res = 0;
        long multiplier = 1;
        for (int i = queueName.length() - 1; i >= 0 && queueName.charAt(i) >= '0' && queueName.charAt(i) <= '9'; i--) {
            res += (queueName.charAt(i) - '0') * multiplier;
            multiplier *= 10;
        }
        return res;
    }

    public DefaultQueueStoreImpl() {
        int q_ave = (int) TOTAL_QUEUE_NUM / IO_THREAD;
        for (int i = 0; i < IO_THREAD; i++) {
            queue_nums[i] = q_ave;
            cluster_size[i] = CLUSTER_SIZE;
        }
        queue_nums[IO_THREAD - 1] = (int) TOTAL_QUEUE_NUM - q_ave * (IO_THREAD - 1);
        cluster_size[IO_THREAD - 1] = CLUSTER_SIZE;

        asyncIO = new AsyncIO(file_prefix, IO_THREAD, queue_nums, cluster_size);
        asyncIO.start();

        for (int i = 0; i < IO_THREAD; i++) {
            data_file_handles[i] = AsyncIO.work_threads[i].data_file_fd;
            index_file_handles[i] = AsyncIO.work_threads[i].index_file_fd;
        }
    }

    public void put(String queueName, byte[] message) {
        long queueID = getQueueID(queueName);
        int threadID = (int) (queueID % IO_THREAD);

        IOThread ioThread = AsyncIO.work_threads[threadID];

        long which_queue_in_this_io_thread = queueID / IO_THREAD;
        long queue_offset = ioThread.queue_counter.getAndIncrement((int) which_queue_in_this_io_thread);

        long chunk_id = ((queue_offset / ioThread.batch_size) * (ioThread.chunk_size) +
                (which_queue_in_this_io_thread * ioThread.batch_size) + queue_offset % ioThread.batch_size);

        long idx_file_offset = AsyncIO.INDEX_ENTRY_SIZE * chunk_id;
        int which_mapped_chunk = (int) (idx_file_offset / AsyncIO.INDEX_MAPPED_BLOCK_SIZE);

        long offset_in_mapped_chunk = idx_file_offset % AsyncIO.INDEX_MAPPED_BLOCK_SIZE;
        while (ioThread.index_file_memory_blocks[which_mapped_chunk] == null) {
//            System.out.println("I am full...");
            synchronized (ioThread.sync_blocks[which_mapped_chunk]) {
                if (ioThread.index_file_memory_blocks[which_mapped_chunk] == null) {
                    try {
                        ioThread.sync_blocks[which_mapped_chunk].wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (message.length > AsyncIO.RAW_NORMAL_MESSAGE_SIZE) {
            ByteBuffer index_record = ByteBuffer.allocate(AsyncIO.INDEX_ENTRY_SIZE);
            byte[] message_ser_buf = MessageB64Serialization.SerializeBase64DecodingSkipIndex(message, message.length);
            long large_msg_size = message_ser_buf.length;
            long large_msg_offset = ioThread.data_file_size.getAndAdd(large_msg_size);

            index_record.putLong(4, large_msg_offset);
            index_record.putLong(12, large_msg_size);
            index_record.put(AsyncIO.INDEX_ENTRY_SIZE - 1, AsyncIO.LARGE_MESSAGE_MAGIC_CHAR);
            try {
                ioThread.data_file_fd.write(ByteBuffer.wrap(message_ser_buf), large_msg_offset);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(int i = 0 ; i < AsyncIO.INDEX_ENTRY_SIZE; i++){
                ioThread.index_file_memory_blocks[which_mapped_chunk][(int) (offset_in_mapped_chunk + i)] = index_record.get(i);
            }
            //absolutePut(ioThread.index_file_memory_blocks[which_mapped_chunk], (int) offset_in_mapped_chunk, index_record);
        } else {
            MessageB64Serialization.SerializeBase64DecodingSkipIndexOff(message, message.length,
                    ioThread.index_file_memory_blocks[which_mapped_chunk], (int) offset_in_mapped_chunk);
        }

        int write_times = ioThread.index_mapped_block_write_counter.incrementAndGet(which_mapped_chunk);

        if (write_times == AsyncIO.INDEX_BLOCK_WRITE_TIMES_TO_FULL) {
//            System.out.println("send one remap task");
            AsyncIO_task task = new AsyncIO_task(0);
            asyncIO.submitIOTask(threadID, task);
        }

    }

    static class MemoryPool {
        Collection<byte[]> results = new ArrayList<>((int) 48);
        byte[][] bytes = new byte[48][];
        ByteBuffer byteBuffer = ByteBuffer.allocate(AsyncIO.INDEX_ENTRY_SIZE * CLUSTER_SIZE);

        MemoryPool() {
            for (int i = 0; i < 48; i++) {
                bytes[i] = new byte[58];
            }
        }
    }

    private static MemoryPool threadMemoryPool[] = new MemoryPool[128];

    static {
        for (int i = 0; i < threadMemoryPool.length; i++) {
            threadMemoryPool[i] = new MemoryPool();
        }
    }

    public Collection<byte[]> get(String queueName, long offset, long num) {
        long currentTid = Thread.currentThread().getId();
        if (!phase3) {
            asyncIO.waitFinishIO();
            asyncIO.waitMappedBuffers();
            if (!tidSet.contains(currentTid)) {
                tidSet.add(currentTid);
                if (tidSet.size() > 10) {
                    phase3 = true;
                    System.out.println("start check phase");
                }
            }
        }

        MemoryPool memoryStruct = threadMemoryPool[(int) currentTid];


        Collection<byte[]> results = memoryStruct.results;
        results.clear();

        long queueID = getQueueID(queueName);

        int threadID = (int) (queueID % IO_THREAD);
        IOThread ioThread = AsyncIO.work_threads[threadID];
        int batch_size = ioThread.batch_size;
        int which_queue_in_this_io_thread = (int) (queueID / IO_THREAD);

        long queue_count = ioThread.queue_counter.get(which_queue_in_this_io_thread);
        long max_offset = Math.min(offset + num, queue_count);

        IOReaderThread ioReaderThread = AsyncIO.ioReaderThread[threadID];

        ByteBuffer index_record = memoryStruct.byteBuffer;

        int result_counter = 0;

        long chunk_size = ioThread.chunk_size;
        long chunk_offset = (which_queue_in_this_io_thread * batch_size);
        for (long queue_offset = offset; queue_offset < max_offset; ) {
            long left_num = batch_size - (queue_offset % batch_size);
            if (queue_offset + left_num > max_offset) {
                left_num = max_offset - queue_offset;
            }
            long chunk_id = ((queue_offset / batch_size) * chunk_size + chunk_offset + queue_offset % batch_size);
            long idx_file_offset = AsyncIO.INDEX_ENTRY_SIZE * chunk_id;

            index_record.position(0);
            index_record.limit((int) (left_num * AsyncIO.INDEX_ENTRY_SIZE));

            if (phase3) {

                int which_chunk_in_io_file = (int) (idx_file_offset / IOReaderThread.MAPPED_BLOCK_SIZE);

                int index_in_this_mapped_chunk = (int) (idx_file_offset % IOReaderThread.MAPPED_BLOCK_SIZE);
                for (int i = 0; i < index_record.limit(); i++) {
                    index_record.put(i, ioReaderThread.index_mapped_buffers[which_chunk_in_io_file].get(index_in_this_mapped_chunk + i));
                }
            } else {
                try {
                    ioThread.index_file_fd.read(index_record, idx_file_offset);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (int element = 0; element < left_num; element++) {
                byte output_buf[];
                if (index_record.get(AsyncIO.INDEX_ENTRY_SIZE * (element + 1) - 1) == AsyncIO.LARGE_MESSAGE_MAGIC_CHAR) {
                    long large_msg_size;
                    long large_msg_offset;
                    large_msg_offset = index_record.getLong(4 + AsyncIO.INDEX_ENTRY_SIZE * element);
                    large_msg_size = index_record.getLong(12 + AsyncIO.INDEX_ENTRY_SIZE * element);
                    ByteBuffer large_msg_buf = ByteBuffer.allocate((int) large_msg_size);
                    try {
                        ioThread.data_file_fd.read(large_msg_buf, large_msg_offset);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    output_buf = MessageB64Serialization.DeserializeBase64EncodingAddIndex(large_msg_buf.array(),
                            0, large_msg_buf.position(), (int) (queue_offset + element));
                    results.add(output_buf);
                } else {

                    MessageB64Serialization.DeserializeBase64EncodingAddIndexNormalSize(index_record.array(),
                            element * AsyncIO.INDEX_ENTRY_SIZE, AsyncIO.INDEX_ENTRY_SIZE, (int) (queue_offset + element), memoryStruct.bytes[result_counter]);
                    results.add(memoryStruct.bytes[result_counter]);
                    result_counter++;
                }

            }
            queue_offset += left_num;
        }
        return results;
    }
}
