package com.bjsasc.FTPClientSDK.model.entry.HTY;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 航天医学实验数据文件表
 */
@Data
public class MedicalTestFile extends BaseFile {


    /**
     * 项目ID
     */
    private Short itemId;

    /**
     * 设备ID
     */
    private Short deviceId;

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