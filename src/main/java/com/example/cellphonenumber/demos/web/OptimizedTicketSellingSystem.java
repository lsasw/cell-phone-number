package com.example.cellphonenumber.demos.web;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OptimizedTicketSellingSystem {

    private static final int TOTAL_TICKETS = 1000;
    private static volatile int ticketPool = TOTAL_TICKETS; // 使用 volatile 保证可见性

    private static final int NUM_WINDOWS = 4;
    private static final AtomicInteger[] windowSales = new AtomicInteger[NUM_WINDOWS];

    private static final long SELL_DELAY_MS = 10;
    private static final int DISPLAY_INTERVAL = 100;
    private static final AtomicInteger ticketsSoldSinceLastDisplay = new AtomicInteger(0);

    // 使用 ReentrantLock
    private static final Lock ticketPoolLock = new ReentrantLock();

    public static void sellTicket(int windowId) {
        int ticketNumberToSell = -1;
        boolean sold = false;

        // 只在需要修改共享资源时才获取锁
        ticketPoolLock.lock();
        try {
            if (ticketPool > 0) {
                ticketNumberToSell = ticketPool;
                ticketPool--;
                sold = true;
                // 核心的票池操作完成，可以释放锁了
            }
        } finally {
            ticketPoolLock.unlock();
        }

        // 锁外执行非关键操作
        if (sold) {
            // 更新统计 (AtomicInteger 本身线程安全)
            windowSales[windowId - 1].incrementAndGet();

            // 打印售票信息 (I/O操作通常较慢，放在锁外)
            System.out.println("窗口" + windowId + "卖出第" + ticketNumberToSell + "张票");

            // 处理余票显示逻辑
            handleDisplayLogic();
        }
    }

    private static void handleDisplayLogic() {
         // --- 扩展挑战：动态余票显示 ---
         // 注意：这个操作本身不是原子的，打印的票数可能不是绝对精确的瞬时值，
         // 但对于监控目的通常足够了。如果需要绝对精确，需要更复杂的同步。
         int soldCount = ticketsSoldSinceLastDisplay.incrementAndGet();
         if (soldCount >= DISPLAY_INTERVAL) {
             // 尝试原子地重置计数器，如果成功则打印余票
             if (ticketsSoldSinceLastDisplay.compareAndSet(soldCount, 0)) {
                 // 注意：ticketPool 是 volatile 的，这里读取到的是最新的值
                 System.out.println("--- 剩余票数: " + ticketPool + " ---");
             }
         }
         // --- 扩展挑战结束 ---
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("开始售票 (优化版1: ReentrantLock)...");

        for (int i = 0; i < NUM_WINDOWS; i++) {
            windowSales[i] = new AtomicInteger(0);
        }

        ExecutorService executor = Executors.newFixedThreadPool(NUM_WINDOWS);

        for (int i = 1; i <= NUM_WINDOWS; i++) {
            final int windowId = i;
            executor.submit(() -> {
                while (true) {
                    // 模拟售票操作耗时
                    try {
                        Thread.sleep(SELL_DELAY_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("窗口 " + windowId + " 售票线程被中断");
                        break;
                    }
                    
                    // 调用优化后的售票方法
                    sellTicket(windowId);
                    
                    // 如果票卖完了，退出循环
                    // 注意：这里检查 ticketPool 可能不是最新的，但由于循环会继续，
                    // 下一次调用 sellTicket 时会再次检查并正确退出。
                    // 或者可以在 sellTicket 返回 boolean 来指示是否还有票。
                    if (ticketPool <= 0) {
                         break;
                    }
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                System.err.println("线程池超时，强制关闭");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("等待线程池关闭时被中断");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("\n售票结束！");
        int totalSold = 0;
        for (int i = 0; i < NUM_WINDOWS; i++) {
            int soldByWindow = windowSales[i].get();
            totalSold += soldByWindow;
            System.out.println("窗口" + (i + 1) + "售出: " + soldByWindow + "张");
        }
        System.out.println("总售出票数: " + totalSold + "张");
    }
}