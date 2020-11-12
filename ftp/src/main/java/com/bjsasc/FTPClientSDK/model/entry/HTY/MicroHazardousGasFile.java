package com.bjsasc.FTPClientSDK.model.entry.HTY;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
/**
 * 微量气体检测装备数据文件
 */ public class MicroHazardousGasFile extends BaseFile {

    /**
     * 文件创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fileTime;

    /**
     * 文件名
     */
    private String fileFullName;

    /**
     * 所属飞行器
     */
    private String cabin;

    /**
     * 所属飞行阶段
     */
    private String flyStage;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件标识
     */
    private String indentity;

    /**
     * 备注
     */
    private String comment;

 }