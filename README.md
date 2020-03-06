# JFR ClassCastException Repro

Consumes JFR Events defined in `ClassCastExceptionDebugProfile.jfc`. Reproduces a `ClassCastException` with Java Flight 
Recorder events.

## Repro

```
java -version
openjdk version "14-ea" 2020-03-17
OpenJDK Runtime Environment (build 14-ea+34-1452)
OpenJDK 64-Bit Server VM (build 14-ea+34-1452, mixed mode, sharing)
```

Run main class `ClassCastExceptionRepro` in IDE and debug at the following line:

`e.printStackTrace(); // TODO debug here`

The `ClassCastException` seems like it can can be induced by generating an exception. For example, this repro generates 
an `OutOfMemoryError` by writing byte arrays to a queue faster than they can be consumed:

````java
Exception in thread "pool-1-thread-3" java.lang.OutOfMemoryError: Java heap space
	at repro.example.ByteArrayProducer.run(ByteArrayProducer.java:14)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1130)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:630)
	at java.base/java.lang.Thread.run(Thread.java:832)
````

Which then triggers the `ClassCastException`:

```java
java.lang.ClassCastException: class jdk.jfr.consumer.RecordedObject cannot be cast to class jdk.jfr.consumer.RecordedThread (jdk.jfr.consumer.RecordedObject and jdk.jfr.consumer.RecordedThread are in module jdk.jfr of loader 'bootstrap')
	at jdk.jfr/jdk.jfr.consumer.RecordedObject.getThread(RecordedObject.java:961)
	at repro.example.ClassCastExceptionRepro$JfrStreamConsumer.lambda$run$0(ClassCastExceptionRepro.java:33)
	at jdk.jfr/jdk.jfr.internal.consumer.Dispatcher$EventDispatcher.offer(Dispatcher.java:52)
	at jdk.jfr/jdk.jfr.internal.consumer.Dispatcher.dispatch(Dispatcher.java:165)
	at jdk.jfr/jdk.jfr.internal.consumer.EventDirectoryStream.processOrdered(EventDirectoryStream.java:211)
	at jdk.jfr/jdk.jfr.internal.consumer.EventDirectoryStream.processRecursionSafe(EventDirectoryStream.java:139)
	at jdk.jfr/jdk.jfr.internal.consumer.EventDirectoryStream.process(EventDirectoryStream.java:97)
	at jdk.jfr/jdk.jfr.internal.consumer.AbstractEventStream.execute(AbstractEventStream.java:243)
	at jdk.jfr/jdk.jfr.internal.consumer.AbstractEventStream$1.run(AbstractEventStream.java:265)
	at jdk.jfr/jdk.jfr.internal.consumer.AbstractEventStream$1.run(AbstractEventStream.java:262)
	at java.base/java.security.AccessController.doPrivileged(AccessController.java:391)
	at jdk.jfr/jdk.jfr.internal.consumer.AbstractEventStream.run(AbstractEventStream.java:262)
	at jdk.jfr/jdk.jfr.internal.consumer.AbstractEventStream.start(AbstractEventStream.java:222)
	at jdk.jfr/jdk.jfr.consumer.RecordingStream.start(RecordingStream.java:329)
	at repro.example.ClassCastExceptionRepro$JfrStreamConsumer.run(ClassCastExceptionRepro.java:43)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1130)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:630)
	at java.base/java.lang.Thread.run(Thread.java:832)
```

## Usage

To build jar: `mvn clean package`  
To run jar: `java -jar target/jfr-class-cast-exception-repro-0.0.1-SNAPSHOT.jar`  
