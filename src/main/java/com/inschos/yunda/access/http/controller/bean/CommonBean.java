package com.inschos.yunda.access.http.controller.bean;

public class CommonBean {

    public static class findAuthorizeRequset {
        public String accountUuid;
        public String userId;
    }

    public static class findAuthorizeResponse extends BaseResponseBean {
        public Object data;
    }

    public static class doBankAuthorizeRequset {
        public String custId;//用户id
        public String accountUuid;//用户account_uuid
        public String name;//用户姓名
        public String bankCode;//用户银行卡号
        public String phone;//用户手机号
        public String requestId;//验证id
        public String vdCode;//验证码
    }

    public static class doBankAuthorizeResponse extends BaseResponseBean {
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

    public static class findUserInfoRequset {
        public String userId;
        public String accountUuid;
    }

    public static class findUserInfoResponse extends BaseResponseBean {
        public findUserInfoResponseData data;
    }

    public static class findUserInfoResponseData{
        public long id;
        public long custId;
        public long accountUuid;
        public String loginToken;
        public String name;
        public long papersType = 1;
        public String papersCode;
        public String phone;
        public long createdAt;
        public long updatedAt;
    }
}
