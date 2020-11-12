package com.bjsasc.FTPClientSDK.model.enu;
/**
 * 上传状态
 */
public enum UploadStatus {
    /**
     * 服务器相应目录创建失败
     */
    Create_Directory_Fail("服务器相应目录创建失败"),
    /**
     * 服务器创建目录成功
     */
    Create_Directory_Success("服务器创建目录成功"),
    /**
     * 上传新文件成功
     */
    Upload_New_File_Success("上传新文件成功"),
    /**
     * 上传新文件失败
     */
    Upload_New_File_Failed("上传新文件失败"),
    /**
     * 文件已经存在，文件重复性校验失败
     */
    File_Exits("文件已经存在，文件重复性校验失败"),
    /**
     * 文件大小完整性校验失败
     */
    Check_File_Size_Failed("文件大小完整性校验失败"),
    /**
     * 断点续传成功
     */
    Upload_From_Break_Success("断点续传成功"),
    /**
     * 断点续传失败
     */
    Upload_From_Break_Failed("断点续传失败"),

    /**
     * 连接数据传输管理服务失败
     */
    Manager_Connect_Failed("连接数据传输管理服务失败"),
    /**
     * 删除文件失败
     */
    Delete_Remote_Faild("删除文件失败"),
    /**
     * 上传任务成功
     */
    Upload_Task_Success("上传任务成功"),
    /**
     * 上传任务失败
     */
    Upload_Task_Failed("上传任务失败"),
    /**
     * 获取文件路径失败
     */
    Get_File_TargetDir_Failed("获取文件路径失败"),
    /**
     * 文件规范性校验失败
     */
    Check_File_Standard_Failed("文件规范性校验失败");

    /**
     * 描述信息
     */
    private final String message;
    /**
     * 构造方法
     * @param message 状态信息
     */
    UploadStatus( String message){
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
