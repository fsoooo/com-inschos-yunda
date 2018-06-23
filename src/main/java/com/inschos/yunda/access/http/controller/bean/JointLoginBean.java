package com.inschos.yunda.access.http.controller.bean;

public class JointLoginBean {

    public static class Requset {
        public String channel_code;
        public String insured_name;
        public String insured_code;
        public String insured_phone;
        public String insured_email;
        public String insured_province;
        public String insured_city;
        public String insured_county;
        public String insured_address;
        public String bank_name;
        public String bank_code;
        public String bank_phone;
        public String bank_address;
        public String channel_order_code;
    }

    public static class Response {
        public String status;//文案显示状态 显示01/不显示02
        public String content;//文案内容
        public String target_url;//默认跳转地址
        public String local_url;//我的保险地址
    }
}
