package com.inschos.yunda.access.http.controller.bean;

public class InsureUserBean {

    public static class userInfoRequest {
        public String token;
        public String name;
        public long papersType = 1;
        public String papersCode;
        public String phone;
    }

    public static class userInfoResponse extends BaseResponseBean {
        public userInfoResponseData data;
    }

    public static class userInfoResponseData {
        public long id;
        public long custId;
        public long accountUuid;
        public String loginToken;
        public String name;
        public long papersType = 1;
        public String papersCode;
        public String phone;
        public long createdAt;
        public long updatedAt;
    }
}
