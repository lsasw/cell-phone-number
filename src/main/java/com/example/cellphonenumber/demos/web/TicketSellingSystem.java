package com.example.cellphonenumber.demos.web;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketSellingSystem {

    // 1. 票务资源池：模拟一个共有 1000张票 的票池
    private static final int TOTAL_TICKETS = 1000;
    private static int ticketPool = TOTAL_TICKETS;

    // 2. 售票窗口：模拟 4个售票窗口
    private static final int NUM_WINDOWS = 4;
    // 每个窗口的独立售票统计
    private static final AtomicInteger[] windowSales = new AtomicInteger[NUM_WINDOWS];

    // 用于模拟售票延迟
    private static final long SELL_DELAY_MS = 10;

    // 扩展挑战：动态余票显示的间隔
    private static final int DISPLAY_INTERVAL = 100;
    private static final AtomicInteger ticketsSoldSinceLastDisplay = new AtomicInteger(0);

    // 售票方法，包含同步逻辑
    public static synchronized boolean sellTicket(int windowId) {
        // 4. 售票规则：检查是否还有票
        if (ticketPool > 0) {
            // 模拟售票操作耗时
            try {
                Thread.sleep(SELL_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("窗口 " + windowId + " 售票线程被中断");
                return false;
            }

            // 售出一张票
            int currentTicketNumber = ticketPool;
            ticketPool--;

            // 更新该窗口的售票统计
            windowSales[windowId - 1].incrementAndGet();

            // 打印售票信息
            System.out.println("窗口" + windowId + "卖出第" + currentTicketNumber + "张票");

            // --- 扩展挑战：动态余票显示 ---
            // 使用原子操作来安全地更新计数器
            int soldCount = ticketsSoldSinceLastDisplay.incrementAndGet();
            if (soldCount >= DISPLAY_INTERVAL) {
                // 为了简化，这里使用 double-checked locking 的思想来减少同步开销
                // 实际上，打印余票本身也需要同步，但为了展示目的，我们接受轻微的不精确性
                // 更精确的方式是将打印余票也放入主锁(sellTicket)中，但这会增加主锁竞争。
                // 这里我们演示一种折衷方案：只在满足条件时才尝试获取锁进行打印。
                if (ticketsSoldSinceLastDisplay.compareAndSet(soldCount, 0)) {
                    System.out.println("--- 剩余票数: " + ticketPool + " ---");
                }
            }
            // --- 扩展挑战结束 ---

            return true; // 成功售出
        }
        return false; // 票已售完
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("开始售票...");

        // 初始化每个窗口的售票计数器
        for (int i = 0; i < NUM_WINDOWS; i++) {
            windowSales[i] = new AtomicInteger(0);
        }

        // 3. 使用ExecutorService（线程池）来管理4个售票窗口线程
        ExecutorService executor = Executors.newFixedThreadPool(NUM_WINDOWS);

        // 提交4个售票任务
        for (int i = 1; i <= NUM_WINDOWS; i++) {
            final int windowId = i;
            executor.submit(() -> {
                // 每个窗口持续售票直到票池为空
                while (true) {
                    boolean sold = sellTicket(windowId);
                    if (!sold) {
                        // 如果没有票了，则退出循环
                        break;
                    }
                }
            });
        }

        // 5. 关闭线程池并等待所有任务完成
        executor.shutdown();
        try {
            // 等待最多1分钟，让所有已提交的任务执行完毕
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                System.err.println("线程池超时，强制关闭");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("等待线程池关闭时被中断");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // 所有窗口售完后，主线程汇总并输出统计结果
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



