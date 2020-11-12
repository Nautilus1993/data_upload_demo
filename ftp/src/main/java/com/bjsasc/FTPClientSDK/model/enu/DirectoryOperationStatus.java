package com.bjsasc.FTPClientSDK.model.enu;

/**
 * 目录操作状态
 */
public enum DirectoryOperationStatus {

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 对象创建成功
     */
    CREATED(201, "创建成功"),

    /**
     * 请求已经被接受
     */
    ACCEPTED(202, "请求被接受"),

    /**
     * 操作已经执行成功，但是没有返回数据
     */
    NO_CONTENT(204, "执行成功，没有返回数据"),

    /**
     * 资源已被移除
     */
    MOVED_PERM(301, "资源被移除"),

    /**
     * 重定向
     */
    REDIRECT(303, "重定向"),

    /**
     * 资源没有被修改
     */
    NOT_MODIFIED(304, "资源没有被修改"),

    /**
     * 参数列表错误（缺少，格式不匹配）
     */
    BAD_REQUEST(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 访问受限，授权过期
     */
    FORBIDDEN(403, "访问受限，授权过期"),

    /**
     * 资源，服务未找到
     */
    NOT_FOUND(404, "未找到"),

    /**
     * 不允许的http方法
     */
    BAD_METHOD(405, "不允许的http方法"),

    /**
     * 资源冲突，或者资源被锁
     */
    CONFLICT(409, "资源冲突，或者资源被锁"),

    /**
     * 目录下有文件，不能删除
     */
    CONFLICT_DEL(410, "目录下有文件，不能删除"),



    /**
     * 不支持的数据，媒体类型
     */
    UNSUPPORTED_TYPE(415, "不支持的数据，媒体类型"),

    /**
     * 系统内部错误
     */
    ERROR(500, "系统内部错误"),

    /**
     * 接口未实现
     */
    NOT_IMPLEMENTED(501, "接口未实现"),


    /**
     * 删除目录失败
     */
    DELDIR_ERROR(504, "删除目录失败"),

    /**
     * 创建目录失败
     */
    NEWDIR_ERROR(505, "创建目录失败"),

    /**
     * 查询目录列表失败
     */
    LISTDIR_ERROR(506, "查询目录列表失败"),

    /**
     * 记录保存失败
     */
    RECORDSAVE_ERROR(512, "记录保存失败");

    /**
     * 目录操作状态码
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
    DirectoryOperationStatus(int code, String message){
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
