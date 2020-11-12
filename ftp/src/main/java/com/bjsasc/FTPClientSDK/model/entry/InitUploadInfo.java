package com.bjsasc.FTPClientSDK.model.entry;

import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class InitUploadInfo {
    //FTP 目录
    FTPDir targetDir;
    //上传文件列表
    Map<String, String> fileIds;
    //初始化失败文件名称列表
    List<String> failFiles;
    //上传年份
    private String year;
}
