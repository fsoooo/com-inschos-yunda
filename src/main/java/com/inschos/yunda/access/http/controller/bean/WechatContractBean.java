package com.inschos.yunda.access.http.controller.bean;

public class WechatContractBean {
    public static class Requset {
        public String warrantyUuid;
        public String warrantyCode;
        public String payNo;
        public String wechatAccount;
        public String clientIp;
        public String insuredName;
        public String insuredCode;
        public String insuredPhone;
    }

    public static class Response extends BaseResponseBean {
        public ResponseData data;
    }

    public static class ResponseData {
        public String status;//状态
        public String statusTxt;//文案内容
    }
}
