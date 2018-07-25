package io.openmessaging.io;

import io.openmessaging.DefaultQueueStoreImpl;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class IOReaderThread {
    final public static long MAPPED_BLOCK_SIZE = AsyncIO.INDEX_ENTRY_SIZE * DefaultQueueStoreImpl.CLUSTER_SIZE * 1024 * 4 * 50;
    final static long IO_READER_MAX_MAPED_CHUNK_NUM = (30L * 1024 * 1024 * 1024 / MAPPED_BLOCK_SIZE);

    IOReaderThread(FileChannel fd) {
        try {
            for (int i = 0; i < IO_READER_MAX_MAPED_CHUNK_NUM; i++) {
                if (i * MAPPED_BLOCK_SIZE < fd.size()) {
                    long real_map_size = Math.min(MAPPED_BLOCK_SIZE, fd.size() - i * MAPPED_BLOCK_SIZE);
                    index_mapped_buffers[i] = fd.map(FileChannel.MapMode.READ_ONLY, ((long) i) * MAPPED_BLOCK_SIZE, real_map_size);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public MappedByteBuffer index_mapped_buffers[] = new MappedByteBuffer[(int) IO_READER_MAX_MAPED_CHUNK_NUM];
}
