package com.bjsasc.FTPClientSDK.model.entry;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
/**
 * FTPDir 云存储目录信息
 */
public class FTPDir {
    /**
     * 目录ID
     */
    String id;
    /**
     * 目录名称或路径信息
     */
    String name;
}
