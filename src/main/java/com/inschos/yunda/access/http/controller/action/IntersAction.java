package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.*;
import com.inschos.yunda.data.dao.*;
import com.inschos.yunda.model.*;
import com.inschos.yunda.extend.inters.ExtendInsurePolicy;
import com.inschos.yunda.extend.inters.InsureHttpRequest;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.inschos.yunda.extend.inters.InsureCommon.*;

@Component
public class IntersAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(IntersAction.class);

    @Autowired
    private JointLoginDao jointLoginDao;

    private String p_code_yd = "90";//英大产品id

    private String p_code_tk = "91";//泰康产品id

    /**
     * 联合登录
     *
     * @return json
     * @params actionBean
     * @access public
     * <p>
     * 韵达触发联合登录，获取报文信息请求账号服务(登录/注册)，获取account_id,manager_id,user_id,token
     */
    public String jointLogin(HttpServletRequest httpServletRequest) {
        JointLoginBean request = JsonKit.json2Bean(HttpKit.readRequestBody(httpServletRequest), JointLoginBean.class);
        BaseResponse response = new BaseResponse();
        //判空
        if (request == null) {
            return json(BaseResponse.CODE_FAILURE, "请检查报文格式是否正确", response);
        }
        if (request.insured_name== null || request.insured_code== null || request.insured_phone== null) {
            return json(BaseResponse.CODE_FAILURE, "姓名,证件号,手机号不能为空", response);
        }
        //TODO 联合登录表插入数据,先判断今天有没有插入,再插入登录记录.每天只有一个记录
        long date = new Date().getTime();
        Calendar calendar = Calendar.getInstance();
        long date_start = calendar.getTimeInMillis();
        JointLogin jointLogin = new JointLogin();
        jointLogin.login_start = date;
        jointLogin.phone = request.insured_phone;
        jointLogin.day_start = date_start;
        jointLogin.day_end = date_start+24*60*60*1000;
        long repeatRes = jointLoginDao.findLoginRecord(jointLogin);
        if(repeatRes==0){
            long login_id = jointLoginDao.addLoginRecord(jointLogin);
        }
        //TODO 触发联合登录,同步操作 http 请求 账号服务
        String loginRes = doAccount(request);
        if(loginRes==null){
            return json(BaseResponse.CODE_FAILURE, "账号服务调用失败", response);
        }
        IntersResponse loginResponse = JsonKit.json2Bean(loginRes, IntersResponse.class);
        if(loginResponse==null){
            return json(BaseResponse.CODE_FAILURE, "账号服务调用失败", response);
        }
        if(loginResponse.code!="200"){
            return json(BaseResponse.CODE_FAILURE, "账号服务调用失败", response);
        }
        String insureRes = doInsured(request);
        if(insureRes==null){
            return json(BaseResponse.CODE_FAILURE, "投保接口调用失败", response);
        }
        IntersResponse insureResponse = JsonKit.json2Bean(insureRes, IntersResponse.class);
        if(insureResponse==null){
            return json(BaseResponse.CODE_FAILURE, "投保接口调用失败", response);
        }
        if(insureResponse.code!="200"){
            return json(BaseResponse.CODE_FAILURE, "投保接口调用失败", response);
        }
        return json(BaseResponse.CODE_SUCCESS, "操作成功", response);
    }

    /**
     * 授权查询
     *
     * @return json
     * @params actionBean
     * @access public
     * 授权查询同样需要触发联合登录
     */
    public String authorizationQuery(HttpServletRequest httpServletRequest) {
        JointLoginBean request = JsonKit.json2Bean(HttpKit.readRequestBody(httpServletRequest), JointLoginBean.class);
        BaseResponse response = new BaseResponse();
        //判空
        if (request == null) {
            return json(BaseResponse.CODE_FAILURE, "请检查报文格式是否正确", response);
        }
        if (request.insured_name== null || request.insured_code== null || request.insured_phone== null) {
            return json(BaseResponse.CODE_FAILURE, "姓名,证件号,手机号不能为空", response);
        }
        //TODO 触发联合登录,同步操作 http 请求 账号服务
        String loginRes = doAccount(request);
        if(loginRes==null){
            return json(BaseResponse.CODE_FAILURE, "账号服务调用失败", response);
        }
        IntersResponse loginResponse = JsonKit.json2Bean(loginRes, IntersResponse.class);
        if(loginResponse==null){
            return json(BaseResponse.CODE_FAILURE, "账号服务调用失败", response);
        }
        if(loginResponse.code!="200"){
            return json(BaseResponse.CODE_FAILURE, "账号服务调用失败", response);
        }
        return json(BaseResponse.CODE_FAILURE, "操作失败", response);
    }

    /**
     * 调用账号服务
     * @param request
     * @return
     */
    private String doAccount(JointLoginBean request){
        BaseResponse response = new BaseResponse();
        ExtendInsurePolicy.GetAccountLogin accountLoginRequest = new ExtendInsurePolicy.GetAccountLogin();
        accountLoginRequest.channel_code = request.channel_code;
        accountLoginRequest.insured_name = request.insured_name;
        accountLoginRequest.insured_code = request.insured_code;
        accountLoginRequest.insured_phone = request.insured_phone;
        accountLoginRequest.insured_email = request.insured_email;
        accountLoginRequest.insured_province = request.insured_province;
        accountLoginRequest.insured_city = request.insured_city;
        accountLoginRequest.insured_county = request.insured_county;
        accountLoginRequest.insured_address = request.insured_address;
        accountLoginRequest.bank_name = request.bank_name;
        accountLoginRequest.bank_code = request.bank_code;
        accountLoginRequest.bank_phone = request.bank_phone;
        accountLoginRequest.bank_address = request.bank_address;
        accountLoginRequest.channel_order_code = request.channel_order_code;
        ExtendInsurePolicy.GetInsuredesponse result = new InsureHttpRequest<>(toJointLogin, accountLoginRequest, ExtendInsurePolicy.GetInsuredesponse.class).post();
        if (result == null) {
            return json(BaseResponse.CODE_FAILURE, "接口请求失败", response);
        } else {
            return json(BaseResponse.CODE_FAILURE, "接口请求失败", response);
        }
    }

    /**
     * 投保操作，先走英大投保，再进行泰康投保
     * @return
     */
    private String doInsured(JointLoginBean request){
        BaseResponse response = new BaseResponse();
        //投保人基础信息
        ExtendInsurePolicy.PolicyHolder policyHolder = new ExtendInsurePolicy.PolicyHolder();
        policyHolder.name = request.insured_name;
        policyHolder.cardType = "1";
        policyHolder.cardCode = request.insured_code;
        policyHolder.phone = request.insured_phone;
        policyHolder.relationName = "本人";
        //以下数据 按业务选填
        policyHolder.occupation = "";//职业
        policyHolder.birthday = "";//生日 时间戳
        policyHolder.sex = "";//性别 1 男 2 女
        policyHolder.age = "";//年龄
        policyHolder.email = "";//邮箱
        policyHolder.nationality = "";//国籍
        policyHolder.annualIncome = "";//年收入
        policyHolder.height = "";//身高
        policyHolder.weight = "";//体重
        policyHolder.area = "";//地区
        policyHolder.address = "";//详细地址
        policyHolder.courier_state = "";//站点地址
        policyHolder.courier_start_time = "";//分拣开始时间
        policyHolder.province = "";//省
        policyHolder.city = "";//市
        policyHolder.county = "";//县
        policyHolder.bank_code = "";//银行卡号
        policyHolder.bank_name = "";//银行卡名字
        policyHolder.bank_phone = "";//银行卡绑定手机
        policyHolder.insure_days = "";//购保天数
        policyHolder.price = "";//价格
        //TODO 投保人被保人和受益人是本人
        List<ExtendInsurePolicy.PolicyHolder> recognizees = new ArrayList<>();
        recognizees.add(policyHolder);
        ExtendInsurePolicy.GetInusredRequest insuredRequest = new ExtendInsurePolicy.GetInusredRequest();
        insuredRequest.productId = 90;
        insuredRequest.startTime = "";
        insuredRequest.endTime = "";
        insuredRequest.count = "";
        insuredRequest.businessNo = "";
        insuredRequest.payCategoryId = "";
        insuredRequest.businessNo = "";
        insuredRequest.policyholder = policyHolder;
        insuredRequest.recognizees = recognizees;
        insuredRequest.beneficiary = policyHolder;
        ExtendInsurePolicy.GetInsuredesponse result = new InsureHttpRequest<>(toInsured, insuredRequest, ExtendInsurePolicy.GetInsuredesponse.class).post();
        if (result == null) {
            return json(BaseResponse.CODE_FAILURE, "接口请求失败", response);
        } else {
            return json(BaseResponse.CODE_FAILURE, "接口请求失败", response);
        }
    }

    /**
     * 调用微信签约
     * @param request
     * @return
     */
    private String doWechatContract(JointLoginBean request){
        return "";
    }

    /**
     * 调用微信代扣
     * @param request
     * @return
     */
    private String doWechatPay(JointLoginBean request){
        return "";
    }

    /**
     * 获取授权详情(微信+银行卡)
     * @return
     */
    private String doAuthorizeRes(){
        return "";
    }
}
