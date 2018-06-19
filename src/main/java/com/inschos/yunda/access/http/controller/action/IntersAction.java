package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class IntersAction extends BaseAction {
    private static final Logger logger = Logger.getLogger(IntersAction.class);

    /**
     * 联合登录
     *
     * @return json
     * @params actionBean
     * @access public
     */
    public String jointLogin(ActionBean actionBean) {
        BaseResponse response = new BaseResponse();
        return json(BaseResponse.CODE_FAILURE, "操作失败", response);
    }

    /**
     * 授权查询
     *
     * @return json
     * @params actionBean
     * @access public
     */
    public String authorizationQuery(ActionBean actionBean) {
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
    public String prepareInusre(ActionBean actionBean) {
        BaseResponse response = new BaseResponse();
        return json(BaseResponse.CODE_FAILURE, "操作失败", response);
    }
}
