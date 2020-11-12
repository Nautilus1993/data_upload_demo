package com.bjsasc.FTPClientSDK.model.entry;


/**
 * 当前用户信息
 */
public class PortalUserInfo {
    /**
     * portal用户名
     */
     String userName;
    /**
     * portal用户密码
     */
     String password;
    /**
     * portal用户令牌
     */
     String token;

    /**
     * portal用户所属中心
     */
     String center;
    /**
     * 舱段
     */
    String cabin;
    /**
     * portal用户所属用户组
     */
    String group;
    // 定时信息-加密
    public static int BUFFER_OFFSET_ONE = 10;
    public static int BUFFER_OFFSET_TWO = 10;
//
//    /**
//     * 当前目录操作的状态
//     */
//    public FTPOperationStatus ftpOperationStatus;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCabin() {
        return cabin;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    /**
     * 构造方法
     */
    public PortalUserInfo() {

    }


    public static int getBufferOffsetOne() {
        return BUFFER_OFFSET_ONE;
    }

    public static int getBufferOffsetTwo() {
        return BUFFER_OFFSET_TWO;
    }
}
