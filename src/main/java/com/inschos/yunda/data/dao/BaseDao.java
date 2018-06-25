package com.inschos.yunda.data.dao;

import org.springframework.transaction.interceptor.TransactionAspectSupport;

public class BaseDao {
    public void rollBack() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
