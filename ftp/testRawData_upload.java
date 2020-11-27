package com.bjsasc.FTPClientSDK.service;

import com.bjsasc.FTPClientSDK.model.entry.*;
import com.bjsasc.FTPClientSDK.model.enu.FileOperationStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
/**
 * 支持断点续传的FTP实用类
 *
 * @version 0.3 实现中文目录创建及中文文件创建，添加对于中文的支持
 */
public class testRawData_upload {
    public static void main(String[] args) {
        //用户名
        String USERNAME = "test_user_1";
        //密码
        String PASSWORD = "QWER1234";
        // 初始化FTPTransferClient 实例
        FTPTransferClient myFtp = new FTPTransferClient(USERNAME, PASSWORD);

        try {
            /************   上传文件    ************/
            // 待上传文件列表
            List<FTPFile> ftpFiles = new ArrayList<>();
            String localPath = "C:\\Users\\qrs\\Desktop";
            String file = "CT_TL1A2_SZ12_23231028052556_20201028052556_20201028052556_M_00001.raw";
            ftpFiles.add(new FTPFile(file,localPath));
            file = "CT_TL1A2_SZ12_20244028052556_20201028052556_20201028052556_M_00001.raw";//PA_EN_TGTH_GCYC_00_GCYC_20200817234411_20200817234411_20220817234411_000.raw
            ftpFiles.add(new FTPFile(file,localPath));

            // 上传文件
            String mainType = "main_001";     // 数据大类：归档数据
            String subType = "sub_001";          // 数据小类：原始数据文件
            String cabin = "cabin_001"; //舱段ID
            //标签
            List<Tag> tagList = myFtp.getTags();//查看所有标签信息
            List<String>tagLabels = new ArrayList<>();
            String tagId = "40e8e2517c96482c90ea0aba1ce637b0";//"2e48a1b6cc9f4e75905bfc22a83444b6"
            tagLabels.add(tagId);//将选中的标签放入标签列表

            // 文件操作状态结果
            final FileOperationResult[] result = new FileOperationResult[1];
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("开始调用上传");
                        result[0] = myFtp.startUpload(cabin,mainType,subType, null,ftpFiles);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            /************   文件监控-上传监控    ************/
            String startTime = "2020-10-14 15:36:02";//开始监控时间
            String endTime = "2022-10-24 15:36:02";//结束监控时间
            List<MFileInfo> uploadMmFileInfo ;

            while (null==result[0] ){
                TimeUnit.MILLISECONDS.sleep(2000);
                uploadMmFileInfo = myFtp.monitor(mainType,subType,startTime,endTime,ftpFiles);
            }

            /************   文件上传结果   ************/
            FileOperationStatus status = result[0].getStatus();//文件上传状态
            switch (status){
                case SUCCESS:
                    System.out.println("用户上传成功");
                    break;
                case FORBIDDEN:
                    System.out.println("用户没有上传权限");
                case Check_Standard_Error:
                    System.out.println("检查规范性失败，失败文件名称列表：{}"+result[0].getFailFiles());
                    break;
                case Check_Repeat_File_Self_Error:
                    System.out.println("检查重复性失败-文件本身存在重复，失败文件名称列表：{}"+result[0].getFailFiles());
                    break;
                case Check_Repeat_Uploaded_Error:
                    System.out.println("检查重复性失败-文件已经被上传，失败文件名称列表：{}"+result[0].getFailFiles());
                    break;
                case Upload_Error:
                    System.out.println("上传失败，失败文件名称列表：{}"+ result[0].getFailFiles());
                    //上传失败详细情况
                    for(String fileName :result[0].getFailFiles()){
                        FileResult fileResult = result[0].getFileResults().get(fileName);
                        System.out.println("文件名：{}；上传状态：{}；原始大小：{}；上传大小：{}"+fileResult.getName()+fileResult.getOriginalSize()+fileResult.getOriginalSize()+fileResult.getSize());
                    }
                    break;
            }

            //断开连接
            myFtp.logout();
        } catch (Exception e) {
            System.out.println("操作FTP出错，抛出异常：{}" +e);
        }
    }

}
