package com.bjsasc.FTPClientSDK.model.entry.HTY;

import lombok.Data;

/**
 * 航天员上传文件基类
 */
@Data
public class BaseFile {
    /**
     * ID
     */
    private String id;

    /**
     * 序号
     */
    private String fileId;
}
