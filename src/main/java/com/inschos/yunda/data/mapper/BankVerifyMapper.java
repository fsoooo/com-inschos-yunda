package com.inschos.yunda.data.mapper;

import com.inschos.yunda.model.BankVerify;

public interface BankVerifyMapper {

    long addBankVerify(BankVerify bankVerify);

    long updateBankVerify(BankVerify bankVerify);

    BankVerify findBankVerify(BankVerify bankVerify);

    BankVerify findBankVerifyId(BankVerify bankVerify);

}
