package com.bjsasc.FTPClientSDK.model.enu;

/**
 * 文件状态
 */
public enum FileStatus {
    /**
     * 未上传
     */
    NO_UPLOAD(10, "未上传"),
    /**
     * 上传中
     */
    IN_UPLOAD(11, "上传中"),
    /**
     * 已上传
     */
    END_UPLOAD(12, "已上传"),

    /**
     * 上传错误
     */
    ERROR_UPLOAD(13, "上传错误"),

    /**
     * 文件删除状态-未删除
     */
    NO_DEL(0,"未删除"),
    /**
     * 文件删除状态-已删除
     */
    HAS_DEL(31,"已删除"),
    /*
     * 删除出错
     * */
    ERROR_DEL(32,"删除失败"),
    /*
    * 文件下载中
    */
    IN_DOWNLOAG(1,"下载中"),
    /*
    * 下载结束
    * */
    END_DOWNLOAD(2,"下载已结束"),
    /*
     * 下载出错
     * */
    ERROR_DOWNLOAD(3,"下载失败"),

    /**
     * 权限验证
     */
    PERMISSION_VALIDATION(-1,"权限验证");
    /**
     * 上传状态码
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
    FileStatus(int code, String message){
        this.code = code;
        this.message = message;
    }

    /**
     * 返回上传状态码
     * @return code上传状态码
     */
    public int code() {
        return this.code;
    }

    /**
     * 返回上传状态信息
     * @return message状态信息
     */
    public String message() {
        return this.message;
    }

}
