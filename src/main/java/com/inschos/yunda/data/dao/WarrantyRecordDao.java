package com.inschos.yunda.data.dao;

import com.inschos.yunda.data.mapper.WarrantyRecordMapper;
import com.inschos.yunda.model.WarrantyRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WarrantyRecordDao {
    @Autowired
    private WarrantyRecordMapper warrantyRecordMapper;

    public long addWarrantyRecord(WarrantyRecord warrantyRecord) {
        return warrantyRecordMapper.addWarrantyRecord(warrantyRecord);
    }

    public long updateWarrantyRecord(WarrantyRecord warrantyRecord) {
        return warrantyRecordMapper.updateWarrantyRecord(warrantyRecord);
    }

    /**
     * 获取前一天的预投保信息
     *
     * @param warrantyRecord
     * @return
     */
    public WarrantyRecord findLastDayWarrantyRecord(WarrantyRecord warrantyRecord) {
        return warrantyRecordMapper.findLastDayWarrantyRecord(warrantyRecord);
    }

    /**
     * 获取投保记录,避免多次投保
     *
     * @param warrantyRecord
     * @return
     */
    public long findInsureWarrantyRes(WarrantyRecord warrantyRecord) {
        return warrantyRecordMapper.findInsureWarrantyRes(warrantyRecord);
    }

    /**
     * 获取投保结果
     *
     * @param warrantyRecord
     * @return
     */
    public WarrantyRecord findInsureResult(WarrantyRecord warrantyRecord) {
        return warrantyRecordMapper.findInsureResult(warrantyRecord);
    }


}
