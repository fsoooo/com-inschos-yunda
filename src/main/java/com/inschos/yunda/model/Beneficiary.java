package com.inschos.yunda.model;

public class Beneficiary {
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
