package com.bjsasc.FTPClientSDK.service;

import com.bjsasc.FTPClientSDK.model.entry.*;
import com.bjsasc.FTPClientSDK.model.entry.HTY.BaseFile;
import com.bjsasc.FTPClientSDK.model.entry.HTY.BioMedicalFile;
import com.bjsasc.FTPClientSDK.model.entry.HTY.DataResultAchieveReportFile;
import com.bjsasc.FTPClientSDK.model.enu.FileOperationStatus;
import com.bjsasc.FTPClientSDK.model.enu.QueryParamEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
/**
 * 支持断点续传的FTP实用类
 *
 * @version 0.3 实现中文目录创建及中文文件创建，添加对于中文的支持
 */
public class testDataResultAchieve {
    public static void main(String[] args) {

        String USERNAME = "test_user_3";  //用户名admin
        String PASSWORD = "QWER1234";  //密码

        // 初始化FTPTransferClient 实例
        FTPTransferClient myFtp = new FTPTransferClient(USERNAME, PASSWORD);

        // 文件操作状态结果
        final FileOperationResult[] result = new FileOperationResult[1];

        try {
            // 连接到远程服务器
            /************   上传文件    ************/
            // 本地文件名和目录
            String file = "文件信息.docx";
            String localPath = "C:\\Users\\qrs\\Desktop";
            // 待上传文件列表
            Map<FTPFile, BaseFile> filesToUpload = new HashMap<>();
            List<FTPFile> ftpFiles = new ArrayList<>();
            FTPFile ftpFile1 = new FTPFile(file,localPath);
            ftpFiles.add(ftpFile1);
            //归档信息
            DataResultAchieveReportFile dataResultAchieveReportFile = new DataResultAchieveReportFile();
            dataResultAchieveReportFile.setFormat("数据成果格式");
            dataResultAchieveReportFile.setAchieveName("数据成果名称");
            dataResultAchieveReportFile.setFounder("创建人");
            dataResultAchieveReportFile.setAchieveFrom("来源");
            dataResultAchieveReportFile.setAchieveKind("类型");
            dataResultAchieveReportFile.setAchieveCopyright("版权方");
            dataResultAchieveReportFile.setDescOfAchievs("描述");
            dataResultAchieveReportFile.setFounderTime(new Date());
            filesToUpload.put(ftpFile1,dataResultAchieveReportFile);
            //标签
            List<Tag> tagList = myFtp.getTags();
            List<String>tagLabels = new ArrayList<>();
            tagLabels.add("40e8e2517c96482c90ea0aba1ce637b0");
            tagLabels.add("2e48a1b6cc9f4e75905bfc22a83444b6");
            // 上传文件
            String mainType = "main_017";     // 数据大类：归档数据
            String subType = "sub_078";          // 数据小类：原始数据文件
            String cabin = "cabin_001"; //舱段ID
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        result[0] = myFtp.startUploadWithInfo(cabin,mainType,subType,tagLabels,filesToUpload);

                    } catch (Exception e) {
                        log.error("上传过程出错！",e);
                        e.printStackTrace();
                    }
                }
            }).start();
            /************   文件监控-上传监控    ************/
            TimeUnit.MILLISECONDS.sleep(1000);
            List<MFileInfo> uploadMmFileInfo = myFtp.monitor(mainType,subType,"2020-10-14 15:36:02","2020-10-24 15:36:02",ftpFiles);


            boolean isMonitor = true;
            while (null==result[0]||isMonitor){
                TimeUnit.MILLISECONDS.sleep(1000);
                uploadMmFileInfo = myFtp.monitor(mainType,subType,"2020-10-14 15:36:02","2020-10-24 15:36:02",ftpFiles);
                isMonitor = false;//结束监控
                for(MFileInfo info : uploadMmFileInfo){
                    if(!info.getPercentage().equals("100%")){
                        isMonitor = true;//继续监控
                        break;
                    }
                }
            }
//            /************   文件上传结果   ************/
            FileOperationStatus status = result[0].getStatus();//文件上传状态
            switch (status){
                case SUCCESS:
                    log.info("用户上传成功");
                    break;
                case FORBIDDEN:
                    log.error("用户没有上传权限");
                case CHECK_STANDARD_ERROR:
                    log.error("检查规范性失败，失败文件名称列表：{}",result[0].getFailFiles());
                    break;
                case CHECK_REPEATE_FILESELF_ERROR:
                    log.error("检查重复性失败-文件本身存在重复，失败文件名称列表：{}",result[0].getFailFiles());
                    break;
                case CHECK_REPEATE_UPLOAD_ERROR:
                    log.error("检查重复性失败-文件已经被上传，失败文件名称列表：{}", result[0].getFailFiles());
                    break;
                case UPLOAD_ERROR:
                    log.error("上传失败，失败文件名称列表：{}", result[0].getFailFiles());
                    //上传失败详细情况
                    for(String fileName :result[0].getFailFiles()){
                        FileResult fileResult = result[0].getFileResults().get(fileName);
                        log.error("文件名：{}；上传状态：{}；原始大小：{}；上传大小：{}",fileResult.getName(),fileResult.getOriginalSize(),fileResult.getOriginalSize(),fileResult.getSize());
                    }
                    break;
            }

            /************   文件搜索 （搜索出要下载的文件）   ************/
            int pageSize = 10; //分页大小
            int pageNum = 1; //页码
            //查询条件
            QueryParam queryParam = new QueryParam();
            String uploadTime = "2020-10-16";
            queryParam.setParam(QueryParamEnum.UPLOAD_TIME,uploadTime);
//            List<FTPFile> filesToDownload = myFtp.searchFile(mainType,subType,queryParam,pageSize,pageNum);

            /************   下载文件    ************/
            String downloadPath = "D:\\hanbing"; //下载到本地路径
//            result[0] = myFtp.startDownload(downloadPath,filesToDownload);

//            /************   新建目录   ************/
//            System.out.println(myFtp.newDirectory("newDir3"));
//
//            /************   新建目录   ************/
//            System.out.println(myFtp.newDirectory("newDir4"));

//            /************   删除目录   ************/
//            System.out.println(myFtp.deleteDirectory("newDir4"));
//
            /************   文件监控 -下载监控   ************/
//            List<MFileInfo> downloadMmFileInfo = myFtp.monitor(mainType,subType,"2020-10-14 15:36:02","2020-10-24 15:36:02",filesToDownload);
//
//
            /************   文件列表    ************/
//            List<FTPFileInfo> listFiles = myFtp.listFile(mainType,subType);

//            /************   删除远程文件    ************/
//            // 将刚刚搜索的文件从远程文件夹删除
//            result[0] = myFtp.deleteFile(ftpFiles);
            //断开连接
            myFtp.logout();
        } catch (Exception e) {
            log.error("操作FTP出错，抛出异常：{}" ,e);
        }
    }

}
