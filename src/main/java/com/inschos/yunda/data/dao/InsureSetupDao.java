package com.inschos.yunda.data.dao;

import com.inschos.yunda.data.mapper.InsureSetupMapper;
import com.inschos.yunda.model.InsureSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InsureSetupDao {
    @Autowired
    private InsureSetupMapper insureSetupMapper;

    public long addInsureSetup(InsureSetup insureSetup) {
        return insureSetupMapper.addInsureSetup(insureSetup);
    }

    public InsureSetup findInsureAutoInfo(InsureSetup insureSetup) {
        return insureSetupMapper.findInsureAutoInfo(insureSetup);
    }

    public InsureSetup findInsureAuthorizeInfo(InsureSetup insureSetup) {
        return insureSetupMapper.findInsureAuthorizeInfo(insureSetup);
    }

    public long updateInsureAuthorize(InsureSetup insureSetup) {
        return insureSetupMapper.updateInsureAuthorize(insureSetup);
    }

    public long updateInsureAuto(InsureSetup insureSetup) {
        return insureSetupMapper.updateInsureAuto(insureSetup);
    }
}
