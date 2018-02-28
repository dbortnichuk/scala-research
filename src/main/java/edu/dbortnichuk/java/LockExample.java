package edu.dbortnichuk.java;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Thread.sleep;

public class LockExample {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Map<String, String> map = new HashMap<>();
        ReadWriteLock lock = new ReentrantReadWriteLock();

        Runnable writeTask1 = () -> {
            lock.writeLock().lock();
            try {
                System.out.println(" write 1 aquired write lock");
                sleep(1000);
                map.put("foo", "bar1");
            }catch(InterruptedException e){
            } finally {
                lock.writeLock().unlock();
            }
        };

        Runnable writeTask2 = () -> {
            lock.writeLock().lock();
            try {
                System.out.println(" write 2 aquired write lock");
                sleep(1000);
                map.put("foo", "bar2");
            }catch(InterruptedException e){
            } finally {
                lock.writeLock().unlock();
            }
        };

        Runnable readTask = () -> {
            lock.readLock().lock();
            try {
                System.out.println("reading: " + map.get("foo"));
                sleep(1);
            }catch(InterruptedException e){
            } finally {
                lock.readLock().unlock();
            }
        };

        executor.submit(readTask);
        executor.submit(writeTask1);
        executor.submit(readTask);
        executor.submit(writeTask2);
        executor.submit(readTask);


        executor.shutdown();
    }



}
