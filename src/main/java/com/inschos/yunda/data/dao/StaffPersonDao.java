package com.inschos.yunda.data.dao;

import com.inschos.yunda.data.mapper.StaffPersonMapper;
import com.inschos.yunda.model.StaffPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StaffPersonDao {
    @Autowired
    private StaffPersonMapper staffPersonMapper;

    public long addStaffPerson(StaffPerson staffPerson) {
        return staffPersonMapper.addStaffPerson(staffPerson);
    }

    public long findStaffPersonId(StaffPerson staffPerson) {
        return staffPersonMapper.findStaffPersonId(staffPerson);
    }

    public StaffPerson findStaffPersonInfoById(StaffPerson staffPerson) {
        return staffPersonMapper.findStaffPersonInfoById(staffPerson);
    }

    public StaffPerson findStaffPersonInfoByCode(StaffPerson staffPerson) {
        return staffPersonMapper.findStaffPersonInfoByCode(staffPerson);
    }
}