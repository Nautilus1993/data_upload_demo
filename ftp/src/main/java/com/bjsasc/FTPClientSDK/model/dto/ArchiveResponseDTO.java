package com.bjsasc.FTPClientSDK.model.dto;


import com.bjsasc.FTPClientSDK.model.entry.Result;
import lombok.Data;

import java.util.Map;

/**
 * 文件归档数据传输对象
 */
@Data
public class ArchiveResponseDTO {
    //文件当前目录
    private String curDir;
    //文件归档目录
    private String archiveDir;
    //本次上传任务ID
    private String taskId;
    //文件名称，文件ID
    private Map<String, String> files;

    private Result result;

}
