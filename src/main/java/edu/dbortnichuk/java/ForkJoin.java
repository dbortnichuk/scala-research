package edu.dbortnichuk.java;

import java.util.concurrent.ForkJoinPool;

public class ForkJoin {

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);

//        MyRecursiveAction myRecursiveAction = new MyRecursiveAction(24);
//        forkJoinPool.invoke(myRecursiveAction);

        MyRecursiveTask myRecursiveTask = new MyRecursiveTask(128);
        long mergedResult = forkJoinPool.invoke(myRecursiveTask);
        System.out.println("mergedResult = " + mergedResult);
    }
}
