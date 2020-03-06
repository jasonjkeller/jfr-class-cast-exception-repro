package repro.example;

import jdk.jfr.Configuration;
import jdk.jfr.consumer.RecordedThread;
import jdk.jfr.consumer.RecordingStream;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ClassCastExceptionRepro {
    static BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    static ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static void main(String[] args) {
        executorService.execute(new JfrStreamConsumer());
        executorService.execute(new ByteArrayConsumer(queue));
        executorService.execute(new ByteArrayProducer(queue));
    }

    static class JfrStreamConsumer implements Runnable {
        public void run() {
            try {
                Path configFilePath = Paths.get("ClassCastExceptionDebugProfile.jfc");
                var config = Configuration.create(configFilePath);
                try (var rs = new RecordingStream(config)) {
                    rs.onEvent(recordedEvent -> {
                        try {
                            // FIXME call either getValue("eventThread") or getThread("eventThread") sometimes causes a CCE
//                            final Object eventThreadB = recordedEvent.getValue("eventThread");
                            final RecordedThread eventThreadA = recordedEvent.getThread("eventThread");
                            System.out.println(eventThreadA.getJavaName());
                        } catch (Exception e) {
                            /*
                             * java.lang.ClassCastException: class jdk.jfr.consumer.RecordedObject cannot be cast to
                             * class jdk.jfr.consumer.RecordedThread (jdk.jfr.consumer.RecordedObject and
                             * jdk.jfr.consumer.RecordedThread are in module jdk.jfr of loader 'bootstrap')
                             */
                            e.printStackTrace(); // TODO debug here
                        }
                    });
                    rs.start();
                }
            } catch (IOException | ParseException e) {
                // ignore
            }
        }
    }
}
