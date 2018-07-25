package io.openmessaging;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

//这是评测程序的一个demo版本，其评测逻辑与实际评测程序基本类似，但是比实际评测简单很多
//该评测程序主要便于选手在本地优化和调试自己的程序

public class DemoTester {
    private static final byte[] messageTemplate = new byte[]{
            'A', 'A', 'A', 'A', 'B', 'B', 'B', 'B', 'C', 'C',
            'C', 'C', 'D', 'D', 'D', 'D', 'E', 'E', 'E', 'E',
            'F', 'F', 'F', 'F', 'G', 'G', 'G', 'G', 'H', 'H',
            'H', 'H', 'I', 'I', 'I', 'I', 'J', 'J', 'J', 'J',
            'K', 'K', 'K', 'K', 'L', 'L', 'L', 'L', 'M', 'M',
            'M', 'M', 'N', 'N', 'N', 'N', 'O', 'O', 'O', 'O',
            'P', 'P', 'P', 'P', 'Q', 'Q', 'Q', 'Q', 'R', 'R',
            'R', 'R', 'S', 'S', 'S', 'S', 'T', 'T', 'T', 'T',
            'U', 'U', 'U', 'U', 'V', 'V', 'V', 'V', 'W', 'W',
            'W', 'W', 'X', 'X', 'X', 'X', 'Y', 'Y', 'Y', 'Y',
            'Z', 'Z', 'Z', 'Z', 'a', 'a', 'a', 'a', 'b', 'b',
            'b', 'b', 'c', 'c', 'c', 'c', 'd', 'd', 'd', 'd',
            'e', 'e', 'e', 'e', 'f', 'f', 'f', 'f', 'g', 'g',
            'g', 'g', 'h', 'h', 'h', 'h', 'i', 'i', 'i', 'i',
            'j', 'j', 'j', 'j', 'k', 'k', 'k', 'k', 'l', 'l',
            'l', 'l', 'm', 'm', 'm', 'm', 'n', 'n', 'n', 'n',
            'o', 'o', 'o', 'o', 'p', 'p', 'p', 'p', 'q', 'q',
            'q', 'q', 'r', 'r', 'r', 'r', 's', 's', 's', 's',
            't', 't', 't', 't', 'u', 'u', 'u', 'u', 'v', 'v',
            'v', 'v', 'w', 'w', 'w', 'w', 'x', 'x', 'x', 'x',
            'y', 'y', 'y', 'y', 'z', 'z', 'z', 'z', '0', '0',
            '0', '0', '1', '1', '1', '1', '2', '2', '2', '2',
            '3', '3', '3', '3', '4', '4', '4', '4', '5', '5',
            '5', '5', '6', '6', '6', '6', '7', '7', '7', '7',
            '8', '8', '8', '8', '9', '9', '9', '9',
    };

    private static final byte[] bigMessageTemplate;

    static {
        bigMessageTemplate = new byte[32768];
        for (int i = 0; i < bigMessageTemplate.length; i++) {
            bigMessageTemplate[i] = messageTemplate[i % messageTemplate.length];
        }
    }

    // used by producer, base64 string followed by fixed length bytes
    private static byte[] produce(int index, int base64Len) {
        byte[] bytes = new byte[base64Len + 8];
        System.arraycopy(bigMessageTemplate, 0, bytes, 0, base64Len);
        // construct index for indexing checking
        for (int i = 0; i < 4; i++) {
            bytes[(base64Len + i)] = ((byte) (index >>> 8 * i));
        }
        // hash code for correctness checking
        System.arraycopy(bigMessageTemplate, 0, bytes, base64Len + 4, 4);
        return bytes;
    }

    // used by consumer, index and integrity checking
    private static boolean verify(byte[] message, int index) {
        // 1st: for index checking
        int verifyingIdx = 0;
        for (int i = 0; i < 4; i++) {
            byte b = message[(message.length - 8 + i)];
            verifyingIdx += ((b & 0xFF) << 8 * i);
        }
        if (verifyingIdx != index) {
            System.out.println(verifyingIdx + " , " + index);
            System.out.println("index checking fail");
            return false;
        }
        // 2nd: for integrity checking
        int integrityStart = message.length - 4;
        for (int i = 0; i < 4; i++) {
            if (message[i] != message[integrityStart + i]) {
                System.out.println(message.length);
                System.out.println("idx:" + verifyingIdx);
                System.out.println(message[i] + message[integrityStart + i]);
                System.out.println("integrity checking fail");
                return false;
            }
        }
        return true;
    }

    public static void main(String args[]) throws Exception {
        //评测相关配置
        //发送阶段的发送数量，也即发送阶段必须要在规定时间内把这些消息发送完毕方可
        int msgNum = 10000000;
        //发送阶段的最大持续时间，也即在该时间内，如果消息依然没有发送完毕，则退出评测
        int sendTime = 2000 * 1000;
        //消费阶段的最大持续时间，也即在该时间内，如果消息依然没有消费完毕，则退出评测
        int checkTime = 1600 * 1000;
        //队列的数量
        int queueNum = 1000000; // attention: currently need to be exact 1000000
        //正确性检测的次数
        int checkNum = queueNum;
        //消费阶段的总队列数量
        int checkQueueNum = queueNum / 10;
        //发送的线程数量
        int sendTsNum = 10;
        //消费的线程数量
        int checkTsNum = 10;        // attention: currently should be exact 10, for the synchronization purpose

        ConcurrentMap<String, AtomicInteger> queueNumMap = new ConcurrentHashMap<>();
        for (int i = 0; i < queueNum; i++) {
            queueNumMap.put("Queue-" + i, new AtomicInteger(0));
        }

        QueueStore queueStore = null;

        try {
            Class queueStoreClass = Class.forName("io.openmessaging.DefaultQueueStoreImpl");
            queueStore = (QueueStore) queueStoreClass.newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }

        //Step1: 发送消息
        long sendStart = System.currentTimeMillis();
        long maxTimeStamp = System.currentTimeMillis() + sendTime;
        AtomicLong sendCounter = new AtomicLong(0);
        Thread[] sends = new Thread[sendTsNum];
        for (int i = 0; i < sendTsNum; i++) {
            sends[i] = new Thread(new Producer(queueStore, i, maxTimeStamp, msgNum, sendCounter, queueNumMap));
        }
        for (int i = 0; i < sendTsNum; i++) {
            sends[i].start();
        }
        for (int i = 0; i < sendTsNum; i++) {
            sends[i].join();
        }
        long sendSend = System.currentTimeMillis();
        System.out.printf("Send: %d ms Num:%d\n", sendSend - sendStart, sendCounter.get());
        long maxCheckTime = System.currentTimeMillis() + checkTime;

        //Step2: 索引的正确性校验
        long indexCheckStart = System.currentTimeMillis();
        AtomicLong indexCheckCounter = new AtomicLong(0);
        Thread[] indexChecks = new Thread[checkTsNum];
        for (int i = 0; i < sendTsNum; i++) {
            indexChecks[i] = new Thread(new IndexChecker(queueStore, i, maxCheckTime, checkNum, indexCheckCounter, queueNumMap));
        }
        for (int i = 0; i < sendTsNum; i++) {
            indexChecks[i].start();
        }
        for (int i = 0; i < sendTsNum; i++) {
            indexChecks[i].join();
        }
        long indexCheckEnd = System.currentTimeMillis();
        System.out.printf("Index Check: %d ms Num:%d\n", indexCheckEnd - indexCheckStart, indexCheckCounter.get());

        //Step3: 消费消息，并验证顺序性
        long checkStart = System.currentTimeMillis();
        Random random = new Random();
        AtomicLong checkCounter = new AtomicLong(0);
        Thread[] checks = new Thread[checkTsNum];
        for (int i = 0; i < sendTsNum; i++) {
            int eachCheckQueueNum = checkQueueNum / checkTsNum;
            ConcurrentMap<String, AtomicInteger> offsets = new ConcurrentHashMap<>();
            for (int j = 0; j < eachCheckQueueNum; j++) {
                String queueName = "Queue-" + random.nextInt(queueNum);
                while (offsets.containsKey(queueName)) {
                    queueName = "Queue-" + random.nextInt(queueNum);
                }
                offsets.put(queueName, queueNumMap.get(queueName));
            }
            checks[i] = new Thread(new Consumer(queueStore, i, maxCheckTime, checkCounter, offsets));
        }
        for (int i = 0; i < sendTsNum; i++) {
            checks[i].start();
        }
        for (int i = 0; i < sendTsNum; i++) {
            checks[i].join();
        }
        long checkEnd = System.currentTimeMillis();
        System.out.printf("Check: %d ms Num: %d\n", checkEnd - checkStart, checkCounter.get());

        //评测结果
        System.out.printf("Tps:%f\n", ((sendCounter.get() + checkCounter.get() + indexCheckCounter.get()) + 0.1) * 1000 / ((sendSend - sendStart) + (checkEnd - checkStart) + (indexCheckEnd - indexCheckStart)));
    }

    static class Producer implements Runnable {

        private AtomicLong counter;
        private ConcurrentMap<String, AtomicInteger> queueCounter;
        private long maxMsgNum;
        private QueueStore queueStore;
        private int number;
        private long maxTimeStamp;

        public Producer(QueueStore queueStore, int number, long maxTimeStamp, int maxMsgNum, AtomicLong counter, ConcurrentMap<String, AtomicInteger> queueCounter) {
            this.counter = counter;
            this.maxMsgNum = maxMsgNum;
            this.queueCounter = queueCounter;
            this.number = number;
            this.queueStore = queueStore;
            this.maxTimeStamp = maxTimeStamp;
        }

        @Override
        public void run() {
            long count;
            while ((count = counter.getAndIncrement()) < maxMsgNum && System.currentTimeMillis() <= maxTimeStamp) {
                try {
                    String queueName = "Queue-" + count % queueCounter.size();
                    synchronized (queueCounter.get(queueName)) {
                        int inQueueOff = queueCounter.get(queueName).getAndIncrement();
                        // currently do not support < 50 ...
                        queueStore.put(queueName, produce(inQueueOff, inQueueOff % 100 == 1 ? 1024 : 50));
//                        queueStore.put(queueName, produce(inQueueOff, 50));
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    System.exit(-1);
                }
            }
        }
    }

    static class IndexChecker implements Runnable {

        private AtomicLong counter;
        private long maxMsgNum;
        private QueueStore queueStore;
        private long maxTimeStamp;
        private int number;
        private ConcurrentMap<String, AtomicInteger> queueCounter;

        public IndexChecker(QueueStore queueStore, int number, long maxTimeStamp, int maxMsgNum, AtomicLong counter, ConcurrentMap<String, AtomicInteger> queueCounter) {
            this.counter = counter;
            this.maxMsgNum = maxMsgNum;
            this.queueStore = queueStore;
            this.number = number;
            this.queueCounter = queueCounter;
            this.maxTimeStamp = maxTimeStamp;
        }

        @Override
        public void run() {
            Random random = new Random();
            while (counter.getAndIncrement() < maxMsgNum && System.currentTimeMillis() <= maxTimeStamp) {
                try {
                    String queueName = "Queue-" + random.nextInt(queueCounter.size());
                    int index = random.nextInt(queueCounter.get(queueName).get()) - 10;
                    if (index < 0) index = 0;
                    Collection<byte[]> msgs = queueStore.get(queueName, index, 10);

                    int cnt = 0;
                    for (byte[] msg : msgs) {
                        if (!verify(msg, index + cnt)) {
                            System.out.println("Check error");
                            System.out.println(queueName);
                            System.exit(-1);
                        }
                        cnt++;
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    System.exit(-1);

                }
            }
        }
    }

    static class Consumer implements Runnable {

        private AtomicLong counter;
        private QueueStore queueStore;
        private ConcurrentMap<String, AtomicInteger> offsets;
        private long maxTimeStamp;
        private int number;

        public Consumer(QueueStore queueStore, int number, long maxTimeStamp, AtomicLong counter, ConcurrentMap<String, AtomicInteger> offsets) {
            this.counter = counter;
            this.queueStore = queueStore;
            this.offsets = offsets;
            this.maxTimeStamp = maxTimeStamp;
            this.number = number;
        }

        @Override
        public void run() {
            ConcurrentMap<String, AtomicInteger> pullOffsets = new ConcurrentHashMap<>();
            for (String queueName : offsets.keySet()) {
                pullOffsets.put(queueName, new AtomicInteger(0));
            }
            while (pullOffsets.size() > 0 && System.currentTimeMillis() <= maxTimeStamp) {
                try {
                    for (String queueName : pullOffsets.keySet()) {
                        int index = pullOffsets.get(queueName).get();
                        Collection<byte[]> msgs = queueStore.get(queueName, index, 10);
                        if (msgs != null && msgs.size() > 0) {
                            pullOffsets.get(queueName).getAndAdd(msgs.size());
                            int cnt = 0;
                            for (byte[] msg : msgs) {
                                if (!verify(msg, index + cnt)) {
                                    System.out.println("Check error");
                                    System.exit(-1);
                                }
                                cnt++;
                            }

                            counter.addAndGet(msgs.size());
                        }
                        if (msgs == null || msgs.size() < 10) {
                            if (pullOffsets.get(queueName).get() != offsets.get(queueName).get()) {
                                System.out.printf("Queue Number Error");
                                System.exit(-1);
                            }
                            pullOffsets.remove(queueName);
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    System.exit(-1);
                }
            }
        }
    }
}


