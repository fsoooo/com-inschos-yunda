package com.inschos.yunda.access.http.controller.bean;

import java.util.List;

public class InsureWarrantyBean {
    public static class warrantyRecordRequest {
        public long id;//用户id
        public long custId;//用户id
        public String warrantyUuid;//'warranty_uuid内部保单唯一标识'
        public String warrantyStatus;//保单状态 待支付/保障中/已失效
        public String warrantyStatusText;//保单状态文案
        public long createdAt;
        public long updatedAt;
    }

    public static class warrantyListRequest {
        public long custId;//用户id
        public long accountUuid;//用户account_uuid
        public String warrantyStatus;//保单状态 待支付/保障中/已失效
    }

    public static class warrantyListResponse extends BaseResponseBean {
        public List<warrantyListResponseData> data;
    }

    public static class warrantyListResponseData {

    }

    public static class warrantyInfoRequest {
        public long custId;//用户id
        public long accountUuid;//用户account_uuid
        public String warrantyUuid;//'warranty_uuid内部保单唯一标识'
    }

    public static class warrantyInfoResponse extends BaseResponseBean {
        public warrantyInfoResponseData data;
    }

    public static class warrantyInfoResponseData {

    }

    public static class insureResultRequest {
        public long custId;//用户id
        public long accountUuid;//用户account_uuid
    }

    public static class insureResultResponse extends BaseResponseBean {
        public insureResultResponseData data;
    }

    public static class insureResultResponseData {

    }
}
