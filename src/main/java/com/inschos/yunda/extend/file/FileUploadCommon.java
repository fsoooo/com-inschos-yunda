package com.inschos.yunda.extend.file;

public class FileUploadCommon {

    private static final String SERVER_HOST = "http://59.110.136.249:9200";

    public static final String upload_by_base64 = getServerHost() + "/file/upBase";

    public static final String get_file_url = getServerHost() + "/file/getFileUrl";

    public static String getServerHost() {
        return SERVER_HOST;
    }
}
