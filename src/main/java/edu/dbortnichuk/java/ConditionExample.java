package edu.dbortnichuk.java;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class ConditionExample {

    public static void main(String[] args) {

        Store store=new Store();
        Producer producer = new Producer(store);
        Consumer consumer = new Consumer(store);
        new Thread(producer).start();
        new Thread(consumer).start();
    }
}

class Store{
    private int product=0;
    ReentrantLock locker;
    Condition condition;

    Store(){
        locker = new ReentrantLock();
        condition = locker.newCondition();
    }

    public void get() {
        System.out.println(Thread.currentThread().getId() + " get before lock");
        locker.lock();
        //System.out.println(Thread.currentThread().getId() + " after lock");

        try{
            // wAit till units is not empty
            while (product<1){
                System.out.println(Thread.currentThread().getId() + " get before await");
            condition.await();
            System.out.println(Thread.currentThread().getId() + " get after await");
        }


        product--;
        //System.out.println("consumer buy 1");
        //System.out.println("store: " + product);


        condition.signalAll();
        System.out.println(Thread.currentThread().getId() + " get after signal");
    }
        catch (InterruptedException e){
        System.out.println(e.getMessage());
    }
        finally{
        locker.unlock();
        System.out.println(Thread.currentThread().getId() + " get after unlock");
    }
}
    public void put() {
        System.out.println(Thread.currentThread().getId() + " put before lock");
        locker.lock();
        try{
            // wait if 3 units in store
            while (product>=3) {
                System.out.println(Thread.currentThread().getId() + " put before await");
                condition.await();
                System.out.println(Thread.currentThread().getId() + " put after await");
            }

            product++;
            //System.out.println("producer provided 1 ");
            //System.out.println("store: " + product);

            condition.signalAll();
        }
        catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
        finally{
            locker.unlock();
            System.out.println(Thread.currentThread().getId() + " put after unlock");
        }
    }
}

class Producer implements Runnable{

    Store store;
    Producer(Store store){
        this.store=store;
    }
    public void run(){
        for (int i = 1; i < 6; i++) {
            store.put();
        }
    }
}

class Consumer implements Runnable{

    Store store;
    Consumer(Store store){
        this.store=store;
    }
    public void run(){
        for (int i = 1; i < 6; i++) {
            store.get();
        }
    }
}
