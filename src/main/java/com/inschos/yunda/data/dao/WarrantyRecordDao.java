package com.inschos.yunda.data.dao;

import com.inschos.yunda.data.mapper.WarrantyRecordMapper;
import com.inschos.yunda.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WarrantyRecordDao {
    @Autowired
    private WarrantyRecordMapper warrantyRecordMapper;

    public long addWarrantyRecord(WarrantyRecord warrantyRecord) {
        return warrantyRecordMapper.addWarrantyRecord(warrantyRecord);
    }

    public WarrantyRecord findLastDayWarrantyRecord(WarrantyRecord warrantyRecord) {
        return warrantyRecordMapper.findLastDayWarrantyRecord(warrantyRecord);
    }

    public WarrantyRecord findInsureResult(WarrantyRecord warrantyRecord) {
        return warrantyRecordMapper.findInsureResult(warrantyRecord);
    }
}
