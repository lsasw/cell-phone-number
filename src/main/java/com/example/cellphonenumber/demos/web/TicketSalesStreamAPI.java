package com.example.cellphonenumber.demos.web;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class TicketSalesStreamAPI {
    public static void main(String[] args) {
        // 开始时间
        long startTime = System.currentTimeMillis();
        // 设置并行流线程数为4（模拟4个售票窗口）
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");

        // 1. 创建票池：生成1-1000的票号
        List<Integer> ticketPool = IntStream.rangeClosed(1, 1000000)
                .boxed()
                .collect(Collectors.toList());

        // 2. 使用并行流处理售票，并统计每个线程（窗口）的售票数
        Map<String, Long> windowSales = ticketPool.parallelStream()
                //.peek(ticket -> System.out.println("售出票号: " + ticket))
                .collect(Collectors.groupingByConcurrent(
                        ticket -> "窗口-" + Thread.currentThread().getName(), // 以线程名作为窗口标识
                        Collectors.counting() // 统计每个窗口售出票数
                ));
        // 错误示例：可能导致计数不准或异常
            //Map<String, Long> unsafeMap = new HashMap<>();
            //ticketPool.parallelStream().forEach(ticket ->
            //        unsafeMap.merge("窗口", 1L, Long::sum));
        // 3. 输出统计结果
        System.out.println("售票统计结果：");
        windowSales.forEach((window, count) -> 
            System.out.println(window + " 售出: " + count + " 张票"));
        
        long totalSold = windowSales.values().stream().mapToLong(Long::longValue).sum();
        System.out.println("总售出票数: " + totalSold + " 张");
        System.out.println("总耗时: " + (System.currentTimeMillis() - startTime) + " ms");
    }
}