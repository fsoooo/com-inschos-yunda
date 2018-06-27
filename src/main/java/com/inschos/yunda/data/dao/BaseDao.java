package com.inschos.yunda.data.dao;

import org.springframework.transaction.interceptor.TransactionAspectSupport;

public class BaseDao {
    /**
     * 需要使用事务时,可以继承此父类,使用rollback()方法回滚
     */
    public void rollBack() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
