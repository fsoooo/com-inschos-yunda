package com.inschos.yunda.access.http.controller.action;


import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.ListKit;
import com.inschos.yunda.assist.kit.TimeKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MsgIndexAction extends BaseAction {
    private static final Logger logger = Logger.getLogger(MsgIndexAction.class);

    /**
     * test
     *
     * @params actionBean
     * @return json
     * @access public
     *
     */
    public String addMessage(ActionBean actionBean){
        BaseResponse response = new BaseResponse();
        return json(BaseResponse.CODE_FAILURE, "操作失败", response);
    }
}
