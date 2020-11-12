package com.bjsasc.FTPClientSDK.model.entry.HTY;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
/**
 * 数据成果
 */
public class DataResultAchieveReportFile extends BaseFile {

    /**
     * 任务代号
     */
    private String mission;

    /**
     * 数据成果类型
     */
    private String achieveKind;

    /**
     * 数据成果名称
     */
    private String achieveName;

    /**
     * 数据成果来源
     */
    private String achieveFrom;

    /**
     * 数据成果版权方
     */
    private String achieveCopyright;

    /**
     * 创建人
     */
    private String founder;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date founderTime;

    /**
     * 成果描述
     */
    private String descOfAchievs;

    /**
     * 数据成果格式
     */
    private String format;

}