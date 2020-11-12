package com.bjsasc.FTPClientSDK.service;

import com.bjsasc.FTPClientSDK.model.entry.*;
import com.bjsasc.FTPClientSDK.model.enu.FileOperationStatus;
import com.bjsasc.FTPClientSDK.model.enu.QueryParamEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
/**
 * 支持断点续传的FTP实用类
 *
 * @version 0.3 实现中文目录创建及中文文件创建，添加对于中文的支持
 */
public class 仿真模型文件元信息表sub_083 {
    public static void main(String[] args) {

        String USERNAME = "test_user_4";  //用户名test_user_3
        String PASSWORD = "QWER1234";  //密码

        // 初始化FTPTransferClient 实例
        FTPTransferClient myFtp = new FTPTransferClient(USERNAME, PASSWORD);

        try {
            // 连接到远程服务器
//            if(!myFtp.connect()){
//                log.error("用户登录失败");
//            }
            /************   上传文件    ************/
            // 待上传文件列表
            List<FTPFile> ftpFiles = new ArrayList<>();
            String localPath = "C:\\Users\\qrs\\Desktop";
            String file = "WT01_LaunchWindows_text_V1.9_name_20201230061252.dll";
            ftpFiles.add(new FTPFile(file,localPath));
//            file = "PA_EN_TGTH_GCYC_00_GCYC_20200817234411_20200817234411_20220817234411_000.raw";
//            ftpFiles.add(new FTPFile(file,localPath));
//            file = "PA_EN_TGTH_GCYC_00_GCYC_20200817234411_20200817234411_20210817234411_000.raw";
//            ftpFiles.add(new FTPFile(file,localPath));

            // 上传文件
            String mainType = "main_022";     // 数据大类：归档数据
            String subType = "sub_083";          // 数据小类：原始数据文件
            String cabin = "cabin_001"; //舱段ID
            //标签
            List<Tag> tagList = myFtp.getTags();
            List<String>tagLabels = new ArrayList<>();
            tagLabels.add("40e8e2517c96482c90ea0aba1ce637b0");
            tagLabels.add("2e48a1b6cc9f4e75905bfc22a83444b6");

            // 文件操作状态结果
            final FileOperationResult[] result = new FileOperationResult[1];

//            result[0] = myFtp.startUpload(cabin,mainType,subType, tagLabels,ftpFiles);
//
//
//            /************   文件监控-上传监控    ************/
//            List<MFileInfo> uploadMmFileInfo =myFtp.monitor(mainType,subType,"2020-10-28 14:36:02","2020-12-24 23:36:02",ftpFiles);
//                //TODO: 打印进度，上传速度 kb/s
//
//            /************   文件上传结果   ************/
//            FileOperationStatus status = result[0].getStatus();//文件上传状态
//            switch (status){
//                case SUCCESS:
//                    log.info("用户上传成功");
//                    break;
//                case FORBIDDEN:
//                    log.error("用户没有上传权限");
//                case CHECK_STANDARD_ERROR:
//                    log.error("检查规范性失败，失败文件名称列表：{}",result[0].getFailFiles());
//                    break;
//                case CHECK_REPEATE_FILESELF_ERROR:
//                    log.error("检查重复性失败-文件本身存在重复，失败文件名称列表：{}",result[0].getFailFiles());
//                    break;
//                case CHECK_REPEATE_UPLOAD_ERROR:
//                    log.error("检查重复性失败-文件已经被上传，失败文件名称列表：{}", result[0].getFailFiles());
//                    break;
//                case UPLOAD_ERROR:
//                    log.error("上传失败，失败文件名称列表：{}", result[0].getFailFiles());
//                    //上传失败详细情况
//                    for(String fileName :result[0].getFailFiles()){
//                        FileResult fileResult = result[0].getFileResults().get(fileName);
//                        log.error("文件名：{}；上传状态：{}；原始大小：{}；上传大小：{}",fileResult.getName(),fileResult.getOriginalSize(),fileResult.getOriginalSize(),fileResult.getSize());
//                    }
//                    break;
//            }

            /************   文件搜索 （搜索出要下载的文件）   ************/
            int pageSize = 10; //分页大小
            int pageNum = 1; //页码
            //查询条件
            QueryParam queryParam = new QueryParam();
            String uploadTime = "2021-01-01";
            queryParam.setParam(QueryParamEnum.UPLOAD_TIME,uploadTime);
            List<FTPFile> filesToDownload = myFtp.searchFile(mainType,subType,queryParam,pageSize,pageNum);

            /************   下载文件    ************/
            String downloadPath = "D:\\hanbing"; //下载到本地路径
            result[0] = myFtp.startDownload(downloadPath,filesToDownload);

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
