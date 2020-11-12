package com.bjsasc.FTPClientSDK.service;

import com.alibaba.fastjson.JSONObject;
import com.bjsasc.FTPClientSDK.model.dto.DownloadResponseDTO;
import com.bjsasc.FTPClientSDK.model.dto.FileInfoDTO;
import com.bjsasc.FTPClientSDK.model.dto.UploadRequestDTO;
import com.bjsasc.FTPClientSDK.model.dto.UploadResponseDTO;
import com.bjsasc.FTPClientSDK.model.entry.*;
import com.bjsasc.FTPClientSDK.model.enu.*;
import com.bjsasc.FTPClientSDK.util.FTPUtil;
import com.bjsasc.FTPClientSDK.util.FileMD5Util;
import com.bjsasc.FTPClientSDK.util.HttpClientUtil;
import com.bjsasc.FTPClientSDK.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.bjsasc.FTPClientSDK.util.FTPUtil.SEPARATOR;

/**
 * 支持断点续传的FTP实用类
 *
 * @version 0.3 实现中文目录创建及中文文件创建，添加对于中文的支持
 */
@Slf4j
public class FTPTransferClientBase {
    /**
     * ftp客户端实例
     */
    private ThreadLocal<FTPClient> ftpClientThreadLocal = new ThreadLocal<FTPClient>();
//    public FTPClient ftpClient = getFTPClient();

    /**
     * 当前用户信息
     */
    public PortalUserInfo portalUserInfo;

    /**
     * 文件名称编码
     */
    private String controlEncoding;
    /**
     * 数据传输访问前缀
     */
    private String basicManagerURL;
    /**
     * 服务器端访问前缀
     */
    private String basicServerURL;
    /**
     * 当前portal用户映射FTP用户信息
     */
    private FTPUser ftpUser;
    /**
     * 分片大小
     */
    private int chunk;
    /**
     * 支持并发数
     */
    public int thread;
    /**
     * 构造函数
     * @param userName portal 用户名
     * @param password 密码
     */
    public FTPTransferClientBase(String userName, String password) {

        portalUserInfo = new PortalUserInfo();
        if(null != userName && "" != userName){
            portalUserInfo.setUserName(userName);
        }else {
            log.error("用户名为空");
            throw new RuntimeException("创建连接失败，用户名为空");
        }
        if(null != password && "" != password){
            portalUserInfo.setPassword(password);
        }else {
            log.error("密码为空");
            throw new RuntimeException("创建连接失败，用户密码为空");
        }
        basicManagerURL = readBasicManagerURL();
        basicServerURL = readBasicServerURL();
        chunk = readChunkProperty();
        thread = readThreadProperty();
        //调用manager后台 验证权限获取用户信息
        String r = HttpClientUtil.doPost(basicManagerURL+"user/authentication/" + userName + "/" + password,null);
        JSONObject dirResult = (JSONObject) JSONObject.parse(r);
        if(Integer.parseInt(dirResult.get("code").toString()) == 200) {
            portalUserInfo.setToken((String) dirResult.getJSONObject("data").get("token"));
            portalUserInfo.setCenter((String) dirResult.getJSONObject("data").get("center"));
            portalUserInfo.setGroup((String) dirResult.getJSONObject("data").get("group"));
        }else{
            log.error("创建连接失败，没有获取到正确的用户信息");
            throw new RuntimeException("创建连接失败，没有获取到正确的用户信息");
        }

        //FTP用户映射
        ftpUser = new FTPUser();
        boolean mappingResult = FTPUtil.userAuthority(basicManagerURL,ftpUser,portalUserInfo);
        if(!mappingResult){
            throw new RuntimeException("创建连接失败，没有正确映射到FTP用户");
        }

        //设置文件名称编码
        controlEncoding = "GBK";
        //设置将过程中使用到的命令输出到控制台
//        this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
    }


    /**
     * 连接到FTP服务器
     * @return 是否连接成功
     * @throws IOException IO异常
     */
    public boolean connect(FTPClient ftpClient) throws IOException {

        if(null!=ftpUser.getHostname()&&""!=ftpUser.getHostname()) {
            ftpClient.connect(ftpUser.getHostname(), ftpUser.getPort());
        }else if(null!=ftpUser.getDomain()&&""!=ftpUser.getDomain()){
                ftpClient.connect(ftpUser.getDomain());
        }
        //测试服务器是否处于正常工作状态
        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                controlEncoding = "UTF-8";
            }
            ftpClient.setControlEncoding(this.controlEncoding);
//            FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
//            conf.setServerLanguageCode("zh");
            return ftpClient.login(ftpUser.getAuthor(), ftpUser.getSecurity());
        }

        //释放资源
        disconnect(ftpClient);
        return false;
    }

    public FileResult downloadOneFile(FTPClient ftpClient,String localDir, FTPFile file,String year)throws Exception{
        FileResult fileResult = new FileResult();
        fileResult.setName(file.getName());

        String localPath = localDir+SEPARATOR+file.getName();
        String downLoadId = null;
        String remotePath = null;
        //调用manager后台开始下载
        String r = HttpClientUtil.doPost(basicManagerURL + "file/startDownload/" + portalUserInfo.getToken() + "/" + file.getId() + "/" + "/" +UploadMode.MODE_FTP.message()+ "/" + year, null);
        JSONObject startDownLoadResult = (JSONObject) JSONObject.parse(r);
        if (startDownLoadResult!=null&&Integer.parseInt(startDownLoadResult.get("code").toString()) == 200) {
            //获取文件下载信息：ID和 path
            DownloadResponseDTO downloadResponseDTO = JsonUtils.jsonToPojo(startDownLoadResult.get("data").toString(),DownloadResponseDTO.class);
            downLoadId = downloadResponseDTO.getDownloadInfoId() ;
            String remotedir = downloadResponseDTO.getDirPath();
            remotePath = remotedir+SEPARATOR+file.getName();
            //文件下载
            DownloadStatus downloadStatus = download(ftpClient,remotePath, localPath,downLoadId);
            log.info("文件下载消息：{} 文件名称{},下载用户{}",downloadStatus.message(),file.getName(),portalUserInfo.getUserName());
            //文件下载完成
            if(downloadStatus.compareTo(DownloadStatus.Download_From_Break_Success)==0||downloadStatus.compareTo(DownloadStatus.Download_New_Success)==0){
                HttpClientUtil.doPost(basicManagerURL + "file/updateFileDownloadStatus/" + portalUserInfo.getToken() + "/" + downLoadId+ "/" + FileStatus.END_DOWNLOAD.code()+ "/" + downloadResponseDTO.getYear(), null);
                fileResult.setStatus(FileOperationStatus.SUCCESS);
                log.info("文件下载成功 本地文件地址：{}，远程文件地址{}",localPath,remotePath);
                //TODO:下载成功后，完整性校验
            }else{
                log.error("文件下载失败，本地文件地址：{}，远程文件地址{},失败原因{}",localPath,remotePath,downloadStatus.message());
                HttpClientUtil.doPost(basicManagerURL + "file/updateFileDownloadStatus/" + portalUserInfo.getToken() + "/" + downLoadId+ "/" + FileStatus.ERROR_DOWNLOAD.code()+ "/" + downloadResponseDTO.getYear(), null);
                fileResult.setStatus(FileOperationStatus.DOWNLOAD_ERROR);
            }
        }else{
            int code = (int) startDownLoadResult.getJSONObject("result").get("code");
            fileResult.setStatus(FileOperationStatus.getByCode(code));
            log.error("初始化文件{}下载失败 ，失败原因：{}",file.getName(),FileOperationStatus.getByCode(code).message());
        }
        return fileResult;
    }
    /**
     *文件下载
     * @param localDir 下载到本地绝对路径
     * @param filesToDownload 要下载的文件名称列表
     * @return DownloadStatus 下载的状态
     */
    public FileOperationResult startDownload(String localDir, List<FTPFile>filesToDownload) throws Exception{
        FileOperationResult fileOperationResult = new FileOperationResult();
        List<String> failNames = new ArrayList<>();
        FileOperationStatus fileOperationStatus = FileOperationStatus.SUCCESS;

        //批量上传 -开始
        log.info("下载任务开始");

        int numThread = filesToDownload.size()>this.thread?this.thread:filesToDownload.size();
        ExecutorService executor = Executors.newFixedThreadPool(numThread);
        CompletionService<FileResult> completionService = new ExecutorCompletionService<>(executor);
        for(FTPFile file : filesToDownload){
            completionService.submit(() -> {
                FileResult fileStatus =null;
                FTPClient ftpClient = null;
                try {
                    ftpClient = getFTPClient();
                    if(ftpClient!=null){
                        fileStatus = downloadOneFile(ftpClient,localDir,file,file.getYear());
                    }
                } catch (Exception e){

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
            System.out.println();
            int queueSize = tpe.getQueue().size();
            System.out.println("当前排队线程数：" + queueSize);
            int activeCount = tpe.getActiveCount();
            System.out.println("当前活动线程数：" + activeCount);
            long completedTaskCount = tpe.getCompletedTaskCount();
            System.out.println("执行完成线程数：" + completedTaskCount);
            long taskCount = tpe.getTaskCount();
            System.out.println("总线程数：" + taskCount);
            Thread.sleep(10000);
            //如果完成队列中没有数据, 则阻塞; 否则返回队列中的数据
            Future<FileResult> resultHolder = completionService.take();
            if(resultHolder.get()!=null){
                if(resultHolder.get().getStatus() != FileOperationStatus.SUCCESS){
                    fileOperationStatus = FileOperationStatus.DOWNLOAD_ERROR;
                    failNames.add(resultHolder.get().getName());
                }
            }
            completionTask++;
        }
        log.info("下载任务完成");
        //ExecutorService使用完一定要关闭
        executor.shutdown();
        fileOperationResult.setStatus(fileOperationStatus);
        fileOperationResult.setFailFiles(failNames);
        return fileOperationResult;
    }

    /**
     * 从FTP服务器上下载文件,支持断点续传，上传百分比汇报
     *
     * @param remote 远程文件路径
     * @param local  本地文件路径
     * @return 下载的状态
     * @throws Exception 异常
     */

    private DownloadStatus download(FTPClient ftpClient,String remote, String local,String downloadId) throws Exception {
        log.info("文件下载请求 远程文件路径：{}，本地路径：{}",remote,local);
        DownloadStatus result;
        //设置以二进制流的方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        //ftpClient.setControlEncoding("UTF-8");
        // 获取登录信息
        FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
        config.setServerLanguageCode("zh");
        ftpClient.configure(config);
        // 使用被动模式设为默认
        ftpClient.enterLocalPassiveMode();

        //调整用户所在位置 -确保为根目录
        ftpClient.changeWorkingDirectory("/");
        //检查远程文件是否存在
        org.apache.commons.net.ftp.FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes(controlEncoding), StandardCharsets.ISO_8859_1));
        if (files.length != 1) {
            log.error("用户当前路径：{}，远程文件不存在 远程文件路径：{}，本地路径：{}",ftpClient.printWorkingDirectory(),remote,local);
            return DownloadStatus.File_Noexist; // TODO:判断这种情况
        }

        long lRemoteSize = files[0].getSize();
        File f = new File(local);

        //本地存在文件，进行断点下载
        if (f.exists()) {
            long localSize = f.length();

            //检查本地是否存在文件
            if (lRemoteSize == localSize) {
                log.error("下载文件前 检验本地存在文件，远程文件名:{}，本地路径：{}",remote,local);
                return DownloadStatus.File_Exits;//TODO:判断这种情况
            }

            //判断本地文件大小是否大于远程文件大小
            if (localSize > lRemoteSize) {
                System.out.println("本地文件大于远程文件，下载中止");
                return DownloadStatus.Check_File_Size_Failed;
            }

            //进行断点续传，并记录状态
            FileOutputStream out = new FileOutputStream(f, true);
            ftpClient.setRestartOffset(localSize);
            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes(controlEncoding), StandardCharsets.ISO_8859_1));
            byte[] bytes = new byte[chunk];
            long step = lRemoteSize / 100;
            long process = localSize / step;
            int c = 0;

            boolean isDo = transfer(ftpClient,out, in,bytes, step, process, localSize, c,downloadId,lRemoteSize);
            if (isDo) {
                log.info("断点下载文件成功 远程文件路径：{}，本地路径：{} ，下载进度：{}",remote,local,process);
                result = DownloadStatus.Download_From_Break_Success;
            } else {
                log.error("断点下载文件失败 远程文件路径：{}，本地路径：{} ，下载进度：{}",remote,local,process);
                result = DownloadStatus.Download_From_Break_Failed;
            }
        } else {
            OutputStream out = new FileOutputStream(f);
            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes(controlEncoding), StandardCharsets.ISO_8859_1));

            byte[] bytes = new byte[chunk];
            long step = lRemoteSize / 100;
            long process = 0;
            long localSize = 0L;
            int c = 0;

            boolean upNewStatus = transfer(ftpClient,out, in,bytes, step, process, localSize, c,downloadId,lRemoteSize);
            if (upNewStatus) {
                log.info("全新下载文件成功 远程文件路径：{}，本地路径：{}",remote,local);
                result = DownloadStatus.Download_New_Success;
            } else {
                log.error("全新下载文件失败 远程文件路径：{}，本地路径：{}",remote,local);
                result = DownloadStatus.Download_New_Failed;
            }
        }
        log.info("下载结果：{}", result);
        return result;
    }
    private boolean transfer(FTPClient ftpClient,OutputStream out,InputStream in,byte[] bytes,long step,long process,long localSize,int c, String downloadId ,long lRemoteSize) throws Exception{
        while ((c = in.read(bytes)) != -1) {
            out.write(bytes, 0, c);
            localSize += c;
            long nowProcess = localSize / step;
            if (nowProcess > process) {
                process = nowProcess;
                //if (process % 10 == 0)
                System.out.println("下载进度：" + process);
                //更新文件下载进度
                String r = HttpClientUtil.doPost(basicManagerURL+"file/updateFileDownloadSize/" + portalUserInfo.getToken() + "/" + downloadId+ "/" + localSize,null);
                JSONObject updateFileSizeResult = (JSONObject) JSONObject.parse(r);
                if(updateFileSizeResult!=null&&Integer.parseInt(updateFileSizeResult.get("code").toString()) == 200) {
                    log.info("更新文件下载大小:{},文件下载ID：{}",localSize,downloadId);
                }else{
                    log.error("更新文件下载大小失败:{},文件下载ID：{}",localSize,downloadId);
                    //TODO　：停止传输
                    break;
                }
            }
        }
        in.close();
        out.close();
        return ftpClient.completePendingCommand();
    }

    /**
     * 文件归档
     * @param fileId 文件ID
     * @param fileId
     * @return
     */
    boolean archive(String fileId,String year){
        String r = HttpClientUtil.doPost(basicManagerURL+"file/archive/data/"+portalUserInfo.getToken()+ "/" + fileId + "/" + year ,null);
        JSONObject archiveResult = (JSONObject) JSONObject.parse(r);
        if(archiveResult!=null&&Integer.parseInt(archiveResult.get("code").toString()) == 200) {
            log.info("自动归档成功，文件ID：{}",fileId);
            return true;
        }else{
            log.error("自动归档失败:{},文件ID：{}",fileId);
            return false;
        }
    }

    /**
     * 文件归档
     * @param fileId 文件ID
     * @param archiveInfo JSON类型数据
     * @return
     */
    boolean archive(String fileId,String archiveInfo,String year){
        String r = HttpClientUtil.sendJsonStr(basicManagerURL+"file/archive/form/" +portalUserInfo.getToken()+ "/" + fileId + "/" + year,archiveInfo);
        JSONObject archiveResult = (JSONObject) JSONObject.parse(r);
        if(archiveResult!=null&&Integer.parseInt(archiveResult.get("code").toString()) == 200) {
            log.info("自动归档成功，文件ID：{},归档信息：{}",fileId,archiveInfo);
            return true;
        }else{
            log.error("自动归档失败:{},文件ID：{},归档信息：{}",fileId,archiveInfo);
            return false;
        }
    }

    /**
     * 文件上传前校验
     * @param files
     * @return
     */
    FileOperationResult checkFilesBeforeUpload(List<FTPFile> files,String token,String subTypeId){
        FileOperationResult result = new FileOperationResult();
        //验证本地文件是否存在
        for(FTPFile file:files){
            String local = file.getPath()+SEPARATOR+file.getName();
            File f = new File(local);
            long localSize = f.length();
            if(!f.exists()||localSize==0){
                log.error("本地文件{}不存在",local);
                result.setStatus(FileOperationStatus.Local_File_No_Exit);
                result.getFailFiles().add(file.getName());
                return result;
            }
        }
        List<String> fileNames = files.stream().map(FTPFile::getName).collect(Collectors.toList());
        String r = HttpClientUtil.sendJsonStr(basicManagerURL+"file/normativeValidation/" +token+'/'+subTypeId ,JsonUtils.objectToJson(fileNames));
        JSONObject checkResult = (JSONObject) JSONObject.parse(r);
        if(checkResult!=null&&Integer.parseInt(checkResult.get("code").toString()) == 200) {
            log.info("文件校验成功,文件列表：{}",files);
            result.setStatus(FileOperationStatus.SUCCESS);
        }else{
            int code = Integer.parseInt(checkResult.get("code").toString());
            result.setStatus(FileOperationStatus.getByCode(code));
            List<String> errorFiles = (List<String>) checkResult.get("data");
            result.setFailFiles(errorFiles);
            log.error("文件校验失败,失败文件：{}",errorFiles);
        }
        return result;
    }

    /**
     * 上传单个文件
     * @param fileId
     * @param sourcePath
     * @param targetPath
     * @return
     * @throws Exception
     */
    boolean singleFileUpload(FTPClient ftpClient,String fileId, String sourcePath, String targetPath,String year)throws Exception{
        /**
         * 测试记录日志
         */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        Date date = new Date();
        try {
            String dateStringParse = sdf.format(date);
            log.error("{}  上传前 {} - {}",Thread.currentThread().getName(),fileId,dateStringParse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 上传前文件本地生成MD5
         */

        String md5 = FileMD5Util.getFileMD5(new File(sourcePath));
        log.error("本地md5校验完 ，MD5值 ：{}",md5);
        try {
            String dateStringParse = sdf.format(date);
            log.error("{}  本地md5校验完时间 {} - {} ",Thread.currentThread().getName(),fileId,dateStringParse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //更新文件上传状态-上传中
        HttpClientUtil.doPost(basicManagerURL+"file/updateFileStatus/" + portalUserInfo.getToken() + "/" + fileId + "/" + FileStatus.IN_UPLOAD.code() + "/" + year +"/" +md5,null);

        log.info("更新文件上传状态:{},文件ID：{}", FileStatus.IN_UPLOAD.message(),fileId);
        UploadStatus upload = upload(ftpClient,sourcePath, targetPath, fileId);
        FileStatus status ;
        //根据上传状态返回结果决定上传完成更新状态

        try {
            date = new Date();
            String dateStringParse = sdf.format(date);
            log.error("{}  上传完 {} - {}",Thread.currentThread().getName(),fileId,dateStringParse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(upload.equals(UploadStatus.Upload_New_File_Success)||upload.equals(UploadStatus.Upload_From_Break_Success)){
            //生成md5校验码
            md5 = getMD5FromServer(targetPath);
//            String md5 = "-1";
            try {
                date = new Date();
                String dateStringParse = sdf.format(date);
                log.error("{}  服务器端md5校验完 {} - {}，MD5值 ：{}",Thread.currentThread().getName(),fileId,dateStringParse,md5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //更新文件上传成功状态
            status = FileStatus.END_UPLOAD;
            String r = HttpClientUtil.doPost(getBasicManagerURL()+"file/updateFileStatus/" + portalUserInfo.getToken() + "/" + fileId + "/" + status.code() + "/" +year+ "/" +md5,null );
            JSONObject checkResult = (JSONObject) JSONObject.parse(r);
            if (checkResult != null && Integer.parseInt(checkResult.get("code").toString()) != 200) {
                //完整性校验失败，返回false删除文件
                log.info("文件上传失败，完整性校验失败：{},文件ID：{}",upload.message(),fileId);
                return false;
            }else{
                log.info("文件上传成功：{},文件ID：{}",upload.message(),fileId);
                return true;
            }
        }else {
            //更新文件上传失败状态
            status = FileStatus.ERROR_UPLOAD;
            HttpClientUtil.doPost(getBasicManagerURL()+"file/updateFileStatus/" + portalUserInfo.getToken() + "/" + fileId + "/" + status.code() + "/" +year,null );
            log.info("文件上传失败：{},文件ID：{}",upload.message(),fileId);
            return false;
        }
    }


    /**
     * 上传前信息初始化
     * @param mainTypeId
     * @param subTypeId
     * @param filesToUpload
     * @param targetDir
     * @return
     */
    UploadRequestDTO generateUploadRequestDTO(String mainTypeId, String subTypeId, List<String> tagList,List<FTPFile> filesToUpload, FTPDir targetDir){
        //上传任务信息
        UploadRequestDTO uploadInfo = new UploadRequestDTO();
        uploadInfo.setMainTypeId(mainTypeId);
        uploadInfo.setSubTypeId(subTypeId);
        uploadInfo.setCenterId(portalUserInfo.getCenter());
        uploadInfo.setUploadUser(portalUserInfo.getUserName());
        uploadInfo.setUploadMode(UploadMode.MODE_FTP.message());
        uploadInfo.setToken(portalUserInfo.getToken());
        uploadInfo.setCabinName(portalUserInfo.getCabin());

        //标签信息
        uploadInfo.setLabelsIds(tagList);

        //上传文件列表
        List<FileInfoDTO> fileInfos = new ArrayList<>();
        for (FTPFile ftpFile : filesToUpload) {
            FileInfoDTO f = new FileInfoDTO();
            String fileName = ftpFile.getName();
            //确保上传的路径不以‘/’结尾
            String sourceDir = ftpFile.getPath();
            if(sourceDir.endsWith(File.separator)){
                sourceDir = sourceDir.substring(0,sourceDir.lastIndexOf(File.separator));
            }else if(sourceDir.endsWith(SEPARATOR)){
                sourceDir = sourceDir.substring(0,sourceDir.lastIndexOf(SEPARATOR));
            }
            try{
                File fs = new File(sourceDir + SEPARATOR + fileName);
                f.setFileName(fileName);
                f.setFileSize(fs.length());
                f.setDirectoryId(targetDir.getId());
                f.setFileLastUpdateTime(fs.lastModified());
                fileInfos.add(f);
            }catch (Exception e){
                e.printStackTrace();
                log.error("初始化上传文件过程出错",e.getMessage());
            }
        }
        uploadInfo.setFileInfoDtos(fileInfos);
        return uploadInfo;
    }
    /**
     * 初始化上传信息
     * @param mainTypeId
     * @param subTypeId
     * @param filesToUpload
     * @return
     */
    Result<InitUploadInfo> initUploadInfo(String cabin,String mainTypeId, String subTypeId,List<String> tagList, List<FTPFile> filesToUpload) {
        //获取上传路径
        Result result = new Result();
        InitUploadInfo initResult = new InitUploadInfo();
        //将舱段信息存储到 portUserInfo
        portalUserInfo.setCabin(cabin);
        FTPDir targetDir = FTPUtil.getUploadDirPath(portalUserInfo.getToken(), basicManagerURL,cabin, portalUserInfo.getCenter(), mainTypeId, subTypeId);
        if (targetDir == null) {
            result.setErrorMsgInfo("获取上传路径失败");
        }
        initResult.setTargetDir(targetDir);

        //上传任务信息整理
        UploadRequestDTO uploadInfo = generateUploadRequestDTO(mainTypeId, subTypeId, tagList,filesToUpload, targetDir);

        //调用manager后台初始化上传信息
        String r = HttpClientUtil.sendJsonStr(basicManagerURL + "file/upload", JsonUtils.objectToJson(uploadInfo));
        JSONObject startUploadResult = (JSONObject) JSONObject.parse(r);
        if (startUploadResult != null && !("".equals(startUploadResult)) && Integer.parseInt(startUploadResult.getJSONObject("result").get("code").toString()) == 200) {
            UploadResponseDTO initUploadDTO = JsonUtils.jsonToPojo(r, UploadResponseDTO.class);
            Map<String, String> fileIds = initUploadDTO.getFiles();
            initResult.setFileIds(fileIds);
            initResult.setYear(initUploadDTO.getYear());

        } else {
            result.setErrorMsgInfo("调用manager后台初始化上传信息失败 ");
            if (null != startUploadResult && !("".equals(startUploadResult))) {
                String errorMessage =  startUploadResult.getJSONObject("result").get("message").toString();
                log.error("文件上传前初始化上传信息失败: {}",errorMessage);
                result.setMessage(errorMessage);
                int code = (int) startUploadResult.getJSONObject("result").get("code");
                result.setCode(code);
                List<String> failFiles = (List<String>) startUploadResult.getJSONObject("result").get("data");
                initResult.setFailFiles(failFiles);
            }
        }
        result.setData(initResult);
        return result;
    }
    /**
     * 上传文件到FTP服务器，支持断点续传
     *
     * @param local  本地文件名称，绝对路径
     * @param remote 远程文件路径，使用/home/directory1/subdirectory/file.ext 按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构
     * @return 上传结果
     * @throws Exception 异常
     */
    private UploadStatus upload(FTPClient ftpClient,String local, String remote,String fileId) throws Exception {
        //设置以二进制流的方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        //ftpClient.setControlEncoding("UTF-8");
        // 获取登录信息
        FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
        config.setServerLanguageCode("zh");
        ftpClient.configure(config);
        // 使用被动模式设为默认
        ftpClient.enterLocalPassiveMode();

        UploadStatus result;

        //对远程目录的处理
        String remoteFileName = remote;
        if (remote.contains(SEPARATOR)) {
            remoteFileName = remote.substring(remote.lastIndexOf(SEPARATOR) + 1);

            //创建服务器远程目录结构，创建失败直接返回
            if (checkDirectory(ftpClient,remote) == UploadStatus.Create_Directory_Fail) {
                log.error("上传文件前检测到远程文件路径不存在，远程文件路径：{}，本地路径：{}",remote,local);
                return UploadStatus.Create_Directory_Fail;
            }
        }

        //检查远程是否存在文件
        org.apache.commons.net.ftp.FTPFile[] files;
        files = ftpClient.listFiles(new String(remoteFileName.getBytes(controlEncoding), StandardCharsets.ISO_8859_1));
        if (files.length == 1) {
            log.info("检查远程存在要上传的文件：{}，断点续传",remote);
            long remoteSize = files[0].getSize();
            File f = new File(local);

            long localSize = f.length();

            if (remoteSize == localSize) {
                log.error("上传文件前 检验远程存在文件，远程文件名:{}，本地路径：{}",remote,local);
                return UploadStatus.File_Exits;
            } else if (remoteSize > localSize) {
                log.error("上传文件前 检验远程存在文件，远程文件大于本地文件,远程文件名:{}，本地路径：{}",remote,local);
                return UploadStatus.Check_File_Size_Failed;
            }

            //尝试移动文件内读取指针,实现断点续传
            result = uploadFile(remoteFileName, f, ftpClient, remoteSize,fileId);

            //如果断点续传没有成功，则删除服务器上文件，重新上传
            if (result == UploadStatus.Upload_From_Break_Failed) {
                log.info("断点续传文件 {} 没有成功，准备删除服务器上文件，重新上传",remote);
                if (!ftpClient.deleteFile(remoteFileName)) {
                    return UploadStatus.Delete_Remote_Faild;
                }
                result = uploadFile(remoteFileName, f, ftpClient, 0,fileId);
            }
        } else {
            File f = new File(local);
            long localSize = f.length();
            //上传前检查到远程没有要上传的文件，要用ftpclient先新建一个空文件
            String ftpRemoteFileName = new String(remoteFileName.getBytes(controlEncoding), StandardCharsets.ISO_8859_1);
            OutputStreamWriter osw = new OutputStreamWriter(ftpClient.appendFileStream(ftpRemoteFileName));
            osw.close();
            boolean isCreate = ftpClient.completePendingCommand();
            log.info("检查远程不存在要上传的文件：{}，在远程创建待上传文件结果：{}",remote,isCreate);
            result = uploadFile(remoteFileName, new File(local), ftpClient, 0,fileId);

        }

        log.info("上传结果：{}", result);
        return result;
    }

    /**
     * 断开与远程服务器的连接
     *
     * @throws IOException IO异常
     */
    public void disconnect(FTPClient ftpClient) throws IOException {
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();

        }
    }

    /**
     * 注销-清除用户登录信息
     */
    public void logout(){
        portalUserInfo.setGroup("");
        portalUserInfo.setCenter("");
        portalUserInfo.setToken("");
        portalUserInfo.setUserName("");
        portalUserInfo.setPassword("");
        portalUserInfo.setCabin("");
    }


//    /**
//     * 递归创建远程服务器目录
//     *
//     * @param remote    远程服务器文件绝对路径
//     * @return 目录创建是否成功
//     * @throws Exception
//     */
    private UploadStatus checkDirectory(FTPClient ftpClient, String remote)  {
        UploadStatus status = UploadStatus.Create_Directory_Success;
        try{
            String directory = remote.substring(0, remote.lastIndexOf(SEPARATOR) + 1);
            if (!directory.equalsIgnoreCase(SEPARATOR) && !ftpClient.changeWorkingDirectory(new String(directory.getBytes(controlEncoding), StandardCharsets.ISO_8859_1))) {
                status = UploadStatus.Create_Directory_Fail;
            }
            //ftpClient.changeWorkingDirectory("/");
        }catch (IOException e){
            status = UploadStatus.Create_Directory_Fail;
        }

        return status;
    }

    /**
     * 上传文件到服务器,新上传和断点续传
     *
     * @param remoteFile  远程文件名，在上传之前已经将服务器工作目录做了改变
     * @param localFile   本地文件File句柄，绝对路径
     * @param ftpClient   FTPClient引用
     * @return
     * @throws IOException
     */
    private UploadStatus uploadFile(String remoteFile, File localFile, FTPClient ftpClient, long remoteSize,String fileId) throws IOException {
        UploadStatus status;
        //显示进度的上传
        System.out.println("");
        long step = localFile.length() / 100;
        long process = 0;
        long speed = 0;
        long time = System.currentTimeMillis();
        long localreadbytes = 0L;
        RandomAccessFile raf = new RandomAccessFile(localFile, "r");
        //TODO: 编码格式 GBK  String.getBytes(String encode)方法会根据指定的encode编码返回某字符串在该编码下的byte数组表示  而与getBytes相对的，可以通过new String(byte[], decode)的方式来还原这个“中”字时，这个new String(byte[], decode)实际是使用decode指定的编码来将byte[]解析成字符串。
        OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes(controlEncoding), StandardCharsets.ISO_8859_1));
        //断点续传
        if (remoteSize > 0) {
            ftpClient.setRestartOffset(remoteSize);
            process = remoteSize / step;
            raf.seek(remoteSize);
            localreadbytes = remoteSize;
        }
        byte[] bytes = new byte[chunk];
        int c;
        System.out.print("raf"+raf);
        while ((c = raf.read(bytes)) != -1) {
            out.write(bytes, 0, c);
            localreadbytes += c;
            long curTime = System.currentTimeMillis()-time;
            curTime = curTime>0?curTime:1;
            speed = c/curTime;
            time = System.currentTimeMillis();
            if (localreadbytes / step != process) {
                process = localreadbytes / step;
                System.out.print("\r上传进度:" + process+"% 上传速度："+speed+" b/ms" +" "+Thread.currentThread().getName());
                out.flush();
                if(process==100){
                    System.out.println();
                }
            }
            //更新文件上传进度
            String r = HttpClientUtil.doPost(basicManagerURL+"file/updateFileSize/" + portalUserInfo.getToken() + "/" + fileId+ "/" + localreadbytes,null);
            JSONObject updateFileSizeResult = (JSONObject) JSONObject.parse(r);
            if(updateFileSizeResult!=null&&Integer.parseInt(updateFileSizeResult.get("code").toString()) == 200) {
                log.info("更新文件上传大小:{},文件ID：{}",localreadbytes,fileId);
            }else{
                status = UploadStatus.Manager_Connect_Failed;
                log.error("更新文件上传大小失败:{},文件下载ID：{}, 失败原因：{}，终止传输 ",localreadbytes,fileId,status.message());
                //TODO　：停止传输
                break;

            }
        }
        out.flush();
        raf.close();
        out.close();
        //手动终止传输流成功并且在传输过程中没有被终止
        boolean result = ftpClient.completePendingCommand();
        //更新文件上传进度
        HttpClientUtil.doPost(basicManagerURL + "file/updateFileSize/" + portalUserInfo.getToken() + "/" + fileId + "/" + localreadbytes, null);

        if (remoteSize > 0) {
            status = result ? UploadStatus.Upload_From_Break_Success : UploadStatus.Upload_From_Break_Failed;
            log.info("断点上传文件结果：{} 远程文件名：{}，本地文件名：{} ，上传进度：{}",status.message(),remoteFile,localFile);
        } else {
            status = result ? UploadStatus.Upload_New_File_Success : UploadStatus.Upload_New_File_Failed;
            log.info("全新上传文件结果：{} 远程文件名：{}，本地文件名：{} ，上传进度：{}",status.message(), remoteFile, localFile);
        }

        return status;
    }

    public FileOperationStatus deleteFile(FTPClient ftpClient,Long fileId,String path,String year) throws Exception{
            boolean isDel = ftpClient.deleteFile(path);
            if(isDel){
                //删除文件成功
                //通知manager删除文件成功
                HttpClientUtil.doPost(basicManagerURL+"file/updateFileStatus/" + portalUserInfo.getToken() + "/" + fileId + "/" + FileStatus.HAS_DEL.code()  + "/" +year,null);
                log.info("文件删除成功：{},文件ID：{}",path,fileId);
                return FileOperationStatus.SUCCESS;
            }else{
                //删除文件失败
                log.info("文件删除失败：{},文件ID：{}",path,fileId);
                return FileOperationStatus.DELFILE_ERROR;
            }
    }
    /**
     * 删除当前目录下的文件
     * @param filesToDelete 要删除文件名称
     * @return 删除文件状态码
     * @throws IOException IO异常
     */
    public FileOperationResult deleteFile(FTPClient ftpClient,List<FTPFile> filesToDelete,String year) throws Exception{
        FileOperationResult fileOperationResult = new FileOperationResult();
        List<String> failNames = new ArrayList<>() ;
        FileOperationStatus fileOperationStatus = FileOperationStatus.SUCCESS;

        for(FTPFile ftpFile :filesToDelete) {
            //调用权限验证 TODO:
            String r = HttpClientUtil.doPost(basicManagerURL+"file/updateFileStatus/" + portalUserInfo.getToken() + "/" + ftpFile.getId() + "/" + FileStatus.PERMISSION_VALIDATION.code() + "/" +year ,null);
            JSONObject permisionResult = (JSONObject) JSONObject.parse(r);
            if (permisionResult != null && Integer.parseInt(permisionResult.get("code").toString()) == 200) {
                //TODO: 路径此时是ID，转成真实绝对路径
                String path = ftpFile.getPath() + SEPARATOR + ftpFile.getName();
                boolean isDel = ftpClient.deleteFile(path);
                if(isDel){
                    //删除文件成功
                    //通知manager删除文件成功
                    HttpClientUtil.doPost(basicManagerURL+"file/updateFileStatus/" + portalUserInfo.getToken() + "/" + ftpFile.getId() + "/" + FileStatus.HAS_DEL.code() + "/" + year,null);
                    log.info("文件删除成功：{},文件ID：{}",path,ftpFile.getId());
                }else{
                    //删除文件失败
                    log.info("文件删除失败：{},文件ID：{}",path,ftpFile.getId());
                    fileOperationStatus = FileOperationStatus.DELFILE_ERROR;
                    failNames.add(ftpFile.getName());
                }
            }else{
                fileOperationStatus = FileOperationStatus.FORBIDDEN;
                failNames.add(ftpFile.getName());
            }
        }
        fileOperationResult.setFailFiles(failNames);
        fileOperationResult.setStatus(fileOperationStatus);
        return fileOperationResult;
    }

    /**
     * 列表查询目录列表-查询该数据类型下的数据列表
     * @param type 数据类型
     * @return  目录操作状态信息
     */
    public List<FTPDir> listDirectory(String type){
        //调用工具类实现
        return FTPUtil.dirList(basicManagerURL,type,portalUserInfo);
    }

    /**
     * 文件监控
     * @param filesToMonitor 文件名称
     * @return FileInfo 文件信息
     */
    public List<MFileInfo> monitor(String mainTypeId,String subTypeId,String startTime,String endTime, List <FTPFile> filesToMonitor) throws Exception{
        List<MFileInfo> fileInfo = new ArrayList<>();
        if(null == filesToMonitor ||filesToMonitor.size()==0){
            log.error("监控文件传入文件为个数为0 ");
            return fileInfo;
        }

        List <String> monitors= new ArrayList<>();

        for(FTPFile file : filesToMonitor){
            monitors.add(file.getName());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startTime",startTime);
        jsonObject.put("endTime",endTime);
        jsonObject.put("fileNames",monitors);
        String r = HttpClientUtil.sendJsonStr(basicManagerURL + "file/monitor/"+ portalUserInfo.getCabin() + "/" +portalUserInfo.getCenter()+ "/" + mainTypeId + "/" + subTypeId + "/" +portalUserInfo.getUserName() ,JsonUtils.objectToJson(jsonObject));
        JSONObject updateFileStateResult = (JSONObject) JSONObject.parse(r);
        if (updateFileStateResult != null && Integer.parseInt(updateFileStateResult.get("code").toString()) == 200) {
            fileInfo = JsonUtils.jsonToList(updateFileStateResult.get("data").toString(),MFileInfo.class);
            log.info("成功监控，文件信息：{}",fileInfo);
        }else{
            log.error("查询失败 {}",updateFileStateResult);
        }
        return fileInfo;
    }

    /**
     * 文件查询
     * @param mainType 数据大类
     * @param subType 数据小类
     * @param queryParams 查询参数
     * @param pageSize 分页大小
     * @param pageNumber 页码
     * @return
     * @throws Exception
     */
    public List <FTPFile>searchFile(String mainType, String subType, QueryParam queryParams, int pageSize, int pageNumber)throws Exception{
        List<FTPFile> FTPFiles = new ArrayList<>();
        String r = HttpClientUtil.sendJsonStr(basicManagerURL + "/file/searchFile/" + portalUserInfo.getToken() + "/" +portalUserInfo.getCenter()+"/" + mainType + "/" + subType + "/" + pageSize+ "/" + pageNumber, JsonUtils.objectToJson(queryParams));
        JSONObject searchResult = (JSONObject) JSONObject.parse(r);
        if (searchResult != null && Integer.parseInt(searchResult.get("code").toString()) == 200) {
            FTPFiles =  (List<FTPFile>)JsonUtils.jsonToList(searchResult.getJSONArray("data").toString(), FTPFile.class);
        }
        log.info("文件查询结果：{}",FTPFiles);
        return FTPFiles;
    }

    /**
     * 根据数据类型查看该文件夹下所有的文件详细信息
     * @param mainType
     * @param subType
     * @return
     */
    public List<FTPFileInfo> listFile(String mainType, String subType){
        List<FTPFileInfo> FTPFiles = new ArrayList<>();
        String r = HttpClientUtil.sendJsonStr(basicManagerURL + "/file/getFTPFileInfo/" + portalUserInfo.getToken() + "/" +portalUserInfo.getCenter()+"/" + mainType + "/" + subType,null);
        JSONObject listResult = (JSONObject) JSONObject.parse(r);
        if (listResult != null && Integer.parseInt(listResult.get("code").toString()) == 200) {
            FTPFiles = (List<FTPFileInfo>) JsonUtils.jsonToList(listResult.getJSONArray("data").toString(), FTPFileInfo.class);
        }
        log.info("列表文件结果：{}",FTPFiles);
        return FTPFiles;
    }
    /**
     * 从配置文件获取属性：分片大小
     * @return
     */
    private int readChunkProperty(){
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("FTPTransfer.properties");
        Properties p = new Properties();
        int chunk = 2*1024*1024;
        try {
            p.load(inputStream);
            chunk = Integer.parseInt(p.getProperty("chunk"));
            inputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return chunk;
    }
    /**
     * 从配置文件获取属性：数据传输管理模块的访问前缀
     * @return
     */
    private String readBasicManagerURL(){
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("FTPTransfer.properties");
        Properties p = new Properties();
        String basicManagerURL = "";
        try {
            p.load(inputStream);
            basicManagerURL = p.getProperty("basicManagerURL");
            inputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return basicManagerURL;
    }
    /**
     * 从配置文件获取属性：数据传输管理模块的访问前缀
     * @return
     */
    private String readBasicServerURL(){
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("FTPTransfer.properties");
        Properties p = new Properties();
        String basicServerURL = "";
        try {
            p.load(inputStream);
            basicServerURL = p.getProperty("basicServerURL");
            inputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return basicServerURL;
    }

    /**
     * 从配置文件获取属性：支持并发线程数量
     * @return
     */
    private int readThreadProperty(){
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("FTPTransfer.properties");
        Properties p = new Properties();
        int thread = 8;
        try {
            p.load(inputStream);
            thread = Integer.parseInt(p.getProperty("thread"));
            inputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return thread;
    }
    /**
     * 获取数据传输管理访问前缀
     * @return
     */
    public String getBasicManagerURL() {
        if (null == basicManagerURL){
            basicManagerURL = readBasicManagerURL();
        }
        return basicManagerURL;
    }

    /**
     * 获取全部标签列表
     */
    public List getTags(){
        List<Tag> tags = new ArrayList<>();
        String r = HttpClientUtil.sendJsonStr(basicManagerURL + "/tags/getAllTags/" + portalUserInfo.getToken() ,null);
        JSONObject listResult = (JSONObject) JSONObject.parse(r);
        if (listResult != null && Integer.parseInt(listResult.get("code").toString()) == 200) {
            tags = (List<Tag>) JsonUtils.jsonToList(listResult.getJSONArray("data").toString(), Tag.class);
        }
        log.info("列表标签结果：{}",tags);
        return tags;
    }

    /**
     * 调用http server 获取 MD5
     * @param targetPath
     * @return
     * @throws Exception
     */
    String getMD5FromServer(String targetPath)throws Exception{
        String md5 =null;
        String r = HttpClientUtil.sendJsonStr(basicServerURL + "/file/MD5?path="+targetPath,null);
        JSONObject result = (JSONObject) JSONObject.parse(r);
        if (result != null && Integer.parseInt(result.get("code").toString()) == 200) {
            Result<String> md5Result = (Result<String>) JsonUtils.jsonToPojo(r,Result.class);
            md5 = md5Result.getData();
        }
        return md5;
    }
    public FTPClient getFTPClient() {
        FTPClient ftpClient = null;
        try{
            if (ftpClientThreadLocal.get() != null && ftpClientThreadLocal.get().isConnected()) {
                return ftpClientThreadLocal.get();
            } else {
                ftpClient = new FTPClient(); //构造一个FtpClient实例
                boolean b = connect(ftpClient);
                ftpClientThreadLocal.set(ftpClient);
                System.out.println("这是"+Thread.currentThread().getName()+" connect :"+b);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ftpClient;
    }
}

