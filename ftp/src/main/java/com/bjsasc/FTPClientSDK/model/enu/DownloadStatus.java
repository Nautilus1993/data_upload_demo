package com.bjsasc.FTPClientSDK.model.enu;

/**
 * 下载状态
 */
public enum DownloadStatus {
    /**
     * 文件不存在
     */
    File_Noexist("文件不存在"),
    /**
     * 文件不存在
     */
    Dir_Noexist("目录不存在"),
    /**
     * 文件大小完整性校验失败
     */
    Check_File_Size_Failed("文件大小完整性校验失败"),
    /**
     * 断点下载文件成功
     */
    Download_From_Break_Success("断点下载文件成功"),
    /**
     * 断点下载文件失败
     */
    Download_From_Break_Failed("断点下载文件失败"),
    /**
     * 全新下载文件成功
     */
    Download_New_Success("全新下载文件成功"),
    /**
     * 全新下载文件失败
     */
    Download_New_Failed("全新下载文件失败") ,
    /**
     * 本地文件已经存在
     */
    File_Exits("本地文件已经存在"),
    /**
     * 全新下载文件成功
     */
    Download_List_Success("批量下载文件成功"),
    /**
     * 连接数据传输管理服务失败
     */
    Manager_Connect_Failed("连接数据传输管理服务失败"),
    /**
     * 全新下载文件失败
     */
    Download_List_Failed("批量下载文件失败") ;

    /**
     * 描述信息
     */
    private final String message;
    /**
     * 构造方法
     * @param message 状态信息
     */
    DownloadStatus( String message){
        this.message = message;
    }

    /**
     * 返回归档状态信息
     * @return message状态信息
     */
    public String message() {
        return this.message;
    }
}
