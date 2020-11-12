package com.bjsasc.FTPClientSDK.model.entry.HTY;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
/**
 * 生理数据文件
 */
public class BioMedicalFile extends BaseFile {

    /**
     * 设备ID
     */
    private Short deviceId;

    /**
     * 人员信息
     */
    private Byte humanId;

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
    private String indentify;

    /**
     * 备注
     */
    private String comment;

    }