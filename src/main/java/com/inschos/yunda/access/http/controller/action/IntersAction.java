package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.HttpKit;
import com.inschos.yunda.data.dao.JointLoginDao;
import com.inschos.yunda.model.JointLogin;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.inschos.yunda.assist.kit.JsonKit;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class IntersAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(IntersAction.class);

    @Autowired
    private JointLoginDao jointLoginDao;

    /**
     * 联合登录
     *
     * @return json
     * @params actionBean
     * @access public
     */
    public String jointLogin(HttpServletRequest httpServletRequest) {
        JointLoginBean request = JsonKit.json2Bean(HttpKit.readRequestBody(httpServletRequest), JointLoginBean.class);
        BaseResponse response = new BaseResponse();
        //判空
        if (request == null) {
            return json(BaseResponse.CODE_FAILURE, "params is empty", response);
        }
        if (request.insured_name.isEmpty() || request.insured_code.isEmpty() || request.insured_phone == 0) {
            return json(BaseResponse.CODE_FAILURE, "insured_name or insured_code or insured_phone is empty", response);
        }
        long date = new Date().getTime();
        JointLogin jointLogin = new JointLogin();
        jointLogin.login_start = date;
        jointLogin.phone = request.insured_phone;
        int login_id = jointLoginDao.addLoginRecord(jointLogin);
        if (login_id == 0) {
            return json(BaseResponse.CODE_FAILURE, "操作失败", response);
        } else {
            return json(BaseResponse.CODE_SUCCESS, "操作成功", response);
        }
    }

    /**
     * 授权查询
     *
     * @return json
     * @params actionBean
     * @access public
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
     * 生成token
     *
     * @param insured_name
     * @param insured_code
     * @param insured_phone
     * @return json
     */
    private String LoginToken(String insured_name, String insured_code, long insured_phone) {
        BaseResponse response = new BaseResponse();
        if (insured_name == null || insured_code == null || insured_phone == 0) {
            return json(BaseResponse.CODE_FAILURE, "insured_name or inusred_code or insured_phone is empty", response);
        }
        return json(BaseResponse.CODE_SUCCESS, "操作成功", response);
    }
}
