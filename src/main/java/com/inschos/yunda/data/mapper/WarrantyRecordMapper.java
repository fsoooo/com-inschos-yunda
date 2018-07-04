package com.inschos.yunda.data.mapper;

import com.inschos.yunda.model.WarrantyRecord;

public interface WarrantyRecordMapper {
    long addWarrantyRecord(WarrantyRecord warrantyRecord);

    long updateWarrantyRecord(WarrantyRecord warrantyRecord);

    WarrantyRecord findLastDayWarrantyRecord(WarrantyRecord warrantyRecord);

    WarrantyRecord findInsureResult(WarrantyRecord warrantyRecord);

    long findInsureWarrantyRes(WarrantyRecord warrantyRecord);
}
