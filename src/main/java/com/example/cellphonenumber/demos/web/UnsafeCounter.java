package com.example.cellphonenumber.demos.web;

import java.util.concurrent.atomic.AtomicInteger;

public class UnsafeCounter {
    private volatile int count = 0; // volatile 仅保证可见性，不保证原子性
    //private final AtomicInteger count = new AtomicInteger(0);

    public  synchronized  void increment() {
        count++; // 非原子操作：实际包含读->改->写三步
        //count.incrementAndGet();
    }

    public int getCount() {
        //return count.get();
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        UnsafeCounter counter = new UnsafeCounter();
        Thread[] threads = new Thread[1000];
        for (int i = 0; i < 1000; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter.increment();
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) t.join();
        System.out.println("Final count: " + counter.getCount()); // 结果可能小于 1,000,000
    }
}