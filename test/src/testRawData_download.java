package com.bjsasc.FTPClientSDK.service;

import com.bjsasc.FTPClientSDK.model.entry.*;
import com.bjsasc.FTPClientSDK.model.enu.FileOperationStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
/**
 * 支持断点续传的FTP实用类
 *
 * @version 0.3 实现中文目录创建及中文文件创建，添加对于中文的支持
 */
public class testRawData_download {
    public static void main(String[] args) {

        //用户名
        String USERNAME = "test_user_1";
        //密码
        String PASSWORD = "QWER1234";
        // 初始化FTPTransferClient 实例
        FTPTransferClient myFtp = new FTPTransferClient(USERNAME, PASSWORD);

        try {
            String mainType = "main_001";     // 数据大类：归档数据
            String subType = "sub_001";          // 数据小类：原始数据文件

            // 文件操作状态结果
            FileOperationResult result ;

            /************   文件搜索 （搜索出要下载的文件）   ************/
            int pageSize = 10; //分页大小
            int pageNum = 1; //页码
            //查询条件 初始化
            QueryParam queryParam = new QueryParam();
            //查看所有查询字段 param
            Map<String,Param> map = queryParam.getParams();
            String upload_time = "2020-11-12";//上传时间
            queryParam.setParam(queryParam.getParam("upload_time"),upload_time);
            //查询
            List<FTPFile> filesToDownload = myFtp.searchFile(mainType,subType,queryParam,pageSize,pageNum);

            /************   下载文件    ************/
            String downloadPath = "D:\\hanbing"; //下载到本地路径
            result = myFtp.startDownload(downloadPath,filesToDownload);//查询下载方法

            String file = "CT_TL1A2_SZ12_20221028052556_20201028052556_20201028052556_M_00001.raw"; // 下载文件名
            List<String> fileNames = new ArrayList<>();
            fileNames.add(file);//文件名称列表
            result = myFtp.startDownload(mainType, subType, downloadPath, fileNames) ;//直接下载方法

            /************   文件监控 -下载监控   ************/
            String startTime = "2020-10-14 15:36:02";//开始监控时间
            String endTime = "2022-10-24 15:36:02";//结束监控时间
            List<MFileInfo> downloadMmFileInfo = myFtp.monitor(mainType,subType,startTime,endTime,filesToDownload);

            /************   文件列表    ************/
            List<FTPFileInfo> listFiles = myFtp.listFile(mainType,subType);

            //断开连接
            myFtp.logout();
        } catch (Exception e) {
            log.error("操作FTP出错，抛出异常：{}" ,e);
        }
    }

}
