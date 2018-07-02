package com.inschos.yunda.data.mapper;

import com.inschos.yunda.model.ClaimInfo;
import com.inschos.yunda.model.ClaimRecord;

import java.util.List;

public interface ClaimRecordMapper {
    long addClaimRecord(ClaimRecord claimRecord);

    long addClaimInfo(ClaimInfo ClaimInfo);

    List<ClaimRecord> findClaimRecordList(ClaimRecord claimRecord);

    ClaimRecord findClaimRecord(ClaimRecord claimRecord);

    ClaimInfo findClaimInfo(ClaimInfo claimInfo);

    ClaimRecord findClaimVerify(ClaimRecord claimRecord);

}
