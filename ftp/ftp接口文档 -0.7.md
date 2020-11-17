## 运行使用说明：

1. 从 ftp 目录下载jar包（FTPClientSDK.jdk）https://github.com/Nautilus1993/data_upload_demo/

2. 下载并打开测试示例文档 testRawData.java，导入FTPClientSDK.jdk

3. 根据实际情况，修改localPath（上传文件路径），file（上传文件名称），upload_time（查询参数等），downloadPath（下载文件路径）

   ------

   

### 类：FTPTransferClient

#### 方法：

#### 1.FTPTransferClient()


```java
public FTPTransferClient(String userName,String password)
```

##### 方法功能：构造函数

| 参数     | 解释         |
| -------- | ------------ |
| userName | portal用户名 |
| password | 密码         |

##### 返回值：无

##### 使用示例

```java
String USERNAME = "test_user_1";//用户名
String PASSWORD = "QWER1234";//密码
// 初始化FTPTransferClient 实例
FTPTransferClient myFtp = new FTPTransferClient(USERNAME, PASSWORD)
```



#### 2.searchFiles()

```java
public List<FTPFile> searchFiles(String mainType,
                                 String subType,
                                 QueryParam query_params，
                                 int page_size,
                                 int page_number)throws Exception
```

##### 方法功能：分页搜索文件

| 参数         | 解释                           |
| ------------ | ------------------------------ |
| mainType     | 数据类型大类                   |
| subType      | 数据类型小类                   |
| query_params | 查询参数                       |
| page_size    | 每页数量                       |
| page_number  | 当前页，从1开始（为0查询所有） |

##### 返回值：文件信息集合List<FTPFile>

##### 使用示例

```java
int pageSize = 10; //分页大小 
int pageNum = 1; //页码
String mainType = "main_001";  // 数据大类：归档数据
String subType = "sub_001";    // 数据小类：原始数据文件
//查询条件
QueryParam queryParam = new QueryParam();
String upload_time = "2020-11-12";//上传时间
queryParam.setParam(queryParam.getParam("upload_time"),upload_time);
//查询
List<FTPFile> filesToDownload = myFtp.searchFile(mainType,subType,                 queryParam,pageSize,pageNum);
```

##### 异常：Exception

```
NullPointerException：查询参数不存在
RuntimeException：查询参数不合法
```

#### 3.startDownload()

##### 3.1 根据search方法返回结果下载

```java
public FileOperationResult startDownload(String localPath,List<FTPFile> filesToDownload)throws Exception
```

##### 方法功能：文件下载

| 参数            | 解释             |
| --------------- | ---------------- |
| filesToDownload | 要下载的文件列表 |
| localPath       | 下载到的本地路径 |

##### 返回值：FileOperationResult文件操作结果

| 类型                   | 参数            | 解释                   |
| ---------------------- | --------------- | ---------------------- |
| FileOperationStatus    | status          | 文件的操作状态         |
| List<String>           | failFiles       | 文件操作失败的文件列表 |
| Date                   | acceptDateEnd   | 结束接收时间           |
| Date                   | acceptDateStart | 开始接收时间           |
| Integer                | dataCount       | 数据量-文件个数        |
| String                 | dataSource      | 数据来源-文件中心      |
| String                 | mainType        | 数据大类               |
| String                 | subType         | 数据小类               |
| Map<String,FileResult> | fileResults     | 数据操作结果           |

##### FileOperationStatus枚举状态

| 枚举常量       | 解释                                  |
| -------------- | ------------------------------------- |
| SUCCESS        | 操作成功                              |
| DOWNLOAD_ERROR | 下载失败-本地文件已存在或者网络因素等 |
| FORBIDDEN      | 访问受限，授权过期                    |

##### 使用示例

```java
String downloadPath = "D:\\hanbing"; //下载到本地路径
FileOperationResult result = myFtp.startDownload(downloadPath,
                           filesToDownload);//filesToDownload 文件查询结果
```

##### 3.2 直接下载方法

```Java
public FileOperationResult startDownload(String mainType, 
                                         String subType,
                                         String localDir, 
                                         List<String>fileNames) throws Exception 
```

##### 方法功能：根据文件名称直接下载文件

| 参数            | 解释                 |
| --------------- | -------------------- |
| mainType        | 数据类型大类         |
| subType         | 数据类型小类         |
| localPath       | 下载到的本地路径     |
| filesToDownload | 要下载的文件名称列表 |

##### 返回值：FileOperationResult 文件操作结果

##### 使用示例

```java
String downloadPath = "D:\\hanbing"; //下载到本地路径;
List<String> fileNames = new ArrayList<>();
fileNames.add(file);//文件名称列表
String mainType = "main_001";  // 数据大类：归档数据
String subType = "sub_001";    // 数据小类：原始数据文件
FileOperationResult result = myFtp.startDownload(mainType, subType, downloadPath, fileNames);
```

##### 

#### 4. getTags()

```java
public List getTags()
```

##### 方法功能：获取所有标签

##### 返回值：所有标签结果List<Tag> 

##### Tag类属性：

| 类型   | 属性名  | 解释   |
| ------ | ------- | ------ |
| String | tagName | 标签名 |
| String | tagID   | 标签ID |

##### 使用示例

```java
//标签
List<Tag> tagList = myFtp.getTags();
```



#### 5. startUpload()

##### 5.1 从文件名提取元信息方法上传

```java
public FileOperationResult startUpload(String cabin，
                                String mianType,
                                String subType,
                                List<String> tagList,
                                List<FTPFile> filesToUpload)throws Exception
```

##### 方法功能：用户开始上传

| 参数          | 解释               |
| ------------- | ------------------ |
| cabin         | 舱段代号           |
| mianType      | 数据类型大类代号   |
| subType       | 数据类型小类代号   |
| tagList       | 标签列表           |
| filesToUpload | 批量上传的文件集合 |

##### 返回值：FileOperationResult文件操作结果

| 类型                   | 参数            | 解释                   |
| ---------------------- | --------------- | ---------------------- |
| FileOperationStatus    | status          | 文件的操作状态         |
| List<String>           | failFiles       | 文件操作失败的文件列表 |
| Date                   | acceptDateEnd   | 结束接收时间           |
| Date                   | acceptDateStart | 开始接收时间           |
| Integer                | dataCount       | 数据量-文件个数        |
| String                 | dataSource      | 数据来源-文件中心      |
| String                 | mainType        | 数据大类               |
| String                 | subType         | 数据小类               |
| Map<String,FileResult> | fileResults     | 数据操作结果           |

##### FileOperationStatus枚举状态

| 枚举常量                     | 解释                            |
| ---------------------------- | ------------------------------- |
| CHECK_REPEATE_FILESELF_ERROR | 检查重复性失败-文件本身存在重复 |
| CHECK_REPEATE_UPLOAD_ERROR   | 检查重复性失败-文件已经被上传   |
| CHECK_SIZE_ERROR             | 检查一致性失败                  |
| CHECK_STANDARD_ERROR         | 检查规范性失败                  |
| UPLOAD_ERROR                 | 上传失败                        |
| SUCCESS                      | 操作成功                        |
| FORBIDDEN                    | 访问受限，授权过期              |

##### 使用示例

```java
List<FTPFile> ftpFiles = new ArrayList<>();// 待上传文件列表
String localPath = "C:\\Users\\qrs\\Desktop";//本地文件所在目录
String file = "CT_TL1A2_SZ12_20201028052556_20201028052556_20201028052556_M_00001.raw";//文件名
ftpFiles.add(new FTPFile(file,localPath));//文件加入待上传列表
String mainType = "main_001";  // 数据大类：归档数据
String subType = "sub_001";    // 数据小类：原始数据文件
String cabin = "cabin_001";   //舱段ID
List<String>tagLabels = new ArrayList<>();//添加标签
tagLabels.add("40e8e2517c96482c90ea0aba1ce637b0");
// 文件上传方法
FileOperationResult result = myFtp.startUpload(cabin,mainType,subType, tagLabels,ftpFiles);
```

##### 5.2 元信息方法上传

```java
public FileOperationResult startUploadWithInfo(String cabin, 
                               String mainTypeId, 
                               String subTypeId, 
                               List<String> tagList, 
                               Map<FTPFile, BaseFile>uploadFileMap)  throws Exception
```

##### 方法功能：用户开始上传

| 参数          | 解释                             |
| ------------- | -------------------------------- |
| cabin         | 舱段代号                         |
| mianType      | 数据类型大类代号                 |
| subType       | 数据类型小类代号                 |
| tagList       | 标签列表                         |
| uploadFileMap | 批量上传的文件和对应归档信息集合 |

##### 返回值：FileOperationResult文件操作结果

##### 使用示例

```java
Map<FTPFile, BaseFile> filesToUpload = new HashMap<>();// 待上传文件列表
String localPath = "C:\\Users\\qrs\\Desktop";//本地文件所在目录
String file = "CT_TL1A2_SZ12_20201028052556_20201028052556_20201028052556_M_00001.raw";//文件名
FTPFile ftpFile = new FTPFile(file,localPath);
//归档信息
DataResultAchieveReportFile reportFile = new DataResultAchieveReportFile();
reportFile.setFormat("数据成果格式");
reportFile.setAchieveName("数据成果名称");
reportFile.setFounder("创建人");
reportFile.setAchieveFrom("来源");
reportFile.setAchieveKind("类型");
reportFile.setAchieveCopyright("版权方");
reportFile.setDescOfAchievs("描述");
reportFile.setFounderTime(new Date());
filesToUpload.put(ftpFile,reportFile);//文件加入待上传列表
String mainType = "main_001";  // 数据大类：归档数据
String subType = "sub_001";    // 数据小类：原始数据文件
String cabin = "cabin_001";   //舱段ID
List<String>tagLabels = new ArrayList<>();//添加标签
tagLabels.add("40e8e2517c96482c90ea0aba1ce637b0");
// 文件上传方法
FileOperationResult result = myFtp.startUpload(cabin,mainType,subType, tagLabels,ftpFiles);
```

##### 

#### 6. deleteFile()

```java
public FileOperationResult deleteFile(List<FTPFile> filesToDelete)throws Exception
```

##### 方法功能：删除文件

| 参数          | 解释             |
| ------------- | ---------------- |
| filesToDelete | 要删除的文件列表 |

##### 返回值：FileOperationResult文件操作结果

| 类型                   | 参数            | 解释                   |
| ---------------------- | --------------- | ---------------------- |
| FileOperationStatus    | status          | 文件的操作状态         |
| List<String>           | failFiles       | 文件操作失败的文件列表 |
| Date                   | acceptDateEnd   | 结束接收时间           |
| Date                   | acceptDateStart | 开始接收时间           |
| Integer                | dataCount       | 数据量-文件个数        |
| String                 | dataSource      | 数据来源-文件中心      |
| String                 | mainType        | 数据大类               |
| String                 | subType         | 数据小类               |
| Map<String,FileResult> | fileResults     | 数据操作结果           |

##### FileOperationStatus枚举状态

| 枚举常量      | 解释               |
| ------------- | ------------------ |
| FORBIDDEN     | 访问受限，授权过期 |
| DELFILE_ERROR | 删除文件失败       |
| SUCCESS       | 操作成功           |

##### 使用示例

```java
FileOperationResult result = myFtp.deleteFile(filesToDelete)；//filesToDelete是搜索出来的结果
```

#### 7.monitor()

```java
public List<MFileInfo> monitor(String mainTypeId,
                               String subTypeId,
                               String startTime,
                               String endTime, 
                               List<FTPFile> filesToMonitor)
```

##### 方法功能：文件监控

| 参数           | 解释     |
| -------------- | -------- |
| mainTypeId     | 大类     |
| subTypeId      | 小类     |
| startTime      | 开始时间 |
| endTime        | 结束时间 |
| filesToMonitor | 文件集合 |

##### 返回值：MFileInfo 监控文件信息（见MFileInfo类）

##### 使用示例

```java
String mainType = "main_001";  // 数据大类：归档数据
String subType = "sub_001";    // 数据小类：原始数据文件            
String startTime = "2020-10-14 15:36:02";//开始监控时间
String endTime = "2020-10-24 15:36:02";//结束监控时间
//监控方法
List<MFileInfo> monitorFileInfo = myFtp.monitor(mainType,subType,startTime,endTime,
    filesToMonitor);//filesToMonitor是上传或者下载的文件列表
```

#### 8. listFile()

```java
public List<FTPFileInfo> listFile(String mainType, String subType)
```

##### 方法功能：根据数据类型查看所有的文件详细信息

##### 返回值：所有文件列表

##### Tag类属性：

| 类型   | 属性名   | 解释     |
| ------ | -------- | -------- |
| String | mainType | 数据大类 |
| String | subType  | 数据小类 |

##### 使用示例

```java
String mainType = "main_001";     // 数据大类：归档数据
String subType = "sub_001";          // 数据小类：原始数据文件
//列表所有文件
List<FTPFileInfo> listFiles = myFtp.listFile(mainType,subType);
```

#### 9. logout()

```java
public void logout()
```

##### 方法功能：退出登录

### 类：MFileInfo

#### 属性：

| 类型   | 属性名          | 解释               |
| ------ | --------------- | ------------------ |
| Long   | currentSize     | 文件目前大小       |
| String | fileName        | 文件名称           |
| Long   | fileSize        | 文件大小           |
| String | percentage      | 文件目前大小百分比 |
| Date   | operationTime   | 文件操作时间       |
| String | operationStatus | 文件操作状态       |



### 类：FTPFile

#### 属性：

| 类型   | 属性名 | 解释         |
| ------ | ------ | ------------ |
| String | id     | ID           |
| String | name   | 文件名       |
| String | path   | 文件路径     |
| String | year   | 文件上传年份 |

### 类：QueryParam

#### 方法：

获取所有参数项

``` java
public Map<String,Param> getParams()
```

获取某一个参数项

```java
public Param getParam(String name)
```

| 类型   | 属性名 | 解释     |
| ------ | ------ | -------- |
| String | name   | 参数名字 |

设置查询值

```java
public void setParam(Param param, String paramValue)
```

| 类型   | 属性名     | 解释     |
| ------ | ---------- | -------- |
| Param  | param      | 查询参数 |
| String | paramValue | 参数值   |

用法

```java
//查询条件 初始化
QueryParam queryParam = new QueryParam();
//查看所有查询字段param
Map<String,Param> map = queryParam.getParams();
String upload_time = "2020-11-12";//上传时间
//设置查询条件
queryParam.setParam(queryParam.getParam("upload_time"),upload_time);
```

### 类：FileResult 

#### 属性：

| 类型                | 属性名       | 解释         |
| ------------------- | ------------ | ------------ |
| String              | name         | 文件名       |
| int                 | originalSize | 文件原始大小 |
| int                 | size         | 文件大小     |
| FileOperationStatus | status       | 操作状态     |

### 类：Param

| 查询参数名称 | 解释                  | 查询值示例  |
| ------------ | --------------------- | ----------- |
| dlsc         | 下行舱段标识/任务标识 | TGTH        |
| dtg          | 数据集合标识          | GCYC        |
| dty          | 数据类型标识          | GCYC        |
| eid          | 明密标识              | UE          |
| data_end     | 数据接收结束时间      | 2020-10-16  |
| data_start   | 数据接收开始时间      | 2020-10-16  |
| file_create  | 文件产生时间          | 2020-10-16  |
| er           | 数据包状态            | 000         |
| suffix       | 文件扩展名            | .raw        |
| sort         | 排序字段              | upload_time |
| order        | 排序规则              | desc/aesc   |
| upload_time  | 上传时间              | 2020-10-16  |

