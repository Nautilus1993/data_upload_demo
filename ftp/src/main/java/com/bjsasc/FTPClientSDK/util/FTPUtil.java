package com.bjsasc.FTPClientSDK.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bjsasc.FTPClientSDK.model.entry.*;
import com.bjsasc.FTPClientSDK.model.enu.DirectoryOperationStatus;
import lombok.extern.slf4j.Slf4j;


import java.io.File;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 用户FTP操作工具类
 */
@Slf4j
public class FTPUtil {
    public static final String SEPARATOR = "/";
    /**
     * 调用权限管理模块的功能进行终端用户与存储用户之间的关系映射
     * @param portalUserInfo 当前portal 用户信息
     * @param ss FTP用户信息
     */
    public static boolean userAuthority(String basicManagerURL, FTPUser ss, PortalUserInfo portalUserInfo) {

        // 调用权限访问模块获取映射应用信息，并根据结果设置用户的存储权限状态
        // TODO 调用权限模块的用户映射功能
        //更新上传任务上传状态 //调用manager后台
        String r = HttpClientUtil.doPost(basicManagerURL + "user/FTPUser/" + portalUserInfo.getToken() + "/" + portalUserInfo.getUserName() + "/" + portalUserInfo.getCenter(), null);
        JSONObject FTPUserResult = (JSONObject) JSONObject.parse(r);
        if (Integer.parseInt(FTPUserResult.get("code").toString()) == 200) {
            try {
                JSONObject FTPUser = JSONObject.parseObject(FTPUserResult.get("data").toString());
                ss.setAuthor(FTPUser.getString("username"));
                ss.setSecurity(FTPUser.getString("password"));
                String address = FTPUser.getString("address");
                if(IsIPAddress(address)){
                    //IP 地址
                    ss.setHostname(address);
                }else {
                    //域名
                    ss.setDomain(address);
                }
                ss.setHostname(FTPUser.getString("address"));
                ss.setPort(FTPUser.getInteger("port"));
                return true;
            } catch (Exception e) {
                log.error("用户映射过程出错，错误原因", e);
                return false;
            }
        } else {
            log.error("用户映射过程出错，错误原因:获取FTP用户失败");
            return false;
        }

    }

    private static boolean IsIPAddress(String domain){
        boolean ipAddress = true;
        for(String t :domain.split("\\.")){
            try{
                int x = Integer.parseInt(t);
                if(!(x>0&&x<255))
                {
                    ipAddress = false;
                }
            }catch (Exception e){
                ipAddress = false;
            }

        }

        return ipAddress;
    }
    /**
     * 查询列表
     *
     * @param type           查询的类型
     * @param portalUserInfo 存储信息
     * @return 目录权限操作状态
     */
    public static List<FTPDir> dirList(String basicManagerURL, String type, PortalUserInfo portalUserInfo) {
        DirectoryOperationStatus fos = DirectoryOperationStatus.SUCCESS;
        //要查询的FTPDir属于哪个一级子目录
        List<FTPDir> childFTPs = new ArrayList<FTPDir>();
        String dirPath = portalUserInfo.getCenter() + File.separator + type;
        //调用manager后台 验证权限 获取目录信息
        String r = HttpClientUtil.doPost(basicManagerURL + "dir/list/" + portalUserInfo.getToken() + "/" + dirPath, null);
        JSONObject dirResult = (JSONObject) JSONObject.parse(r);
        if (dirResult != null && Integer.parseInt(dirResult.get("code").toString()) == 200) {
            //判断子目录是否为空-dirResult.getJSONObject("data")
            JSONArray jsonArray = dirResult.getJSONArray("data");
            if (jsonArray.size() != 0) {
                childFTPs = JSONObject.parseArray(jsonArray.toJSONString(), FTPDir.class);
            }
            log.info("查询目录{}成功", dirPath);
        } else {
            //获取子目录失败
            fos = DirectoryOperationStatus.LISTDIR_ERROR;
            //回复原来的用户位置
            log.error("查询目录{}失败", dirPath);
        }
        return childFTPs;
    }

    /**
     * 获取标准年
     * @return
     */
    public static String getDateYear(){
        Date date = new Date();
        String s = String.format("%tY",date);
        log.info("获取年份：{}",s);
        return s;
    }

    /**
     * 获取标准月日
     * @return
     */
    public static String getDateMonthDate(){
        Date date = new Date();
        String s = String.format("%tm%td",date,date);
        log.info("获取月份日期：{}",s);
        return s;
    }

    /**上传前创建远程服务器目录
     * @param token
     * @param centerId
     * @param mainTypeId
     * @param subTypeId
     * @return
     * @throws Exception
     */

    public static FTPDir getUploadDirPath(String token,String basicManagerURL,String cabin, String centerId,String mainTypeId,String subTypeId) {
        //年 路径ID
        String yearDir = FTPUtil.getDateYear();
        //月日 路径ID
        String monthDateDir = FTPUtil.getDateMonthDate();
        FTPDir dir = new FTPDir();
        String r = createDirectory(basicManagerURL, token, cabin,centerId, mainTypeId, subTypeId, yearDir, monthDateDir);
        if (null != r) {
            dir.setId(r);
            r = getDirPath(basicManagerURL, token, dir.getId());
            if (null != r) {
                dir.setName(r);
            } else {
                log.error("通过数据类型查询目录ID失败");
            }
        } else {
            log.error("创建目录路径失败");
        }
        log.info("上传路径：{}",dir);
        return dir;
    }

    /**
     * 根据数据中心、数据类型、年月日创建目录
     * @param token
     * @param center
     * @param mainType
     * @param subType
     * @param year
     * @param monthDate
     * @return  如果目录ID
     */
    public static String createDirectory(String basicManagerURL, String token, String cabin,String center, String mainType, String subType, String year, String monthDate) {

        JSONObject queryParam = new JSONObject();
        queryParam.put("token",token);
        queryParam.put("cabin",cabin);
        queryParam.put("center",center);
        queryParam.put("mainType",mainType);
        queryParam.put("subType",subType);
        queryParam.put("year",year);
        queryParam.put("monthDate",monthDate);

        String r = HttpClientUtil.sendJsonStr(basicManagerURL + "dir/newDir" ,queryParam.toJSONString());

        JSONObject dirIdResult = (JSONObject) JSONObject.parse(r);

        if (dirIdResult != null && Integer.parseInt(dirIdResult.get("code").toString()) == 200) {
            log.info("查询目录ID条件： center：{},  mainType：{},  subType：{},  year：{},  monthDate：{}, 查询结果：{}", center,mainType,subType,year,monthDate,dirIdResult);
            String queryId = dirIdResult.get("data").toString();
            log.info("返回查询目录ID结果：{}", queryId);
            return queryId;
        }
        log.error("获取目录ID不存在，返回 null");
        return null;
    }


    /**
     * 根据目录ID获取目录绝对路径
     * @param token
     * @param dirId
     * @return 如果路径不存在，返回 null
     */
    public static String getDirPath(String basicManagerURL, String token, String dirId) {
        String r = HttpClientUtil.sendJsonStr(basicManagerURL + "dir/getDirPath/" + token + "/" + dirId,null);
        JSONObject dirIdResult = (JSONObject) JSONObject.parse(r);
        if (dirIdResult != null && Integer.parseInt(dirIdResult.get("code").toString()) == 200) {
            log.info("根据目录ID：{} 查询目录路径结果：{}", dirId,dirIdResult);
            return dirIdResult.get("data").toString();
        }
        log.error("目录：{} 绝对路径不存在，返回 null",dirId);
        return null;
    }


//    /**
//     * 创建新的目录
//     * @param token
//     * @param parentDirId
//     * @param dirName
//     * @return
//     */
//    public static String createDirectory(String basicManagerURL, String token, String parentDirId,String dirName){
//        String r = HttpClientUtil.sendJsonStr(basicManagerURL + "dir/newDir/" + token + "/" + parentDirId + "/" + dirName,null);
//        JSONObject dirIdResult = (JSONObject) JSONObject.parse(r);
//        if (dirIdResult != null && Integer.parseInt(dirIdResult.get("code").toString()) == 200) {
//            log.info("新建目录路径结果：{}", dirIdResult);
//            return dirIdResult.get("data").toString();
//        }
//        log.error("创建新的目录失败，返回 null，父目录ID：{},目录名：{}",parentDirId,dirName);
//        return null;
//    }
}

