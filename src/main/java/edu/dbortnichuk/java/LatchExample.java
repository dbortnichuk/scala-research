package edu.dbortnichuk.java;

import java.util.concurrent.CountDownLatch;
class LatchHelperService extends Thread {
    private int ID;
    private CountDownLatch latch;
    public LatchHelperService(int ID, CountDownLatch latch) {
        this.ID = ID;/*w ww . ja v a2s .c  om*/
        this.latch = latch;
    }
    public void run() {
        try {
            Thread.sleep(1000);
            System.out.println("Service #" + ID + "  has  started...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.latch.countDown();
        }
    }
}

class LatchMainService extends Thread {
    private CountDownLatch latch;

    public LatchMainService(CountDownLatch latch) {
        this.latch = latch;
    }
    public void run() {
        try {
            System.out.println("waiting for services to start.");
            latch.await();
            System.out.println("started.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class LatchExample {
    public static void main(String[] args) {
        // Create a countdown latch with 2 as its counter
        CountDownLatch latch = new CountDownLatch(2);
        LatchMainService ms = new LatchMainService(latch);
        ms.start();
        for (int i = 1; i <= 2; i++) {
            LatchHelperService lhs = new LatchHelperService(i, latch);
            lhs.start();
        }
    }
}
