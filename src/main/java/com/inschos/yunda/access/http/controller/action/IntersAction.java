package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.*;
import com.inschos.yunda.data.dao.*;
import com.inschos.yunda.extend.inters.ExtendInsurePolicy;
import com.inschos.yunda.extend.inters.InsureHttpRequest;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.inschos.yunda.extend.inters.InsureCommon.*;

@Component
public class IntersAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(IntersAction.class);

    @Autowired
    private JointLoginDao jointLoginDao;

    private String login_url = "http://122.14.202.146:9200/account";//账号服务接口地址

    private String prepare_url = "http://122.14.202.146:9200/trading/insure";//预投保接口地址

    private String insure_url = "http://122.14.202.146:9200/trading/insure";//投保接口地址

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

        //TODO 触发联合登录,同步操作 http 请求
        try {
            L.log.debug("=============================Request================================");
            L.log.debug(JsonKit.bean2Json(request));
            //TODO http 请求
            String result = HttpClientKit.post(insure_url, JsonKit.bean2Json(request));
            L.log.debug("=============================Response================================");
            L.log.debug(result);
            if(request!=null){
                response = null;
            }else{
                response = null;
            }
            if (response != null) {
                return json(BaseResponse.CODE_SUCCESS, "接口请求成功", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, "接口请求失败", response);
            }
        } catch (IOException e) {
            return json(BaseResponse.CODE_FAILURE, "接口请求失败", response);
        }
//        long date = new Date().getTime();
//        JointLogin jointLogin = new JointLogin();
//        jointLogin.login_start = date;
//        jointLogin.phone = request.insured_phone;
//        int login_id = jointLoginDao.addLoginRecord(jointLogin);
//        if (login_id == 0) {
//            return json(BaseResponse.CODE_FAILURE, "操作失败", response);
//        } else {
//            return json(BaseResponse.CODE_SUCCESS, "操作成功", response);
//        }
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
        BaseResponse response = new BaseResponse();
        return json(BaseResponse.CODE_FAILURE, "操作失败", response);
    }

    /**
     * 预投保
     *
     * @return json
     * @params actionBean
     * @access public
     */
    public String prepareInusre(HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();
        return json(BaseResponse.CODE_FAILURE, "操作失败", response);
    }

    /**
     * 调用账号服务
     * @param request
     * @return
     */
    private String doAccount(JointLoginBean request){
        return "";
    }

    /**
     * 同步执行投保
     * @return
     */
    private String doInsuredSync(JointLoginBean request){
        BaseResponse response = new BaseResponse();
        ExtendInsurePolicy.GetInusredRequest insuredRequest = new ExtendInsurePolicy.GetInusredRequest();
        insuredRequest.productId = 90;
        insuredRequest.startTime = "";
        insuredRequest.endTime = "";
        insuredRequest.count = "";
        insuredRequest.businessNo = "";
        insuredRequest.payCategoryId = "";
        insuredRequest.businessNo = "";
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
        //被保人
        ExtendInsurePolicy.Recognizee recognizee = new ExtendInsurePolicy.Recognizee();
        List<ExtendInsurePolicy.Recognizee> recognizees = new ArrayList<>();
        //受益人
        ExtendInsurePolicy.Beneficiary beneficiary = new ExtendInsurePolicy.Beneficiary();
        insuredRequest.policyholder = policyHolder;
        insuredRequest.recognizees = recognizees;
        insuredRequest.beneficiary = beneficiary;

        ExtendInsurePolicy.GetInsuredesponse result = new InsureHttpRequest<>(toInsured, insuredRequest, ExtendInsurePolicy.GetInsuredesponse.class).post();
        if (result == null) {
            return json(BaseResponse.CODE_FAILURE, "接口请求失败", response);
        } else {
            return json(BaseResponse.CODE_FAILURE, "接口请求失败", response);
        }
    }

    /**
     * 异步执行投保
     * @return
     */
    private String doInsuredAsync(JointLoginBean request){
        return "";
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
}
