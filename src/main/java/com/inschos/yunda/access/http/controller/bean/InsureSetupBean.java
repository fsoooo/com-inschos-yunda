package com.inschos.yunda.access.http.controller.bean;

public class InsureSetupBean {
    public static class addInsureAutoRequset {
        public long insureAutoStatus;
        public long insureType;
        public String insurePrice;
    }

    public static class updateInsureAutoRequset {
        public long insureAutoStatus;
        public long insureType;
        public String insurePrice;
    }

    public static class findInsureAutoResponseData {
        public long custId;
        public long autoStatus;
        public String price;
        public long type;
        public long closeTime;
    }

    public static class findInsureAuthorizeResponseData {
        public long custId;
        public String authorizeBank;
        public long authorizeStatus;
    }

    public static class accountInfoRequest {
        public long custId;//用户id
        public long accountUuid;//用户account_uuid
    }

    public static class accountInfoResponse extends BaseResponseBean {
        public accountInfoResponseData data;
    }

    public static class accountInfoResponseData {
        public String name;
        public String idCard;
        public String phone;
        public String loginToken;

    }

    public static class doBankAuthorizeRequest {
        public String name;//用户姓名
        public String bankCode;//用户银行卡号
        public String phone;//用户手机号
        public String verifyId;//验证id
        public String verifyCode;//验证码
    }

    public static class doBankAuthorizeResponse extends BaseResponseBean {
        public Object data;
    }

    public static class doWechatContractRequset {
        public String clientIp;
    }

    public static class doWechatContractResponse extends BaseResponseBean {
        public Object data;
    }
}
