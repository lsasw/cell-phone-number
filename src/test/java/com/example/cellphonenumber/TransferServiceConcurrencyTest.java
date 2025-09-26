package com.example.cellphonenumber;

import com.example.cellphonenumber.demos.bank.Account;
import com.example.cellphonenumber.demos.bank.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.*;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferServiceConcurrencyTest {
    private static final int THREAD_COUNT = 100;
    private static final int TRANSFERS_PER_THREAD = 1000;
    private Account accountX;
    private Account accountY;
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        accountX = new Account("X001", 100000);  // 初始大余额支持并发操作
        accountY = new Account("Y002", 0);
        transferService = new TransferService();
    }

    @Test
    void concurrentTransfer_NoDeadlockAndBalanceConsistent() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        // 提交并发任务：每个线程执行多次小额转账
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // 等待统一开始
                    for (int j = 0; j < TRANSFERS_PER_THREAD; j++) {
                        transferService.transfer(accountX, accountY, 1); // 每次转1元
                    }
                } catch (Exception e) {
                    fail("并发转账出现异常: " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // 同时启动所有线程
        assertTrue(endLatch.await(30, TimeUnit.SECONDS), "测试超时，可能存在死锁");
        executor.shutdown();

        // 验证总余额一致性
        int totalExpected = 100000 + 0; // 初始总额
        int totalActual = (int) (accountX.getBalance() + accountY.getBalance());
        assertEquals(totalExpected, totalActual, "并发转账后总余额不匹配，存在数据竞争");
    }
}