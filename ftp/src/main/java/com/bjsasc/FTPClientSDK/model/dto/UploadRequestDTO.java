package com.bjsasc.FTPClientSDK.model.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 文件上传时请求参数的DTO
 */
@Data
public class UploadRequestDTO {
    private String centerId; //数据中心
    private String mainTypeId; //数据大类
    private String subTypeId; //数据小类
    private String uploadUser; //上传用户
    private String cabinName;  //舱段名称
    private String dataDesc;   //数据描述
    private Date getTime;      //采集时间
    private String ip;         //ip
    private String userGroup;  //用户组
    private String uploadMode;   //上传方式

    //文件信息
    private List<FileInfoDTO> fileInfoDtos;
    private String token;  //用户的token信息
    List<String> labelsIds;    //标签的id集合
}
