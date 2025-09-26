package com.example.cellphonenumber.demos.web;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HighlyOptimizedTicketSellingSystem {

    // 使用 AtomicInteger 管理票池
    private static final AtomicInteger ticketPool = new AtomicInteger(1000);

    private static final int NUM_WINDOWS = 4;
    private static final AtomicInteger[] windowSales = new AtomicInteger[NUM_WINDOWS];

    private static final long SELL_DELAY_MS = 10;
    private static final int DISPLAY_INTERVAL = 100;
    private static final AtomicInteger ticketsSoldSinceLastDisplay = new AtomicInteger(0);

    public static void sellTicket(int windowId) {
        // 原子地减票并获取减之前的值
        int currentTicketNumber = ticketPool.getAndDecrement();

        // 如果返回的票号大于0，说明之前有票，这次售票成功
        if (currentTicketNumber > 0) {
            // 更新该窗口的售票统计 (AtomicInteger 本身线程安全)
            windowSales[windowId - 1].incrementAndGet();

            // 打印售票信息 (在锁外)
            System.out.println("窗口" + windowId + "卖出第" + currentTicketNumber + "张票");

            // 处理余票显示逻辑 (在锁外)
            handleDisplayLogic();

        }
        // 如果 currentTicketNumber <= 0, 说明票已售完，无需操作
    }

     private static void handleDisplayLogic() {
         int soldCount = ticketsSoldSinceLastDisplay.incrementAndGet();
         if (soldCount >= DISPLAY_INTERVAL) {
             if (ticketsSoldSinceLastDisplay.compareAndSet(soldCount, 0)) {
                 // 注意：这里读取 ticketPool.get() 可能与打印信息时的票数有微小的时间差
                 System.out.println("--- 剩余票数: " + ticketPool.get() + " ---");
             }
         }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("开始售票 (优化版2: AtomicInteger)...");

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
                    
                    sellTicket(windowId);
                    
                    // 由于 AtomicInteger 保证了 ticketPool 操作的原子性，
                    // 我们可以安全地检查它是否小于等于0来决定是否退出
                    if (ticketPool.get() < 0) { // 当票卖完后，get() 会返回0或负数
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
        // 最终票数应该是0（如果正好卖完）或一个负数（表示超额卖出的数量，但由于 getAndDecrement 的原子性，这里不会出现）
        // 实际售出 = 初始票数 - 最终 ticketPool 值
        System.out.println("总售出票数: " + (1000 - ticketPool.get()) + "张"); 
        // 或者使用统计的总和
        // System.out.println("总售出票数: " + totalSold + "张");
    }
}