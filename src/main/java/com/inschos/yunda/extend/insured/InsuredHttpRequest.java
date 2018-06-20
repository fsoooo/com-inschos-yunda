package com.inschos.yunda.extend.insured;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inschos.yunda.assist.kit.HttpClientKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.assist.kit.L;
import com.inschos.yunda.assist.kit.StringKit;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InsuredHttpRequest<Request extends InsuredHttpRequest, Response extends InsuredResponse> {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private InsuredRequestEntity request;
    private Class<Response> cls;
    private String url;

    public InsuredHttpRequest(String url, Request request, Class<Response> cls) {
        this.url = url;
        this.request = new InsuredRequestEntity<Request>();
        this.cls = cls;
        if (request != null) {
            this.request.data = request;
            this.request.sign = SignatureTools.sign(JsonKit.bean2Json(request), SignatureTools.RSA_PRIVATE_KEY);
        }

        this.request.sendTime = sdf.format(new Date(System.currentTimeMillis()));
    }

    public Response post() {
        Response response;
        try {
            L.log.debug("=============================================================================================================================");
            L.log.debug(JsonKit.bean2Json(request));
            String result = HttpClientKit.post(url, JsonKit.bean2Json(request));
            L.log.debug("=============================================================================================================================");
            L.log.debug(result);

            response = JsonKit.json2Bean(result, cls);

            if (response != null) {
                response.verify = verifySignature(result);
            } else {
                try {
                    response = cls.newInstance();
                    response.state = InsuredResponse.RESULT_FAIL;
                    response.msg = "请求失败";
                    response.verify = false;
                } catch (InstantiationException | IllegalAccessException e) {
                    // e.printStackTrace();
                    return null;
                }
            }
            return response;
        } catch (IOException e) {
            // e.printStackTrace();
            try {
                response = cls.newInstance();
                response.state = InsuredResponse.RESULT_FAIL;
                response.msg = "请求失败";
                response.verify = false;
                return response;
            } catch (InstantiationException | IllegalAccessException ex) {
                // ex.printStackTrace();
                return null;
            }
        }
    }

    private boolean verifySignature(String responseJson) {
        JsonNode jsonNode = null;
        try {
            jsonNode = new ObjectMapper().readTree(responseJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean flag = false;
        if (jsonNode != null) {
            JsonNode signNode = jsonNode.get("sign");
            String sign = signNode.textValue();
            if (StringKit.isEmpty(sign)) {
                flag = true;
            } else {
                String content = jsonNode.get("data").toString();
                flag = SignatureTools.verify(content, sign, SignatureTools.RSA_PUBLIC_KEY);
            }
        }
        return flag;
    }
}
