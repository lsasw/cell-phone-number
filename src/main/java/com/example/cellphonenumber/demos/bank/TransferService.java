package com.example.cellphonenumber.demos.bank;

public class TransferService {
    /**
     * 转账操作（原子性保证）
     * @param from  转出账户
     * @param to    转入账户
     * @param amount 转账金额
     * @throws IllegalArgumentException 金额无效或余额不足
     */
    public void transfer(Account from, Account to, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("金额必须大于0");
        if (from == to) throw new IllegalArgumentException("转出和转入账户不能相同");

        // 锁排序：按账户ID的哈希值确定加锁顺序（避免死锁关键）
        Account firstLock = from.getId().compareTo(to.getId()) < 0 ? from : to;
        Account secondLock = firstLock == from ? to : from;

        synchronized (firstLock) {      // 先锁小ID账户
            synchronized (secondLock) { // 再锁大ID账户
                if (from.getBalance() < amount) {
                    throw new IllegalArgumentException("余额不足");
                }
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }
}