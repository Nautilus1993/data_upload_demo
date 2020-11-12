package com.bjsasc.FTPClientSDK.model.dto;

import lombok.Data;

@Data
/**
 * 下载文件与数据传输管理的数据交互
 */
public class DownloadResponseDTO {
    /**
     * 下载ID
     */
    private String downloadInfoId;
    /**
     * 目录路径
     */
    private String dirPath;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     *文件下载时间年份
     */
    private String year;
}
