package arhangel.dim.lections.threads.queueu;

public class Main {
    public static void main(String[] args) {
        MyBlockingQueue queue = new MyBlockingQueue();
        Thread thread1 = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++)
                    queue.put(new Integer(i));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        Thread thread2 = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    queue.take();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    thread1.start();
    thread2.start();
    }
}
