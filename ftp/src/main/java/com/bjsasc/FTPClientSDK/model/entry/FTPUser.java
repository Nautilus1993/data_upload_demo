package com.bjsasc.FTPClientSDK.model.entry;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * 云存储FTP用户信息
 */
@Slf4j
public class FTPUser {
    /**
     * ftp用户名
     */
    private String author;
    /**
     * ftp 用户密码
     */
    private String security;

    /**
     * ftp IP地址
     */
    private String hostname;

    /**
     * ftp 域名
     */
    private String domain;
    /**
     * ftp 端口号
     */
    private int port;

    public String getAuthor() {
        //把字符串转为字节数组
        byte[] b = author.getBytes();
        //遍历
        for (int i = 0; i < b.length; i++) {
            b[i] -= PortalUserInfo.getBufferOffsetOne();
        }
        return new String(b);
    }

    public void setAuthor(String author) {
        //把字符串转为字节数组
        byte[] b = author.getBytes();
        //遍历
        for (int i = 0; i < b.length; i++) {
            b[i] += PortalUserInfo.getBufferOffsetOne();
        }
        this.author = new String(b);
    }

    public String getSecurity() {
        String se = null;
        try{
            //把字符串转为字节数组
            byte[] b = security.getBytes("GBK");
            //遍历
            for (int i = 0; i < b.length; i++) {
                b[i] -= PortalUserInfo.getBufferOffsetTwo();
            }
            se = new String(b,"GBK");
        }catch (Exception e){
            log.error("获取密码出错！");
        }

        return se;
    }

    public void setSecurity(String security) {
        String se = null;
        try{
            //把字符串转为字节数组
            byte[] b = security.getBytes("GBK");
            //遍历
            for (int i = 0; i < b.length; i++) {
                b[i] += PortalUserInfo.getBufferOffsetTwo();
            }
            this.security = new String(b,"GBK");
        }catch (Exception e){
            log.error("获取密码出错！");
        }
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
