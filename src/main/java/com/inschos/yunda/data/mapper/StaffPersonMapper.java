package com.inschos.yunda.data.mapper;

import com.inschos.yunda.model.*;

public interface StaffPersonMapper {
    long addStaffPerson(StaffPerson staffPerson);

    long findStaffPerson(StaffPerson staffPerson);
}
