package com.inschos.yunda.data.mapper;

import com.inschos.yunda.model.BankVerify;

public interface BankVerifyMapper {
    BankVerify findBankVerify(BankVerify bankVerify);

    long addBankVerify(BankVerify bankVerify);

    long updateBankVerify(BankVerify bankVerify);

    BankVerify findBankVerifyRepeat(BankVerify bankVerify);

    BankVerify findBankVerifyId(BankVerify bankVerify);

}
