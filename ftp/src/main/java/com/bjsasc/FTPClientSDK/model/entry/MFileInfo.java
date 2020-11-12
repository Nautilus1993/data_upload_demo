package com.bjsasc.FTPClientSDK.model.entry;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
/**
 * 操作文件监控信息
 */
public class MFileInfo {

    /**
     * 文件目前大小百分比
     */
    String percentage;

    /**
     * 文件名称
     */
    String fileName;

    /**
     * 文件大小
     */
    Long fileSize;

    /**
     * 文件操作结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date operationEndTime;

    /**
     * 文件目前大小
     */
    Long currentSize;

    /**
     * 文件开始操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date operationStartTime;

    /**
     * 文件操作状态
     */
    String operationStatus;

    /*
     * 速度
     * */
    private String speed;

}