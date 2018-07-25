package io.openmessaging.io.tests;

import io.openmessaging.io.AsyncIO;

public class test_io {
    public static void main(String args[]){
        int queue_num_per_file[] = new int[8];
        int batch_size[] = new int[8];
        for(int i = 0 ; i < 8; i++){
            queue_num_per_file[i] = 100;
            batch_size[i] = 10;
        }
        AsyncIO asyncIO = new AsyncIO("data", 8, queue_num_per_file, batch_size);
        asyncIO.start();
        asyncIO.waitFinishIO();
    }
}
