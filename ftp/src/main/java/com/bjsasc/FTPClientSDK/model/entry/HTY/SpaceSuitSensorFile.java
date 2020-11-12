package com.bjsasc.FTPClientSDK.model.entry.HTY;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 舱外服上注传感器系数数据文件
 */
@Data
public class SpaceSuitSensorFile extends BaseFile {

    /**
     * 文件创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fileTime;

    /**
     * 文件名
     */
    private String fileFullName;

   }