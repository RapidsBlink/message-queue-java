package io.openmessaging.io;

import io.openmessaging.DefaultQueueStoreImpl;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

enum IO_thread_status {INITING, RUNNING, CLOSING};

public class AsyncIO {
    final public static byte LARGE_MESSAGE_MAGIC_CHAR = '!';
    final public static int INDEX_ENTRY_SIZE = 43;
    final public static int RAW_NORMAL_MESSAGE_SIZE = 58;
    final static int MAX_CONCURRENT_INDEX_MAPPED_BLOCK_NUM = 3;
    final static int MAX_BLOCKING_IO_TASK = 64;
    final public static long INDEX_MAPPED_BLOCK_SIZE = (INDEX_ENTRY_SIZE * 1000 * 250 * 10);
    final public static long INDEX_BLOCK_WRITE_TIMES_TO_FULL = (INDEX_MAPPED_BLOCK_SIZE / INDEX_ENTRY_SIZE);
    final public static int MAX_MAPED_CHUNK_NUM = (int) (24L * 1024 * 1024 * 1024 / INDEX_MAPPED_BLOCK_SIZE);

    static boolean mapped_buffer_flags = false;
    static boolean finished = false;
    static int finished_thread = 0;

    static boolean sended_flush_msg = false;
    static Lock send_lock = new ReentrantLock();
    static int thread_num;
    public static IOThread work_threads[];

    public static IOReaderThread ioReaderThread[] = new IOReaderThread[DefaultQueueStoreImpl.IO_THREAD];

    public AsyncIO(String file_prefix, int thread_num, int queue_num_per_file[], int batch_size[]) {
        AsyncIO.thread_num = thread_num;
        this.work_threads = new IOThread[thread_num];
        for (int i = 0; i < thread_num; i++) {
            work_threads[i] = new IOThread(i, file_prefix, queue_num_per_file[i], batch_size[i], MAX_BLOCKING_IO_TASK);
        }

    }

    public void start() {
        for (int i = 0; i < thread_num; i++) {
            this.work_threads[i].start();
        }
    }

    public void submitIOTask(int which_thread, AsyncIO_task task) {
        if (work_threads[which_thread].status == IO_thread_status.RUNNING) {
            try {
                work_threads[which_thread].blockingQueue.put(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.printf("failed to submit IO task to %d thread\n", which_thread);
        }
    }

    ConcurrentSkipListSet<Long> exitThreadSets = new ConcurrentSkipListSet<>();
    final Object terminateLock = new Object();

    public void waitFinishIO() {
        if (!finished) {
            send_lock.lock();
            if (!sended_flush_msg) {
                sended_flush_msg = true;
                try {
                    for (int i = 0; i < thread_num; i++) {
                        AsyncIO_task task = new AsyncIO_task(-1);
                        work_threads[i].blockingQueue.put(task);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            send_lock.unlock();

            synchronized (DefaultQueueStoreImpl.indexCheckerStartSync) {
                if (AsyncIO.finished_thread < thread_num) {
                    try {
                        DefaultQueueStoreImpl.indexCheckerStartSync.wait();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            finished = true;
        }
    }

    private CyclicBarrier barrier1 = new CyclicBarrier(10, () -> {
//            for(int i = 0 ; i < DefaultQueueStoreImpl.IO_THREAD; i++){
//                for(int chunkID = 0 ; chunkID < AsyncIO.MAX_MAPED_CHUNK_NUM; chunkID++){
//                    if(AsyncIO.work_threads[i].index_file_memory_blocks[chunkID] != null){
//                        AsyncIO.work_threads[i].index_file_memory_blocks[chunkID].clear();
//                        AsyncIO.work_threads[i].index_file_memory_blocks[chunkID] = null;
//                    }
//                }
//            }
        for (int i = 0; i < DefaultQueueStoreImpl.IO_THREAD; i++) {
            ioReaderThread[i] = new IOReaderThread(AsyncIO.work_threads[i].index_file_fd);
        }
    });

    public void waitMappedBuffers() {
        if (!mapped_buffer_flags) {
            try {
                barrier1.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            mapped_buffer_flags = true;
        }
    }
}
