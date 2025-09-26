package com.example.cellphonenumber;

import com.example.cellphonenumber.demos.bank.Account;
import com.example.cellphonenumber.demos.bank.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransferServiceFunctionalTest {
    private Account accountA;
    private Account accountB;
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        accountA = new Account("A001", 1000);
        accountB = new Account("B002", 500);
        transferService = new TransferService();
    }

    @Test
    void transfer_Success() {
        // 正常转账：A向B转200
        transferService.transfer(accountA, accountB, 200);
        assertEquals(800, accountA.getBalance()); // A余额减少200
        assertEquals(700, accountB.getBalance()); // B余额增加200
    }

    @Test
    void transfer_InsufficientBalance_ThrowsException() {
        // 余额不足时抛出异常
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> transferService.transfer(accountA, accountB, 1500));
        assertEquals("余额不足", exception.getMessage());
        // 验证余额未变化
        assertEquals(1000, accountA.getBalance());
        assertEquals(500, accountB.getBalance());
    }

    @Test
    void transfer_NegativeAmount_ThrowsException() {
        // 金额为负时拒绝操作
        assertThrows(IllegalArgumentException.class, 
            () -> transferService.transfer(accountA, accountB, -100));
    }

    @Test
    void transfer_SameAccount_ThrowsException() {
        // 转出转入账户相同时报错
        assertThrows(IllegalArgumentException.class, 
            () -> transferService.transfer(accountA, accountA, 100));
    }
}