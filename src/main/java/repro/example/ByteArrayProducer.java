package repro.example;

import java.util.concurrent.BlockingQueue;

public class ByteArrayProducer implements Runnable {
    private final BlockingQueue<byte[]> queue;

    public ByteArrayProducer(BlockingQueue<byte[]> queue) {
        this.queue = queue;
    }

    public void run() {
        while (true) {
            queue.offer(new byte[3 * 1024 * 1024]);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
