package com.inschos.yunda.access.http.controller.bean;

import java.util.List;

public class InusrePayBean {
    public static class Requset {
        public String warrantyUuid;
        public String payType;
        public String payUrl;
        public String payNo;
        public String payWay;
        public String outPolicyUrl;
        public List<BankData> bankData;
    }

    public static class Response {
        public String status;//状态
        public String statusTxt;//文案内容
    }

    public class BankData {
        public String name;
        public String certCode;
        public String bankPhone;
        public String bankCode;
    }
}

