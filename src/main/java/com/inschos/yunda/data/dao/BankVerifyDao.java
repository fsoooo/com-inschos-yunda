package com.inschos.yunda.data.dao;

import com.inschos.yunda.data.mapper.BankVerifyMapper;
import com.inschos.yunda.model.BankVerify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BankVerifyDao {
    @Autowired
    private BankVerifyMapper bankVerifyMapper;

    public BankVerify findBankVerify(BankVerify bankVerify) {
        return bankVerifyMapper.findBankVerify(bankVerify);
    }

    public long addBankVerify(BankVerify bankVerify) {
        return bankVerifyMapper.addBankVerify(bankVerify);
    }

    public long updateBankVerify(BankVerify bankVerify) {
        return bankVerifyMapper.updateBankVerify(bankVerify);
    }

    public BankVerify findBankVerifyRepeat(BankVerify bankVerify) {
        return bankVerifyMapper.findBankVerifyRepeat(bankVerify);
    }

    public BankVerify findBankVerifyId(BankVerify bankVerify) {
        return bankVerifyMapper.findBankVerifyId(bankVerify);
    }
}
