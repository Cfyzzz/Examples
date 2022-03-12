import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Service {
    private AtomicInteger count = new AtomicInteger(0);
    private LinkedList<String> log = new LinkedList<>();
    private static Lock lock = new ReentrantLock();

    public synchronized void log() {
        count.getAndIncrement();
        log.add(Thread.currentThread().getName());
    }

    public synchronized void debug() {
        System.out.println(count.get());
        log.forEach(System.out::println);
    }

    public static void main(String[] args) {
        Service counter = new Service();
        var t1 = new Thread(() -> process(counter), "T1");
        var t2 = new Thread(() -> process(counter), "T2");
        var t3 = new Thread(() -> process(counter), "T3");

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        counter.debug();
    }

    private static void process(Service counter) {
        try {
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    for (var i=0; i < 10; i++) {
                        counter.log();
                        Thread.sleep(10);
                    }
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
