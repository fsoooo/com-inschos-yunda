package com.inschos.yunda.extend.file;

import com.inschos.yunda.annotation.CheckParamsKit;
import com.inschos.yunda.assist.kit.HttpClientKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.extend.insured.CarInsuranceResponse;

import java.io.IOException;
import java.util.ArrayList;

public class FileUpload {

    private static FileUpload instance;

    private FileUpload() {

    }

    public static FileUpload getInstance() {
        if (instance == null) {
            synchronized (FileUpload.class) {
                if (instance == null) {
                    instance = new FileUpload();
                }
            }
        }
        return instance;
    }

    public static class UploadByBase64Request extends FileUploadRequest {
        public String base64;
        public String fileKey;
        public String fileName;
    }

    public FileUploadResponse uploadByBase64(UploadByBase64Request request) {

        FileUploadResponse response = new FileUploadResponse();
        if (request == null) {
            response.code = CarInsuranceResponse.RESULT_FAIL;
            response.message = new ArrayList<>();
            response.message.add(new CheckParamsKit.Entry<>("default", "请求体为null"));
            return response;
        }

        try {
            String result = HttpClientKit.post(FileUploadCommon.upload_by_base64, JsonKit.bean2Json(request));

            if (result != null) {
                response = JsonKit.json2Bean(result, FileUploadResponse.class);

                if (response == null) {
                    response = new FileUploadResponse();
                    response.code = CarInsuranceResponse.RESULT_FAIL;
                    response.message = new ArrayList<>();
                    response.message.add(new CheckParamsKit.Entry<>("default", "请求失败"));
                }

            } else {
                response.code = CarInsuranceResponse.RESULT_FAIL;
                response.message = new ArrayList<>();
                response.message.add(new CheckParamsKit.Entry<>("default", "请求失败"));
            }

        } catch (IOException e) {
            e.printStackTrace();
            response.code = CarInsuranceResponse.RESULT_FAIL;
            response.message = new ArrayList<>();
            response.message.add(new CheckParamsKit.Entry<>("default", "请求失败"));
        }

        return response;
    }

}
