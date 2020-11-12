package com.bjsasc.FTPClientSDK.service;

import com.bjsasc.FTPClientSDK.model.entry.HTY.BaseFile;
import com.bjsasc.FTPClientSDK.model.enu.*;
import com.bjsasc.FTPClientSDK.model.entry.*;
import com.bjsasc.FTPClientSDK.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import java.util.*;
import java.util.concurrent.*;

import static com.bjsasc.FTPClientSDK.util.FTPUtil.SEPARATOR;

/**
 * 支持断点续传的FTP实用类
 *
 * @version 0.3 实现中文目录创建及中文文件创建，添加对于中文的支持
 */
@Slf4j
public class FTPTransferClient extends FTPTransferClientBase{

    /**
     * 构造函数
     *
     * @param userName portal 用户名
     * @param password 密码
     */
    public FTPTransferClient(String userName, String password) {
        super(userName, password);
    }

    /**
     *  用户开始上传归档
     * @param mainTypeId 数据类型-大类 必填
     * @param subTypeId 数据类型-小类 必填
     * @param tagList
     * @param filesToUpload 批量上传数据文件 必填
     * @return UploadStatus 上传的状态
     */
    /**
     *
     * @param cabin
     * @param mainTypeId
     * @param subTypeId
     * @param tagList
     * @param filesToUpload
     * @return
     * @throws Exception
     */
    public FileOperationResult startUpload(String cabin, String mainTypeId, String subTypeId, List<String> tagList, List<FTPFile> filesToUpload)  throws Exception{
        FileOperationResult fileOperationResult = new FileOperationResult();
        FileOperationStatus fileOperationStatus = FileOperationStatus.SUCCESS;
//        List<String> failNames = new ArrayList<>() ;
        //文件校验

        FileOperationResult checkResult = checkFilesBeforeUpload(filesToUpload,portalUserInfo.getToken(),subTypeId);
        if(checkResult.getStatus() != FileOperationStatus.SUCCESS){
            return checkResult;
        }
        //初始化上传信息
        Result<InitUploadInfo> initResult = initUploadInfo(cabin,mainTypeId,subTypeId,tagList,filesToUpload);
        InitUploadInfo initUploadInfo = initResult.getData();
        if (initResult.isSuccess()) {
            Map<String, String> fileIds =  initUploadInfo.getFileIds();
            FTPDir dir = initUploadInfo.getTargetDir();
            //批量上传 -开始
            log.info("上传任务开始");

            int numThread = filesToUpload.size()>this.thread?this.thread:filesToUpload.size();
            ExecutorService executor = Executors.newFixedThreadPool(numThread);
            CompletionService<FileResult> completionService = new ExecutorCompletionService<>(executor);
            for(FTPFile f : filesToUpload){
                completionService.submit(() -> {
                    FileResult fileStatus =null;
                    FTPClient ftpClient = null;
                    try {
                        ftpClient = getFTPClient();
                        if(ftpClient!=null){
                            fileStatus = uploadOneFile(ftpClient,f, fileIds.get(f.getName()), dir,initUploadInfo.getYear());
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    } finally{
                        if(ftpClient!=null) {
                            disconnect(ftpClient);
                        }
                    }
                    return fileStatus;
                });
            }

            ThreadPoolExecutor tpe = ((ThreadPoolExecutor) executor);
            int completionTask = 0;

            while (completionTask < numThread) {
                System.out.println("completionTask = "+completionTask);
                int queueSize = tpe.getQueue().size();
                System.out.println("当前排队线程数：" + queueSize);
                int activeCount = tpe.getActiveCount();
                System.out.println("当前活动线程数：" + activeCount);
                long completedTaskCount = tpe.getCompletedTaskCount();
                System.out.println("执行完成线程数：" + completedTaskCount);
                long taskCount = tpe.getTaskCount();
                System.out.println("总线程数：" + taskCount);
                Thread.sleep(1000);
                //如果完成队列中没有数据, 则阻塞; 否则返回队列中的数据
                Future<FileResult> resultHolder = completionService.take();
                if(resultHolder.get()!=null){
                    fileOperationResult.getFileResults().put(resultHolder.get().getName(),resultHolder.get());
                    if(resultHolder.get().getStatus() != FileOperationStatus.SUCCESS){
                        fileOperationStatus = FileOperationStatus.UPLOAD_ERROR;
                        fileOperationResult.getFailFiles().add(resultHolder.get().getName());
                    }
                    completionTask++;
                }

            }

            log.info("上传任务完成");
            //ExecutorService使用完一定要关闭
            executor.shutdown();
            fileOperationResult.setStatus(fileOperationStatus);

        } else {
                fileOperationResult.setStatus(FileOperationStatus.getByCode(initResult.getCode()));
                List<String> files = initUploadInfo.getFailFiles();
                fileOperationResult.setFailFiles(files);
        }
        return fileOperationResult;
    }

    /**
     *  用户开始上传  指定为航天员上传
     * @param mainTypeId 数据类型-大类 必填
     * @param subTypeId 数据类型-小类 必填
     * @param uploadFileMap 批量上传数据文件 map类型(key:文件，value:存档信息必填
     * @return UploadStatus 上传的状态
     */
    public FileOperationResult startUploadWithInfo(String cabin, String mainTypeId, String subTypeId, List<String> tagList, Map<FTPFile, BaseFile>uploadFileMap)  throws Exception{
        FileOperationResult fileOperationResult = new FileOperationResult();
        FileOperationStatus fileOperationStatus = FileOperationStatus.SUCCESS;
        List<String> failNames = new ArrayList<>() ;
        List<FTPFile> filesToUpload = new ArrayList(uploadFileMap.keySet());
        //初始化上传信息
        Result<InitUploadInfo> initResult = initUploadInfo(cabin,mainTypeId,subTypeId,tagList,filesToUpload);
        InitUploadInfo initUploadInfo = initResult.getData();
        if (initResult.isSuccess()) {
            Map<String, String> fileIds =  initUploadInfo.getFileIds();
            FTPDir dir = initUploadInfo.getTargetDir();
            //批量上传 -开始
            log.info("上传任务开始");
            int numThread = filesToUpload.size()>this.thread?this.thread:filesToUpload.size();
            ExecutorService executor = Executors.newFixedThreadPool(numThread);
            CompletionService<FileResult> completionService = new ExecutorCompletionService<>(executor);
            for(FTPFile f : filesToUpload){

                completionService.submit(() -> {
                    FileResult fileStatus = null;
                    FTPClient ftpClient = null;
                    try{
                        ftpClient =super.getFTPClient();
                        if(ftpClient!=null){
                            fileStatus = uploadOneFileWithInfo(ftpClient,f, fileIds.get(f.getName()), dir,uploadFileMap.get(f),initUploadInfo.getYear());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if(ftpClient!=null) {
                            disconnect(ftpClient);
                        }
                    }
                    return fileStatus;
                });
            }
            int completionTask = 0;
            while(completionTask < numThread) {
                //如果完成队列中没有数据, 则阻塞; 否则返回队列中的数据
                Future<FileResult> resultHolder = completionService.take();
                if(resultHolder.get()!=null) {
                    fileOperationResult.getFileResults().put(resultHolder.get().getName(), resultHolder.get());
                    if (resultHolder.get().getStatus() != FileOperationStatus.SUCCESS) {
                        fileOperationStatus = FileOperationStatus.UPLOAD_ERROR;
                        failNames.add(resultHolder.get().getName());
                    }
                    completionTask++;
                }

            }
            log.info("上传任务完成");
            //ExecutorService使用完一定要关闭
            executor.shutdown();
            fileOperationResult.setStatus(fileOperationStatus);
            fileOperationResult.setFailFiles(failNames);
        } else {
            fileOperationResult.setStatus(FileOperationStatus.getByCode(initResult.getCode()));
            List<String> files = initUploadInfo.getFailFiles();
            fileOperationResult.setFailFiles(files);
        }
        return fileOperationResult;
    }

    /**
     * 上传一个无附带归档信息的文件
     * @param f
     * @param fileId
     * @param dir
     * @return
     * @throws Exception
     */
    private FileResult uploadOneFile(FTPClient ftpClient,FTPFile f, String fileId, FTPDir dir,String year)throws Exception{
        FileResult fileResult = new FileResult();
        String fileName = f.getName();
        fileResult.setName(fileName);
        String sourcePath = f.getPath() + SEPARATOR + fileName;
        String targetPath = dir.getName() + SEPARATOR + fileName;
        System.out.println(fileName+"开始上传 ");
        //上传单个文件
        boolean result = singleFileUpload(ftpClient,fileId,sourcePath,targetPath,year);
        //根据上传状态返回结果决定上传完成更新状态
        if(result){
            fileResult.setStatus(FileOperationStatus.SUCCESS);
            //文件归档
            boolean archiveResult = archive(fileId,year);
            if(!archiveResult){
                fileResult.setStatus(FileOperationStatus.ARCHIVED_ERROR);
            }
        }else {
            fileResult.setStatus(FileOperationStatus.UPLOAD_ERROR);
            //删除上传失败文件
            ftpClient.deleteFile(targetPath);
        }
        log.info("文件上传结果：{},文件ID：{}",result,fileId);
        return fileResult;
    }

    /**
     * 上传一个附带归档信息的文件
     * @param f
     * @param fileId
     * @param dir
     * @param baseFile
     * @return
     * @throws Exception
     */
    private FileResult uploadOneFileWithInfo(FTPClient ftpClient,FTPFile f, String fileId, FTPDir dir,BaseFile baseFile,String year)throws Exception{
        FileResult fileResult = new FileResult();
        String fileName = f.getName();
        fileResult.setName(fileName);
        String sourcePath = f.getPath() + SEPARATOR + fileName;
        String targetPath = dir.getName() + SEPARATOR + fileName;
        //上传单个文件
        boolean result = singleFileUpload(ftpClient,fileId,sourcePath,targetPath,year);
        //根据上传状态返回结果决定上传完成更新状态
        if(result){
            fileResult.setStatus(FileOperationStatus.SUCCESS);
            //文件归档
            boolean archiveResult = archive(fileId,JsonUtils.objectToJson(baseFile),year);
            if(!archiveResult){
                fileResult.setStatus(FileOperationStatus.ARCHIVED_ERROR);
            }
        }else {
            fileResult.setStatus(FileOperationStatus.UPLOAD_ERROR);
            //删除上传失败文件
            ftpClient.deleteFile(targetPath);
        }
        log.info("文件上传结果：{},文件ID：{}",result,fileId);
        return fileResult;
    }

}

