package com.bjsasc.FTPClientSDK.model.entry;

import lombok.Data;

/**
 * FTP 云存储文件
 */
@Data
public class FTPFile {

    /**
     * 文件ID
     */
    private String id;
    /**
     * 文件名称
     */
    private String name;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 文件所在年
     */
    private String year;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if(path.endsWith("/"))
            path = path.substring(0,path.lastIndexOf("/"));
        else if(path.endsWith("\\"))
            path = path.substring(0,path.lastIndexOf("\\"));
        this.path = path;
    }
    public FTPFile(){}
    public FTPFile(String name, String path){
        this.name = name;
        this.path = path;
    }
}
