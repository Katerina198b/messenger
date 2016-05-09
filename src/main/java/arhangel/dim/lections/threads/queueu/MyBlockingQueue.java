package arhangel.dim.lections.threads.queueu;

import javax.sound.midi.Track;
import java.util.LinkedList;

public class MyBlockingQueue<E> implements BlockingQueue<E> {

    Object monitor = new Object();

    private LinkedList<E> list = new LinkedList<>();

    private int size = 3;
    private int now = 0;

    public int getNow() {
        return now;
    }

    public void setNow(int now) {
        this.now = now;
    }


    @Override
    public void put(E elem) throws InterruptedException {
        synchronized (monitor) {
            while (now == size) {
                monitor.wait();
            }
            list.addLast(elem);
            now++;
            monitor.notifyAll();
        }
    }

    @Override
    public E take() throws InterruptedException {
        if (now != 0) {
            synchronized (monitor) {
                now--;
                monitor.notifyAll();
                return list.removeFirst();
            }
        } else {
            monitor.wait();
        }
        return null;
    }

    @Override
    public E poll(int timeout) throws InterruptedException {
        if (now != 0) {
            synchronized (monitor) {
                now--;
                monitor.notifyAll();
                return list.get(now - 1);
            }
        } else {
            monitor.wait(timeout);
            return null;
        }
    }

    @Override
    public boolean offer(E object) throws InterruptedException {
        if (now == size) {
            return false;
        } else {
            synchronized (monitor) {
                list.add(object);
                now++;
                monitor.notifyAll();
            }
        }
        return true;
    }
}
