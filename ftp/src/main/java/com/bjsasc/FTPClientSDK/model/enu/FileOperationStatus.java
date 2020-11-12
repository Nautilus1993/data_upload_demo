package com.bjsasc.FTPClientSDK.model.enu;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件操作结果
 */
public enum FileOperationStatus {
    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 访问受限，授权过期
     */
    FORBIDDEN(403, "访问受限，授权过期"),

    /**
     * 资源，服务未找到
     */
    NOT_FOUND(404, "未找到"),
    /**
     * 系统内部错误
     */
    ERROR(500, "系统内部错误"),
    /**
     * 上传失败
     */
    UPLOAD_ERROR(502, "上传失败"),

    /**
     * 下载失败
     */
    DOWNLOAD_ERROR(503, "下载失败-本地文件已存在或者网络因素等"),
    /**
     * 调用超时
     */
    ADD_DIR_TIME_OUT(505, "请求创建目录接口超时"),
    /**
     * 删除文件失败
     */
    DELFILE_ERROR(507, "删除文件失败"),

    /**
     * 归档失败
     */
    ARCHIVED_ERROR(510, "归档失败"),
    /**
     * 元信息提取异常
     */
    ERROR_META_INFO(512, "元信息提取异常,请查看元信息是否正确"),
    /**
     * 检查一致性失败
     */
    CHECK_SIZE_ERROR(513, "检查一致性失败"),
    /**
     * 检查规范性失败
     */
    CHECK_STANDARD_ERROR(514, "检查规范性失败"),
    /**
     * 检查重复性失败
     */
    CHECK_REPEATE_FILESELF_ERROR(515, "检查重复性失败-文件本身存在重复"),
    /**
     * 检查重复性失败
     */
    CHECK_REPEATE_UPLOAD_ERROR(516, "检查重复性失败-文件已经被上传"),
    /**
     * 文件被修改过
     */
    CHECK_REPEATE_FILE_MODIFY_ERROR(517, "检查重复性失败-文件被修改过"),
    /**
     * 本地文件不存在
     */
    Local_File_No_Exit(518,"本地文件不存在或者文件大小为0");
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
    FileOperationStatus(int code, String message){
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
    private static final Map<Integer, FileOperationStatus> MAP = new HashMap<>();

    static {
        for (FileOperationStatus item : FileOperationStatus.values()) {
            MAP.put(item.code, item);
        }
    }
    public static FileOperationStatus getByCode(int code){
        return MAP.get(code);
    }

}
