package com.inschos.yunda.aop;

import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.access.http.controller.bean.BaseRequestBean;
import com.inschos.yunda.access.http.controller.bean.BaseResponseBean;
import com.inschos.yunda.access.rpc.bean.AccountBean;
import com.inschos.yunda.access.rpc.client.AccountClient;
import com.inschos.yunda.annotation.CheckParamsKit;
import com.inschos.yunda.annotation.GetActionBeanAnnotation;
import com.inschos.yunda.assist.kit.ConstantKit;
import com.inschos.yunda.assist.kit.HttpKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.assist.kit.StringKit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Component
@Aspect
public class HttpAopDefine {

    @Autowired
    private AccountClient accountClient;

    @Around("@annotation(com.inschos.yunda.annotation.GetActionBeanAnnotation)")
    public Object checkAuth(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if (request != null) {
            BaseResponseBean response = new BaseResponseBean();

            String buildCode = request.getParameter(BaseRequestBean.FILEID_BUILDCODE);
            String platform = request.getParameter(BaseRequestBean.FILEID_PLATFORM);
            String apiCode = request.getParameter(BaseRequestBean.FILEID_APICODE);

            if (ConstantKit.IS_PRODUCT && !isValidVersion(buildCode, platform)) {
                response.code = BaseResponseBean.CODE_VERSION_FAILURE;
                CheckParamsKit.Entry<String, String> entry = CheckParamsKit.getDefaultEntry();
                entry.details = "版本过低或无效，请安装最新版本";
                List<CheckParamsKit.Entry<String, String>> list = new ArrayList<>();
                list.add(entry);
                response.message = list;
                return JsonKit.bean2Json(response);
            }

            String accessToken = request.getParameter(BaseRequestBean.FILEID_ACCESS_TOKEN);
//			B.log.info("accessToken:{}", accessToken);

            ActionBean bean = new ActionBean();
            bean.token = accessToken;

            AccountBean accountBean = accountClient.getAccount(accessToken);

            if (!isAccess(joinPoint, bean,accountBean)) {
                response.code = BaseResponseBean.CODE_ACCESS_FAILURE;
                CheckParamsKit.Entry<String, String> entry = CheckParamsKit.getDefaultEntry();
                entry.details = "未登录";
                List<CheckParamsKit.Entry<String, String>> list = new ArrayList<>();
                list.add(entry);
                response.message = list;
                return JsonKit.bean2Json(response);
            }
            if (StringKit.isInteger(buildCode)) {
                bean.buildCode = Integer.valueOf(buildCode);
            }
            if (StringKit.isInteger(apiCode)) {
                bean.apiCode = Integer.valueOf(apiCode);
            }
            bean.platform = platform;
            bean.url = request.getRequestURL().toString();

            bean.body = HttpKit.readRequestBody(request);
            if (StringKit.isEmpty(bean.body)) {
                bean.body = "{}";
            }

            Object[] params = new Object[]{bean};
            Object returnValue = joinPoint.proceed(params);
            return returnValue;
        } else {
            return joinPoint.proceed();
        }
    }

    private boolean isValidVersion(String buildCode, String platform) {
        if (StringKit.isEmpty(buildCode)) {
            return false;
        }
        return true;
    }

    private boolean isAccess(ProceedingJoinPoint joinPoint, ActionBean bean,AccountBean accountBean) {
        boolean isAuthCheck = true;

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        if (signature != null) {
            Method method = signature.getMethod();
            if (method != null && method.isAnnotationPresent(GetActionBeanAnnotation.class)) {
                GetActionBeanAnnotation annotation = signature.getMethod().getAnnotation(GetActionBeanAnnotation.class);
                isAuthCheck = annotation.isCheckAccess();
            }
        }

        if(isAuthCheck){
            if(accountBean!=null){
                bean.managerUuid = accountBean.managerUuid;
                bean.accountUuid = accountBean.accountUuid;
                bean.userId = accountBean.userId;
                bean.userType = accountBean.userType;
                bean.username = accountBean.username;
                bean.email = accountBean.email;
                bean.phone = accountBean.phone;
                bean.sysId = accountBean.sysId;
            }else{
                return false;
            }
        }


        return true;
    }
}
