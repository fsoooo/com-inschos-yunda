package com.inschos.yunda.data.mapper;

import com.inschos.yunda.model.*;

public interface JointLoginMapper {
    long addLoginRecord(JointLogin jointLogin);
    long findLoginRecord(JointLogin jointLogin);
}
