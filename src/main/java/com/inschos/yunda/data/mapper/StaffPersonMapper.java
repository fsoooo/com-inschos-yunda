package com.inschos.yunda.data.mapper;

import com.inschos.yunda.model.StaffPerson;

public interface StaffPersonMapper {
    long addStaffPerson(StaffPerson staffPerson);

    long findStaffPersonId(StaffPerson staffPerson);

    StaffPerson findStaffPersonInfoById(StaffPerson staffPerson);

    StaffPerson findStaffPersonInfoByCode(StaffPerson staffPerson);
}
