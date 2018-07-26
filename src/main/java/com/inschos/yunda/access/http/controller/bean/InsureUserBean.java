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

    public static class AccountInfoResponse extends BaseResponseBean {
        public AccountInfoResponseData data;
    }

    public static class AccountInfoResponseData {
        public String id;//用户id
        public String name;//姓名
        public String papersType;//证件类型1：身份证，2：护照，3：军官证，4：其他
        public String papersCode;//证件号
        public String papersStart;//证件开始时间
        public String papersEnd;//证件结束时间
        public String sex;//性别1、男 2、女
        public String birthday;//生日
        public String address;//地址
        public String addressDetail;//详细地址
        public String phone;//手机号
        public String email;//邮箱
        public String head;//头像地址
        public String insQuaCode;//资格证号
        public String insQuaStart;//开始时间戳
        public String insQuaEnd;//结束时间戳
    }
}
