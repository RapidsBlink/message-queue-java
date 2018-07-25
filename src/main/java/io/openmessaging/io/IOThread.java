package io.openmessaging.io;

import io.openmessaging.DefaultQueueStoreImpl;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;

import static io.openmessaging.utils.UnsafeUtils.absolutePut;
import static io.openmessaging.utils.UnsafeUtils.unmap;

public class IOThread extends Thread {

    private void doIO(AsyncIO_task task) throws IOException {
        for (; current_index_mapped_start_chunk < AsyncIO.MAX_MAPED_CHUNK_NUM; current_index_mapped_start_chunk++) {
            if (index_mapped_block_write_counter.get(current_index_mapped_start_chunk) >= AsyncIO.INDEX_BLOCK_WRITE_TIMES_TO_FULL) {

//                MappedByteBuffer index_file_buf = index_file_fd.map(FileChannel.MapMode.READ_WRITE,
//                        AsyncIO.INDEX_MAPPED_BLOCK_SIZE * current_index_mapped_start_chunk, AsyncIO.INDEX_MAPPED_BLOCK_SIZE);
//                absolutePut(index_file_buf, 0, index_file_memory_blocks[current_index_mapped_start_chunk], 0, (int) AsyncIO.INDEX_MAPPED_BLOCK_SIZE);
//                unmap(index_file_buf);
                index_file_fd.write(ByteBuffer.wrap(index_file_memory_blocks[current_index_mapped_start_chunk]), AsyncIO.INDEX_MAPPED_BLOCK_SIZE * current_index_mapped_start_chunk);

                int next_chunk = current_index_mapped_start_chunk + AsyncIO.MAX_CONCURRENT_INDEX_MAPPED_BLOCK_NUM;
                current_index_mapped_start_offset += AsyncIO.INDEX_MAPPED_BLOCK_SIZE;
                current_index_mapped_end_offset += AsyncIO.INDEX_MAPPED_BLOCK_SIZE;
                this.index_file_size = this.current_index_mapped_end_offset;
                this.index_file_fd.truncate(this.index_file_size);

                index_file_memory_blocks[next_chunk] = index_file_memory_blocks[current_index_mapped_start_chunk];
                index_file_memory_blocks[current_index_mapped_start_chunk] = null;
                synchronized (sync_blocks[next_chunk]) {
                    sync_blocks[next_chunk].notifyAll();
                }
            } else {
                break;
            }
        }
    }

    @Override
    public void run() {
        this.status = IO_thread_status.RUNNING;
        for (; ; ) {
            AsyncIO_task task = null;
            try {
                task = this.blockingQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (this.status == IO_thread_status.CLOSING || task.global_offset == -1) {
                for (int i = current_index_mapped_start_chunk; i < AsyncIO.MAX_MAPED_CHUNK_NUM && index_file_memory_blocks[i] != null; i++) {
                    try {
//                        MappedByteBuffer index_file_buf = index_file_fd.map(FileChannel.MapMode.READ_WRITE, i * AsyncIO.INDEX_MAPPED_BLOCK_SIZE, AsyncIO.INDEX_MAPPED_BLOCK_SIZE);
//                        absolutePut(index_file_buf, 0, index_file_memory_blocks[i], 0, (int) AsyncIO.INDEX_MAPPED_BLOCK_SIZE);
//                        unmap(index_file_buf);
                        index_file_fd.write(ByteBuffer.wrap(index_file_memory_blocks[i]), AsyncIO.INDEX_MAPPED_BLOCK_SIZE * i);
                        index_file_memory_blocks[i] = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // must print out, otherwise incorrect because of compiler jit optmization
                synchronized (DefaultQueueStoreImpl.indexCheckerStartSync) {
                    AsyncIO.finished_thread++;
                    if (AsyncIO.finished_thread == DefaultQueueStoreImpl.IO_THREAD) {
                        DefaultQueueStoreImpl.indexCheckerStartSync.notifyAll();
                    }
                }
                break;
            }
            try {
                doIO(task);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    IOThread(int thread_id, String file_prefix, int queue_num_per_file, int batch_size, int blocking_queue_size) {
        this.status = IO_thread_status.INITING;
        this.batch_size = batch_size;
        this.queue_num_per_file = queue_num_per_file;
        this.thread_id = thread_id;
        this.chunk_size = queue_num_per_file * batch_size;
        File data_file_name_f = new File(file_prefix + '_' + thread_id + ".data");
        File index_file_name_f = new File(file_prefix + '_' + thread_id + ".idx");
        try {
            data_file_fd = FileChannel.open(data_file_name_f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE, StandardOpenOption.SPARSE, StandardOpenOption.TRUNCATE_EXISTING);
            index_file_fd = FileChannel.open(index_file_name_f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE, StandardOpenOption.SPARSE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        blockingQueue = new ArrayBlockingQueue<>(AsyncIO.MAX_BLOCKING_IO_TASK);
        this.queue_counter = new AtomicIntegerArray(queue_num_per_file);

        try {
            this.current_index_mapped_start_offset = 0;
            this.current_index_mapped_start_chunk = 0;
            this.current_index_mapped_end_offset = AsyncIO.MAX_CONCURRENT_INDEX_MAPPED_BLOCK_NUM * AsyncIO.INDEX_MAPPED_BLOCK_SIZE;
            this.index_file_size = this.current_index_mapped_end_offset;

            this.index_file_fd.truncate(this.index_file_size);
            for (int i = 0; i < AsyncIO.MAX_CONCURRENT_INDEX_MAPPED_BLOCK_NUM; i++) {
                index_file_memory_blocks[i] = new byte[(int) AsyncIO.INDEX_MAPPED_BLOCK_SIZE];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < AsyncIO.MAX_MAPED_CHUNK_NUM; i++) {
            sync_blocks[i] = new Object();
        }
    }

    private final int thread_id;
    final public long chunk_size;
    private final int queue_num_per_file;
    final public int batch_size;
    public AtomicLong data_file_size = new AtomicLong(0);
    public long index_file_size = 0;
    public AtomicIntegerArray queue_counter;

    public FileChannel data_file_fd;
    public FileChannel index_file_fd;
    final BlockingQueue<AsyncIO_task> blockingQueue;
    IO_thread_status status;

    public long current_index_mapped_start_offset = 0;
    public long current_index_mapped_end_offset = 0;
    public int current_index_mapped_start_chunk = 0;

    public byte[][] index_file_memory_blocks = new byte[AsyncIO.MAX_MAPED_CHUNK_NUM][];
    public AtomicIntegerArray index_mapped_block_write_counter = new AtomicIntegerArray(AsyncIO.MAX_MAPED_CHUNK_NUM);
    public Object sync_blocks[] = new Object[AsyncIO.MAX_MAPED_CHUNK_NUM];
}
