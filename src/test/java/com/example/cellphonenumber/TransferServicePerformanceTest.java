package com.example.cellphonenumber;

import com.example.cellphonenumber.demos.bank.Account;
import com.example.cellphonenumber.demos.bank.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferServicePerformanceTest {
    private Account accountP;
    private Account accountQ;
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        accountP = new Account("P001", 1000000);
        accountQ = new Account("Q002", 0);
        transferService = new TransferService();
    }

    @Test
    void throughput_UnderHighLoad() throws InterruptedException {
        int threads = 50;
        int iterations = 10000;
        long startTime = System.nanoTime();

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < iterations; j++) {
                    transferService.transfer(accountP, accountQ, 1);
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        long totalOperations = threads * iterations;
        double throughput = totalOperations / (duration / 1000.0); // 操作数/秒

        System.out.printf("吞吐量: %.2f 操作/秒%n", throughput);
        assertTrue(throughput > 1000, "吞吐量过低，存在性能瓶颈"); // 根据实际需求调整阈值
    }
}