package com.bjsasc.FTPClientSDK.model.dto;

import lombok.Data;


/**
 * 文件上传时，请求的文件信息
 */
@Data
public class FileInfoDTO {
    private String directoryId;//目录id
    private String fileName;  //文件名
    private Long fileSize;    //文件总大小
    private Long fileLastUpdateTime;  //文件的最后修改时间
}
