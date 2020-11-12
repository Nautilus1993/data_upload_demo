package com.bjsasc.FTPClientSDK.service;

import com.bjsasc.FTPClientSDK.model.entry.*;
import com.bjsasc.FTPClientSDK.model.entry.HTY.BaseFile;
import com.bjsasc.FTPClientSDK.model.entry.HTY.DataResultAchieveReportFile;
import com.bjsasc.FTPClientSDK.model.enu.QueryParamEnum;
import org.apache.commons.cli.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

class Main {
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("h", "help", false, "print options' information");

        /**
         * 初始化FTPTransferClient 实例
         */
//        //方法名称
//        Option ftpTransferClient = new Option("F", "FTPTransferClient", false, "init FTPTransferClient function");
//        ftpTransferClient.setRequired(false);
//        options.addOption(ftpTransferClient);
        //参数名称
        Option username = new Option("u", "username", true, "input username");
        username.setRequired(false);
        options.addOption(username);

        Option password = new Option("p", "password", true, "input username");
        password.setRequired(false);
        options.addOption(password);

//        /**
//         * 连接到远程服务器
//         */
//        //方法名称
//        Option connect = new Option("C", "connect", false, "connect function");
//        connect.setRequired(false);
//        options.addOption(connect);

        /**
         * 断开连接到远程服务器
         */
        //方法名称
        Option disconnect = new Option("LO", "logOut", false, "disconnect function");
        disconnect.setRequired(false);
        options.addOption(disconnect);

        /**
         * 获取标签
         */
        //方法名称
        Option connect = new Option("T", "tagList", false, "get all tagList ");
        connect.setRequired(false);
        options.addOption(connect);


        /**
         * 调用upload上传方法
         */
        //方法名称
        Option upload = new Option("UL", "upload", false, "upload file function");
        upload.setRequired(false);
        options.addOption(upload);
        //参数名称-文件位置
        Option location = new Option("l", "location", true, "input file location");
        location.setRequired(false);
        options.addOption(location);
        //参数名称-舱段
        Option cabin = new Option("c", "cabin", true, "input cabin");
        cabin.setRequired(false);
        options.addOption(cabin);
        //参数名称-大类
        Option mainType = new Option("m", "mainType", true, "input file mainType");
        mainType.setRequired(false);
        options.addOption(mainType);
        //参数名称-小类
        Option subType = new Option("s", "subType", true, "input file subType");
        subType.setRequired(false);
        options.addOption(subType);

        //参数名称-上传文件带标签 withValueSeparator(char sep)指定参数值之间的分隔符
        Option tags = OptionBuilder.withArgName("args")
                .withLongOpt("tags")
                .hasArgs()
                .withValueSeparator(',')
                .withDescription("tag ids")
                .create("t");
        options.addOption(tags);

        //参数名称-上传文件列表 withValueSeparator(char sep)指定参数值之间的分隔符
        Option files = OptionBuilder.withArgName("args")
                .withLongOpt("files")
                .hasArgs()
                .withValueSeparator(',')
                .withDescription("file names")
                .create("f");
        options.addOption(files);

        //参数名称-归档元信息
        Option info = OptionBuilder.withArgName("property=name")
                .withLongOpt("info")
                .hasArgs()
                .withValueSeparator()
                .withDescription("use value for a property info")
                .create("i");
        options.addOption(info);

        /**
         * 调用 search 搜索文件方法
         */
        Option search = new Option("SE", "search", false, "search file function");
        search.setRequired(false);
        options.addOption(search);

        //参数名称-当前页
        Option number = new Option("n", "number", true, "input page number");
        number.setRequired(false);
        options.addOption(number);

        //参数名称-页码大小
        Option size = new Option("z", "size", true, "input page size");
        size.setRequired(false);
        options.addOption(size);

        //参数名称-归档元信息
        Option param = OptionBuilder.withArgName("property=name")
                .withLongOpt("query")
                .hasArgs()
                .withValueSeparator()
                .withDescription("use value for a query param")
                .create("q");
        options.addOption(param);

        /**
         * 调用 download 下载文件方法
         */
        Option download = new Option("DL", "download", false, "download file function");
        download.setRequired(false);
        options.addOption(download);



        CommandLineParser parser = new GnuParser();

        //传输全局变量
        FTPTransferClient myFtp = null;
        final FileOperationResult[] result = new FileOperationResult[1];
        try {
            CommandLine cli = parser.parse(options, args);
            if(cli.hasOption("h")){
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp("Options", options);
            }
            /**
             * -F 初始化方法
             */
            if (true){
                System.out.println("初始化");
                if(cli.hasOption("u") && cli.hasOption("p")){ //-u -p
                    String USERNAME = cli.getOptionValue("u");
                    String PASSWORD = cli.getOptionValue("p");
                    myFtp = new FTPTransferClient(USERNAME,PASSWORD);
                }
            }
            /**
             *-C 连接方法
             */
//            if (cli.hasOption("C")){
//                System.out.println("进入连接方法");
//                isSuccess = myFtp.connect();
//                System.out.println("连接结果 ："+isSuccess);
//            }
            /**
             *-DC 断开连接方法
             */
//            if (cli.hasOption("DC")){
//                System.out.println("进入断开连接方法");
//                myFtp.disconnect();
//            }
            /**
             *-UL 上传方法
             */
            if (cli.hasOption("UL")){
                System.out.println("进入上传方法");
                List<String> tagLabels = new ArrayList<>();
                List<FTPFile> ftpFiles = new ArrayList<>();
                Map<FTPFile, BaseFile> filesToUpload = new HashMap<>();
                String LOCATION = null;
                String CABIN = null;
                String MAINTYPE = null;
                String SUBTYPE = null;
                DataResultAchieveReportFile dataResult = null;

                //上传信息

                if(cli.hasOption("t") && cli.hasOption("f") && cli.hasOption("l")  && cli.hasOption("c") && cli.hasOption("m") && cli.hasOption("s")){ //-u -p
                    System.out.println("获取上传信息");
                    tagLabels = Arrays.asList(cli.getOptionValues("t"));
                    String[] FILENAMES = cli.getOptionValues("f");
                    LOCATION = cli.getOptionValue("l");
                    CABIN = cli.getOptionValue("c");
                    MAINTYPE = cli.getOptionValue("m");
                    SUBTYPE = cli.getOptionValue("s");

                    for (String FILENAME:FILENAMES){
                        ftpFiles.add(new FTPFile(FILENAME,LOCATION));
                    }
                }else{
                    System.out.println("参数不全");
                }

                //归档信息
                if(cli.hasOption("i")){
                    System.out.println("进入归档信息");
                    Properties properties = cli.getOptionProperties("i");
                    String FORMAT = properties.getProperty("format");
                    String ACHIEVENAME = properties.getProperty("achieveName");
                    String FOUNDER = properties.getProperty("founder");
                    String FOUNDERTIME = properties.getProperty("founderTime");
                    String ACHIEVEFROM = properties.getProperty("achieveFrom");
                    String ACHIEVEKIND = properties.getProperty("achieveKind");
                    String ACHIEVECOPYRIGHT = properties.getProperty("achieveCopyright");
                    String DESCOFACHIEVE = properties.getProperty("descOfAchievs");

                    dataResult = new DataResultAchieveReportFile();
                    if(null!= FORMAT){
                        dataResult.setFormat(FORMAT);
                    }
                    if(null!= FOUNDER) {
                        dataResult.setFounder(FOUNDER);
                    }
                    //创建SimpleDateFormat对象实例并定义好转换格式
                    if(null!=FOUNDERTIME ) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dataResult.setFounderTime(sdf.parse(FOUNDERTIME));
                    }
                    if(null!= ACHIEVECOPYRIGHT) {
                        dataResult.setAchieveCopyright(ACHIEVECOPYRIGHT);
                    }
                    if(null!= ACHIEVEFROM) {
                        dataResult.setAchieveFrom(ACHIEVEFROM);
                    }
                    if(null!= ACHIEVEKIND) {
                        dataResult.setAchieveKind(ACHIEVEKIND);
                    }
                    if(null!= DESCOFACHIEVE) {
                        dataResult.setDescOfAchievs(DESCOFACHIEVE);
                    }
                    if(null!= ACHIEVENAME) {
                        dataResult.setAchieveName(ACHIEVENAME);
                    }
                    System.out.println("退出归档信息");
                }

                //有归档信息上传方法
                if(dataResult!=null){
                    System.out.println("进入有归档信息上传方法");
                    filesToUpload.put(ftpFiles.get(0),dataResult);
                    // 文件操作状态结果
                    FTPTransferClient finalMyFtp = myFtp;
                    String finalCABIN = CABIN;
                    String finalMAINTYPE = MAINTYPE;
                    String finalSUBTYPE = SUBTYPE;
                    List<String> finalTagLabels = tagLabels;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println("开始调用上传");
                                result[0] = finalMyFtp.startUpload(finalCABIN, finalMAINTYPE, finalSUBTYPE, finalTagLabels,ftpFiles);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    /************   文件监控-上传监控    ************/
                    TimeUnit.MILLISECONDS.sleep(2000);
                    List<MFileInfo> uploadMmFileInfo ;
                    boolean isMonitor = true;
                    while (null==result[0]||isMonitor){
                        TimeUnit.MILLISECONDS.sleep(1000);
                        uploadMmFileInfo = myFtp.monitor(MAINTYPE,SUBTYPE,"2020-10-14 15:36:02","2020-12-24 23:36:02",ftpFiles);
                        //TODO: 打印进度，上传速度 kb/s
                        isMonitor = false;//结束监控
                        for(MFileInfo in : uploadMmFileInfo){
                            System.out.println(in.getPercentage()+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            if(!in.getPercentage().equals("100%")){
                                System.out.println(in.getFileName()+" kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk靠靠靠靠靠靠靠靠靠靠靠靠靠靠靠靠靠靠靠 "+in.getPercentage()+" "+in.getSpeed());
                                isMonitor = true;//继续监控
                            }
                        }
                    }
                    System.out.println("上传结果："+result[0].getStatus());
                    System.out.println("断开连接");
                //无归档信息上传方法
                }else{
                    System.out.println("无归档信息上传方法");
                    result[0] = myFtp.startUpload(CABIN, MAINTYPE, SUBTYPE,tagLabels,ftpFiles);

                    System.out.println("上传结果:"+result[0].getStatus());
                    System.out.println("断开连接");

                }
            }
            /**
             *-SE 搜索方法
             */
            if(cli.hasOption("SE")){
                System.out.println("进入搜索方法");
                int NUMBER = 0;
                int SIZE = 0;
                String MAINTYPE = null;
                String SUBTYPE = null;
                QueryParam queryParam =null;
                if(cli.hasOption("n")&&cli.hasOption("z")&&cli.hasOption("m")&&cli.hasOption("s")){
                    NUMBER = Integer.parseInt(cli.getOptionValue("n"));
                    SIZE = Integer.parseInt(cli.getOptionValue("z"));
                    MAINTYPE = cli.getOptionValue("m");
                    SUBTYPE = cli.getOptionValue("s");
                }
                if(cli.hasOption("q")){
                    System.out.println("进入查询条件");
                    //查询条件
                    Properties properties = cli.getOptionProperties("q");
                    queryParam = new QueryParam();
                    String UPLOADTIME = properties.getProperty("uploadTime");
                    //TODO: 查询参数补全
                    queryParam.setParam(QueryParamEnum.UPLOAD_TIME,UPLOADTIME);
                }
                List<FTPFile> filesToDownload = myFtp.searchFile(MAINTYPE,SUBTYPE,queryParam,SIZE,NUMBER);
                for(FTPFile f : filesToDownload){
                    System.out.println(f.getName()+" "+f.getPath()+" "+f.getId());
                }
            }

//            /**
//             *-DL 下载方法
//             */
//            if(cli.hasOption("DL")){
//                System.out.println("进入下载方法");
//                int NUMBER = 0;
//                int SIZE = 0;
//                String MAINTYPE = null;
//                String SUBTYPE = null;
//                String LOCATION = null;
//                QueryParam queryParam =null;
//                if(cli.hasOption("n")&&cli.hasOption("z")&&cli.hasOption("m")&&cli.hasOption("s")&&cli.hasOption("l")){
//                    NUMBER = Integer.parseInt(cli.getOptionValue("n"));
//                    SIZE = Integer.parseInt(cli.getOptionValue("z"));
//                    MAINTYPE = cli.getOptionValue("m");
//                    SUBTYPE = cli.getOptionValue("s");
//                    LOCATION = cli.getOptionValue("l");
//                }
//                if(cli.hasOption("q")){
//                    //查询条件
//                    Properties properties = cli.getOptionProperties("q");
//                    queryParam = new QueryParam();
//                    String UPLOADTIME = properties.getProperty("uploadTime");
//                    //TODO: 查询参数补全
//                    queryParam.setParam(QueryParamEnum.UPLOAD_TIME,UPLOADTIME);
//                }
//                List<FTPFile> filesToDownload = myFtp.searchFile(MAINTYPE,SUBTYPE,queryParam,SIZE,NUMBER);
//                result[0] = myFtp.startDownload(LOCATION,filesToDownload);
//                System.out.println("下载结果"+result[0].getStatus());
//        }
            /**
             * -T 获取所有标签方法
              */
            if(cli.hasOption("T")) {
                System.out.println("进入获取所有标签方法");
                List<Tag> tagList =  myFtp.getTags();
                for(Tag tag:tagList){
                    System.out.println(tag.getTagName()+" : "+tag.getTagID());
                }
            }

        }catch (Exception e){

        }

    }
}
