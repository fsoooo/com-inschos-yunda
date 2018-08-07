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

    public long updateStaffPerson(StaffPerson staffPerson) {
        return staffPersonMapper.updateStaffPerson(staffPerson);
    }

    public long findStaffPersonId(StaffPerson staffPerson) {
        if (staffPersonMapper.findStaffPersonId(staffPerson) != null) {
            return staffPersonMapper.findStaffPersonId(staffPerson).longValue();
        } else {
            return 0;
        }
    }

    public StaffPerson findStaffPersonInfoById(StaffPerson staffPerson) {
        return staffPersonMapper.findStaffPersonInfoById(staffPerson);
    }

    public StaffPerson findStaffPersonInfoByCode(StaffPerson staffPerson) {
        return staffPersonMapper.findStaffPersonInfoByCode(staffPerson);
    }

    public StaffPerson findStaffPersonInfoByPhone(StaffPerson staffPerson) {
        return staffPersonMapper.findStaffPersonInfoByPhone(staffPerson);
    }
}