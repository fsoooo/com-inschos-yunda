package com.inschos.yunda.extend.insured;

import java.util.List;

public class InsuredResponse {


    public String code;
    public List<Message> message;
    public Data data;

    public class Message {
        public String digest;//default
        public String details;//成功
    }

    public class Data {
        public String status;//保单状态
        public String statusTxt;//保单状态文案
        public String warrantyUuid;//保单UUID
    }


}
