package com.inschos.yunda.data.dao;

import com.inschos.yunda.data.mapper.ClaimRecordMapper;
import com.inschos.yunda.model.ClaimInfo;
import com.inschos.yunda.model.ClaimRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClaimRecordDao {
    @Autowired
    private ClaimRecordMapper claimRecordMapper;

    public long addClaimRecord(ClaimRecord claimRecord) {
        return claimRecordMapper.addClaimRecord(claimRecord);
    }

    public List<ClaimRecord> findClaimRecordList(ClaimRecord claimRecord) {
        return claimRecordMapper.findClaimRecordList(claimRecord);
    }

    public long addClaimInfo(ClaimInfo claimInfo) {
        return claimRecordMapper.addClaimInfo(claimInfo);
    }

    public ClaimRecord findClaimRecord(ClaimRecord claimRecord) {
        return claimRecordMapper.findClaimRecord(claimRecord);
    }

    public ClaimInfo findClaimInfo(ClaimInfo claimInfo) {
        return claimRecordMapper.findClaimInfo(claimInfo);
    }

    public ClaimRecord findClaimVerify(ClaimRecord claimRecord) {
        return claimRecordMapper.findClaimRecord(claimRecord);
    }

    public long updateClaimInfo(ClaimInfo claimInfo){
        return claimRecordMapper.updateClaimInfo(claimInfo);
    }
}
