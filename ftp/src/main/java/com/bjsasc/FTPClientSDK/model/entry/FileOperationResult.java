package com.bjsasc.FTPClientSDK.model.entry;

import com.bjsasc.FTPClientSDK.model.enu.FileOperationStatus;
import lombok.Data;

import java.util.*;

/**
 * 文件操作结果
 */
@Data
public class FileOperationResult {
    /**
     * 操作状态返回
     */
    public FileOperationStatus status;
    /**
     * 操作失败文件列表返回
     */
    public List<String> failFiles = new ArrayList<>();
    /**
     * 数据大类
     */
    public String mainType;
    /**
     * 数据小类
     */
    public String subType;
    /**
     * 数据来源-文件中心
     */
    public String dataSource;
    /**
     * 数据量-文件个数
     */
    public Integer dataCount;
    /**
     * 开始接收时间
     */
    public Date acceptDateStart;
    /**
     * 结束接收时间
     */
    public Date acceptDateEnd;
    /**
     * 数据操作结果
     */
    public Map<String, FileResult> fileResults = new HashMap<>();
}
