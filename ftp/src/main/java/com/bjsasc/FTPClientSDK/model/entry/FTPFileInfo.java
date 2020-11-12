package com.bjsasc.FTPClientSDK.model.entry;

import lombok.Data;

import java.util.Date;

/**
 * FTP 云存储文件 文件详情信息
 */
@Data
public class FTPFileInfo {

    /**
     * 文件名称
     */
    public String fileName;

    /**
     * 上传时间
     */
    Date uploadTime;

    /**
     * 文件大小
     */
    Long fileSize;
}
