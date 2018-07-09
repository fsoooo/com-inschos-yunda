package com.inschos.yunda.access.http.controller.bean;

import java.util.List;

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

    public static class Response extends BaseResponseBean {
        public ResponseData data;
    }

    public static class ResponseData {
        public String status;//文案显示状态 显示01/不显示02
        public String content;//文案内容
        public String target_url;//默认跳转地址
        public String local_url;//我的保险地址
    }

    public static class AuthorizeQueryResponse extends BaseResponseBean {
        public AuthorizeQueryResponseData data;
    }

    public static class AuthorizeQueryResponseData {
        public String status;//授权状态:01未授权/02已授权
        public String url;//授权页面地址
    }

    public static class AccountResponse extends BaseResponseBean {
        public AccountResponseData data;
    }

    public static class AccountResponseData {
        public String custId;
        public String accountUuid;
        public String loginToken;
    }

    public static class BankResponse extends BaseResponseBean {
        public List<BankResponseData> data;
    }

    public static class BankResponseData {
        public String name;
        public String certCode;
        public String bankPhone;
        public String bankCode;
    }
}
