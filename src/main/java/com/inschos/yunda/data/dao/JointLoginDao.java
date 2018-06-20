package com.inschos.yunda.data.dao;

import com.inschos.yunda.data.mapper.*;
import com.inschos.yunda.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JointLoginDao {
    @Autowired
    private JointLoginMapper jointLoginMapper;

    public int addLoginRecord(JointLogin jointLogin){
        return jointLoginMapper.addLoginRecord(jointLogin);
    }
}
