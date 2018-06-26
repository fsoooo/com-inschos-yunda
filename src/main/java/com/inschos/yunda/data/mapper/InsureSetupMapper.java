package com.inschos.yunda.data.mapper;

import com.inschos.yunda.model.*;

public interface InsureSetupMapper {
    long addInsureSetup(InsureSetup insureSetup);

    InsureSetup findInsureAutoInfo(InsureSetup insureSetup);

    long updateInsureAuto(InsureSetup insureSetup);

    InsureSetup findInsureAuthorizeInfo(InsureSetup insureSetup);

    long updateInsureAuthorize(InsureSetup insureSetup);


}
