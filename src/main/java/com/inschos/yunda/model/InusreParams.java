package com.inschos.yunda.model;

import org.omg.CORBA.PolicyHolder;

import java.util.List;

public class InusreParams {
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
