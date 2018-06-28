package com.inschos.yunda.access.http.controller.bean;

public class PayCommonBean {
    public static class findWecahtContractRequset {
        public String accountUuid;
        public String userId;
    }

    public static class findWecahtContractResponse extends BaseResponseBean {
        public Object data;
    }

    public static class doWecahtContractRequset {
        public String warrantyUuid;
        public String warrantyCode;
        public String payNo;
        public String wechatAccount;
        public String clientIp;
        public String insuredName;
        public String insuredCode;
        public String insuredPhone;
    }

    public static class doWecahtContractResponse extends BaseResponseBean {
        public Object data;
    }

    public static class findBankAuthorizeRequset {
        public String accountUuid;
        public String userId;
    }

    public static class findBankAuthorizeResponse extends BaseResponseBean {
        public Object data;
    }

    public static class doBankAuthorizeRequset {
        public String warrantyUuid;
        public String warrantyCode;
        public String payNo;
        public String wechatAccount;
        public String clientIp;
        public String insuredName;
        public String insuredCode;
        public String insuredPhone;
    }

    public static class doBankAuthorizeResponse extends BaseResponseBean {
        public Object data;
    }
}
