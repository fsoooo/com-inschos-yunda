package com.inschos.yunda.data.mapper;

import com.inschos.yunda.model.*;

public interface WarrantyRecordMapper {
    long addWarrantyRecord(WarrantyRecord warrantyRecord);

    WarrantyRecord findLastDayWarrantyRecord(WarrantyRecord warrantyRecord);

    long updateWarrantyRecord(WarrantyRecord warrantyRecord);

    WarrantyRecord findInsureResult(WarrantyRecord warrantyRecord);
}
