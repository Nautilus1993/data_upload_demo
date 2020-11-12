package com.bjsasc.FTPClientSDK.model.dto;


import com.bjsasc.FTPClientSDK.model.entry.Result;
import lombok.Data;

import java.util.Map;

//初始化上传任务后，查询用户临时目录，和生成的上传文件列表，用于存储在redis里
@Data
public class UploadResponseDTO {
    //临时目录
    private String tempDir;
    //本次上传任务ID
    private String taskId;
    //上传年份
    private String year;
    //文件名称，文件ID
    private Map<String, String> files;

    private Result result;
}
