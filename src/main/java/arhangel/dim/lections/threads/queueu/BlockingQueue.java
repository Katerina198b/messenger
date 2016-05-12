package arhangel.dim.lections.threads.queueu;

/**
 *
 */
public interface BlockingQueue<E> {

    /**
     *
     * @param  the element to add
     */
    void put(E elem) throws InterruptedException;

    /**
     *
     * @return the head element
     */
    E take() throws InterruptedException;

    E poll(int timeout) throws InterruptedException;

    boolean offer(E tt) throws InterruptedException;

}
