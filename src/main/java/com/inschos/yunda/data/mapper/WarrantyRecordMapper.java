package com.inschos.yunda.data.mapper;

import com.inschos.yunda.model.*;

public interface WarrantyRecordMapper {
    long addWarrantyRecord(WarrantyRecord warrantyRecord);

    WarrantyRecord findLastDayWarrantyRecord(WarrantyRecord warrantyRecord);

    WarrantyRecord findInsureResult(WarrantyRecord warrantyRecord);
}
