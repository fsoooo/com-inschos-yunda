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

    }

}
