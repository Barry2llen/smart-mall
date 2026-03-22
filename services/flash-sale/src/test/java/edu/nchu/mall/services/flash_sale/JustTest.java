package edu.nchu.mall.services.flash_sale;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class JustTest {
    public static void main(String[] args) {
        Runner runner = new SynchronizedRunner(1235);
        Thread t1 = new Thread(() -> {
            try {
                runner.printOdd();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                runner.printEven();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        t2.start();
    }
}

abstract class Runner {
    protected int n;

    public Runner (int n) {
        this.n = n;
    }

    abstract public void printOdd() throws InterruptedException;

    abstract public void printEven() throws InterruptedException;
}

class SemaphoreRunner extends Runner {
    private int x;
    private Semaphore odd;
    private Semaphore even;

    public SemaphoreRunner (int n) {
        super(n);
        this.x = 1;
        this.odd = new Semaphore(1);;
        this.even = new Semaphore(0);
    }

    public void printOdd() throws InterruptedException {
        for (int i = 1;i <= n/2 + n % 2;i++) {
            odd.acquire();
            System.out.println(x++);
            even.release(1);
        }
    }

    public void printEven() throws InterruptedException {
        for (int i = 1;i <= n/2;i++) {
            even.acquire();
            System.out.println(x++);
            odd.release(1);
        }
    }
}

class ReentrantLockRunner extends Runner {
    private int x;
    private final ReentrantLock lock;
    private final Condition condition;

    public ReentrantLockRunner (int n) {
        super(n);
        this.x = 1;
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
    }

    public void printOdd() throws InterruptedException {
        for (int i = 1;i <= n/2 + n % 2;i++) {
            lock.lock();
            try {
                while (x % 2 == 0) {
                    condition.await();
                }
                System.out.println(x++);
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    public void printEven() throws InterruptedException {
        for (int i = 1;i <= n/2;i++) {
            lock.lock();
            try {
                while (x % 2 == 1) {
                    condition.await();
                }
                System.out.println(x++);
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}

class AtomicRunner extends Runner {
    private final AtomicInteger x;

    public AtomicRunner(int n) {
        super(n);
        this.x = new AtomicInteger(1);
    }

    public void printOdd() {
        for (int i = 1; i <= n / 2 + n % 2; i++) {
            while (x.get() % 2 == 0) {
                Thread.onSpinWait();
            }
            System.out.println(x.get());
            x.incrementAndGet();
        }
    }

    public void printEven() {
        for (int i = 1; i <= n / 2; i++) {
            while (x.get() % 2 != 0) {
                Thread.onSpinWait();
            }
            System.out.println(x.get());
            x.incrementAndGet();
        }
    }
}

class SynchronizedRunner extends Runner {

    private int x;
    private final Object lock;

    public SynchronizedRunner(int n) {
        super(n);
        this.x = 1;
        this.lock = new Object();
    }

    public void printOdd() throws InterruptedException {
        for (int i = 1; i <= n / 2 + n % 2; i++) {
            synchronized (lock) {
                while (x % 2 == 0) {
                    lock.wait();
                }
                System.out.println(x++);
                lock.notifyAll();
            }
        }
    }

    public void printEven() throws InterruptedException {
        for (int i = 1; i <= n / 2; i++) {
            synchronized (lock) {
                while (x % 2 != 0) {
                    lock.wait();
                }
                System.out.println(x++);
                lock.notifyAll();
            }
        }
    }
}