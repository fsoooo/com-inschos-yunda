package com.inschos.yunda.access.http.controller.bean;


import com.inschos.yunda.assist.kit.*;

public class ActionBean {

    public String salt;

    public String url;

    public String body;

    public int buildCode;

    public String platform;

    public int apiCode=1;

    public String userId;

    // 1-个人，2-企业客户，3-业管，4-代理
    public int userType;

    public String managerUuid;

    public String accountUuid;

    public String username;

    public String phone;

    public String email;

    public long sysId;

    private final static String SALT_VALUE_TEST = "InschosTest";

    private final static String SALT_VALUE_ONLINE = "InschosOnLine";

    public int type;

    public long tokenTime;

    public String referer;

    public static final RC4Kit rc4 = new RC4Kit("Inschos@2018@verifyToken");

    public static String getSalt(String salt) {
        if (ConstantKit.IS_PRODUCT) {
            return SALT_VALUE_ONLINE+salt;
        } else {
            return SALT_VALUE_TEST+salt;
        }
    }

    public static boolean isValidSalt(String salt){
        String  prefixSalt ;
        if (ConstantKit.IS_PRODUCT) {
            prefixSalt = SALT_VALUE_ONLINE;
        } else {
            prefixSalt = SALT_VALUE_TEST;
        }
        return salt!=null&& salt.startsWith(prefixSalt);
    }


    public static String packageToken(ActionBean bean) {
        if (bean != null) {
            String token = JsonKit.bean2Json(bean);
            if (!StringKit.isEmpty(token)) {
                return rc4.encry_RC4_base64(token);
            }
        }
        return "";
    }

    public static ActionBean parseToken(String token) {
        ActionBean bean = null;
        try {
            if (!StringKit.isEmpty(token)) {
                token = token.replaceAll(" ","+");
                String tokenString = rc4.decry_RC4_base64(token);

                if (!StringKit.isEmpty(tokenString)) {
                    bean = JsonKit.json2Bean(tokenString, ActionBean.class);
                }
            }
        } catch (Exception e) {
            L.log.debug("verifyToken parse error:{}", e);
        }
        if (bean == null) {
            bean = new ActionBean();
        }
        return bean;
    }
}
