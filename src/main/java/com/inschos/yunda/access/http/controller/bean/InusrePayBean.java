package com.inschos.yunda.access.http.controller.bean;

import java.util.List;

public class InusrePayBean {
    public static class Requset {
        public String payNo;//支付业务号
        public String payWay;//支付方式 1 银联 2 支付宝 3 微信 4现金  必填
        public String outPolicyUrl;//回调通知url
        public List<BankData> bankData;
    }

    public static class Response extends BaseResponseBean {
        public ResponseData data;
    }

    public static class ResponseData {
        public String status;//状态
        public String statusTxt;//文案内容
    }

    public static class BankData {
        public String name;
        public String certCode;
        public String bankPhone;
        public String bankCode;
    }
}

