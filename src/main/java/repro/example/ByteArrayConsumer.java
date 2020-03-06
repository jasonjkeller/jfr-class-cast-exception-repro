package repro.example;

import java.util.concurrent.BlockingQueue;

public class ByteArrayConsumer implements Runnable {
    private final BlockingQueue<byte[]> queue;

    public ByteArrayConsumer(BlockingQueue<byte[]> queue) {
        this.queue = queue;
    }

    public void run() {
        while (true) {
            try {
                queue.take();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
