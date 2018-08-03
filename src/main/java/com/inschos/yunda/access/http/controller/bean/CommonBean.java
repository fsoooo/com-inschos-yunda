package com.inschos.yunda.access.http.controller.bean;

public class CommonBean {

    public static class findAuthorizeRequset {
        public String token;
    }

    public static class findAuthorizeResponse extends BaseResponseBean {
        public findAuthorizeResponseData data;
    }

    public static class findAuthorizeResponseData {
        public authorizeBank bank;
        public contractWechat weixin;
    }

    public static class authorizeBank {
        public String bankName;//银行名称
        public String bankCode;//银行卡号
        public String bankPhone;//银行预留手机号
        public String bankCity;//开户行

    }

    public static class contractWechat {
        public String openid;//微信openID
        public String changeType;//变更类型 ADD | DELETE
        public String contractCode;//签约协议号
        public String contractExpiredTime;//协议过期时间
        public String contractId;//委托代扣协议id
    }


    public static class doBankAuthorizeRequset {
        public String token;
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
        public String token;
        public String warrantyUuid;
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
}
