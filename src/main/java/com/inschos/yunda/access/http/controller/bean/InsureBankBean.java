package com.inschos.yunda.access.http.controller.bean;

import java.util.List;

public class InsureBankBean {
    public static class bankRequest {
        public long bankId;
        public String token;
        public String name;//用户姓名
        public String bankCode;//用户银行卡号
        public String bankName;//银行名称
        public String bankCity;//开户行地址
        public String phone;//用户手机号
        public long bankUseStatus;//银行卡使用状态
        public long bankAuthorizeStatus;//银行卡授权状态
        public String verifyId;//验证id
        public String verifyCode;//验证码
    }

    public static class addBankRequest {
        public String bankName;//银行卡名称
        public String bankCode;//银行卡号码
        public String bankPhone;//银行卡绑定手机
        public String bankCity;//银行卡开户城市
    }

    public static class bankListResponse extends BaseResponseBean {
        public List<bankInfoResponseData> data;
    }

    public static class bankInfoRequest {
        public long id;
    }

    public static class bankInfoResponse extends BaseResponseBean {
        public bankInfoResponseData data;
    }

    public static class bankInfoResponseData {
        public long id;
        public String bankName;//银行卡名称
        public String bankCode;//银行卡号码
        public String bankPhone;//银行卡绑定手机
        public String bankCity;//银行卡开户城市
        public String bankType;//银行卡类型 1储蓄卡，2借记卡
        public String status;//审核状态(授权状态):是否通过审核检验，1未审核,2已审核,3审核失败
    }

    public static class updateBankStatusRequest {
        public long id;//银行卡id
        public String bankName;//银行卡名称
        public String bankCode;//银行卡号码
        public String bankPhone;//银行卡绑定手机
        public String bankCity;//银行卡开户城市
    }

    public static class deleteBankStatusRequest {
        public long id;//银行卡id
    }

    public static class bankSmsRequest {
        public String origin = "YUNDA";//标识
        public String name;//用户姓名
        public String bankCode;//用户银行卡号
        public String bankPhone;//银行卡绑定手机
        public String idCard;//用户身份证号
    }

    public static class bankSmsResponse extends BaseResponseBean {
        public bankSmsResponseData data;
    }

    public static class bankSmsResponseData {
        public String requestId;//响应ID
    }

    public static class bankVerifyIdRequest {
        public long cust_id;
        public String bank_code;
        public String bank_phone;
    }

    public static class verifyBankSmsRequest {
        public String origin = "YUNDA";//标识
        public String Name;//用户姓名
        public String bankCode;//用户银行卡号
        public String bankPhone;//银行卡绑定手机
        public String idCard;//用户身份证号
        public String requestId;//验证id
        public String vdCode;//验证码
    }

    public static class verifyBankSmsResponse extends BaseResponseBean {
        public verifyBankSmsResponseData data;
    }

    public static class verifyBankSmsResponseData {
        public boolean verifyStatus;
    }

}
