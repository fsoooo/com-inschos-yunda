package com.inschos.yunda.extend.inters;

import com.inschos.yunda.annotation.CheckParams;
import com.inschos.yunda.assist.kit.*;
import com.inschos.yunda.model.IntersResponse;

import java.util.Date;
import java.util.List;

public class ExtendInsurePolicy {


    public static class GetInusredRequest extends InsureRequest {
        public long productId;//产品id：英大90/泰康91
        public String startTime;//开始时间,必填
        public String endTime;//结束时间,必填
        public String count;//购买份数,必填
        public String businessNo;//业务识别号
        public String payCategoryId;//缴别ID
        public PolicyHolder policyholder;//投保人 必填
        public List<Recognizee> recognizees;//被保人 必填
        public Beneficiary beneficiary;//投保人
    }

    public static class GetInsuredesponse extends InsureResponse {
        public String code;
        public List<IntersResponse.Message> message;
        public IntersResponse.Data data;

        public static class Message {
            public String digest;//default
            public String details;//成功
        }

        public static class Data {
            public String status;//保单状态
            public String statusTxt;//保单状态文案
            public String warrantyUuid;//保单UUID
        }
    }

    public static class PolicyHolder {
        public String relationName;//本人",//与投保人关系 必填
        public String name;//姓名", //必填
        public String cardType;//证件类型  必填
        public String cardCode;//证件号  必填
        public String phone;//手机号  必填
        //以下数据 按业务选填
        public String occupation;//职业
        public String birthday;//生日 时间戳
        public String sex;//性别 1 男 2 女
        public String age;//年龄
        public String email;//邮箱
        public String nationality;//国籍
        public String annualIncome;//年收入
        public String height;//身高
        public String weight;//体重
        public String area;//地区
        public String address;//详细地址

        public String courier_state;//站点地址
        public String courier_start_time;//分拣开始时间

        public String province;//省
        public String city;//市
        public String county;//县

        public String bank_code;//银行卡号
        public String bank_name;//银行名称
        public String bank_phone;//银行卡绑定名字

        public String insure_days;//购保天数
        public String price;//保费
    }

    public static class Recognizee {
        public String relationName;//本人",//与投保人关系 必填
        public String name;//姓名", //必填
        public String cardType;//证件类型  必填
        public String cardCode;//证件号  必填
        public String phone;//手机号  必填
        //以下数据 按业务选填
        public String occupation;//职业
        public String birthday;//生日 时间戳
        public String sex;//性别 1 男 2 女
        public String age;//年龄
        public String email;//邮箱
        public String nationality;//国籍
        public String annualIncome;//年收入
        public String height;//身高
        public String weight;//体重
        public String area;//地区
        public String address;//详细地址
    }

    public static class Beneficiary {
        public String relationName;//本人",//与投保人关系 必填
        public String name;//姓名", //必填
        public String cardType;//证件类型  必填
        public String cardCode;//证件号  必填
        public String phone;//手机号  必填
        //以下数据 按业务选填
        public String occupation;//职业
        public String birthday;//生日 时间戳
        public String sex;//性别 1 男 2 女
        public String age;//年龄
        public String email;//邮箱
        public String nationality;//国籍
        public String annualIncome;//年收入
        public String height;//身高
        public String weight;//体重
        public String area;//地区
        public String address;//详细地址
    }

}
