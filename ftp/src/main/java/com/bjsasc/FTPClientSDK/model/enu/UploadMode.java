package com.bjsasc.FTPClientSDK.model.enu;

/**
 * 上传模式 - HTTP/FTP
 */
public enum UploadMode {
    /**
     * 未归档
     */
    MODE_HTTP(0, "PORTAL"),
    /**
     * 归档中
     */
    MODE_FTP(1, "API");
    /**
     * 归档状态码
     */
    private final int code;
    /**
     * 描述信息
     */
    private final String message;
    /**
     * 构造方法
     * @param code 状态码
     * @param message 状态信息
     */
    UploadMode(int code, String message){
        this.code = code;
        this.message = message;
    }

    /**
     * 返回归档状态码
     * @return code上传状态码
     */
    public int code() {
        return this.code;
    }

    /**
     * 返回归档状态信息
     * @return message状态信息
     */
    public String message() {
        return this.message;
    }
}
