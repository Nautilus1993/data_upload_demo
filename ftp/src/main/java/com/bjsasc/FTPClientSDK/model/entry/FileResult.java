package com.bjsasc.FTPClientSDK.model.entry;

import com.bjsasc.FTPClientSDK.model.enu.FileOperationStatus;
import lombok.Data;

@Data
/**
 * 单个文件操作结果
 */
public class FileResult {
    /**
     * 文件名
     */
    public String name;
    /**
     * 操作状态
     */
    public FileOperationStatus status;
    /**
     * 文件大小
     */
    public int size;
    /**
     * 文件原始大小
     */
    public int originalSize;

}
