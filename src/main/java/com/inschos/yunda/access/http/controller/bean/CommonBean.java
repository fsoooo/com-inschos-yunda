package com.inschos.yunda.access.http.controller.bean;

public class CommonBean {

    public static class findAuthorizeRequset {
        public String token;
    }

    public static class findAuthorizeResponse extends BaseResponseBean {
        public Object data;
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
