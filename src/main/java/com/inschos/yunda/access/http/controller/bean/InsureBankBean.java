package com.inschos.yunda.access.http.controller.bean;

public class InsureBankBean {
    public static class bankRequest {
        public long bankId;
        public String name;//用户姓名
        public String bankCode;//用户银行卡号
        public String phone;//用户手机号
        public long bankUseStatus;//银行卡使用状态
        public long bankAuthorizeStatus;//银行卡授权状态
    }

    public static class addBankRequest {
        public long custId;//用户id
        public long accountUuid;//用户account_uuid
        public String name;//用户姓名
        public String bankCode;//用户银行卡号
        public String phone;//用户手机号
    }

    public static class addBankResponse extends BaseResponseBean {
        public addBankResponseData data;
    }

    public static class addBankResponseData {

    }

    public static class bankListRequest {
        public long custId;//用户id
        public long accountUuid;//用户account_uuid
    }

    public static class bankListResponse extends BaseResponseBean {
        public bankListResponseData data;
    }

    public static class bankListResponseData {

    }

    public static class bankInfoRequest {
        public long bankId;
        public long custId;//用户id
        public long accountUuid;//用户account_uuid
    }

    public static class bankInfoResponse extends BaseResponseBean {
        public bankInfoResponseData data;
    }

    public static class bankInfoResponseData {

    }
}
