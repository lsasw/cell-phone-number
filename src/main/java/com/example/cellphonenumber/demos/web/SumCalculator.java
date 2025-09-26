package com.example.cellphonenumber.demos.web;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class SumCalculator extends RecursiveTask<Integer> {
    private int start;
    private int end;

    public SumCalculator(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if (end - start <= 10) { // 如果任务足够小，直接计算结果
            int sum = 0;
            for (int i = start; i <= end; i++) {
                sum += i;
            }
            return sum;
        } else { // 否则，分割任务
            int middle = (start + end) / 2;
            SumCalculator subtask1 = new SumCalculator(start, middle);
            SumCalculator subtask2 = new SumCalculator(middle + 1, end);
            subtask1.fork(); // 创建子任务并异步执行
            int subresult2 = subtask2.compute(); // 同步执行另一个子任务
            return subresult2 + subtask1.join(); // 等待子任务完成并合并结果
        }
    }

    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        SumCalculator task = new SumCalculator(1, 1000);
        int result = pool.invoke(task); // 提交任务到线程池并获取结果
        System.out.println("The sum is: " + result);
    }
}
