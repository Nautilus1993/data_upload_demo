## 简介

​       此文档为云平台文件数据归档API，传输协议采用FTP方式。**API使用说明**包含项目中所有类、方法接口和参数说明。

## API 使用说明

### FTPTransferClient类

#### 类说明：上传引擎类，包含FTP建立连接、上传、下载、查询、进度监控、退出功能

#### 方法说明：

#### 1.FTPTransferClient()方法


```java
public FTPTransferClient(String userName,String password)
```

##### 方法说明：初始化FTPTransferClient用例：用户认证登录并获取相应FTP用户

| 参数     | 解释   | 类型   | 说明               |
| -------- | ------ | ------ | ------------------ |
| userName | 用户名 | String | 与portal用户名一致 |
| password | 密码   | String | 与portal密码一致   |

##### 返回值：FTPTransferClient 

##### 使用示例

```java
String USERNAME = "test_user_1";//用户名
String PASSWORD = "QWER1234";//密码
// 初始化FTPTransferClient 实例
FTPTransferClient myFtp = new FTPTransferClient(USERNAME, PASSWORD);
```



#### 2.searchFiles()方法

```java
public List<FTPFile> searchFiles(String mainType,
                                 String subType,
                                 QueryParam query_params，
                                 int page_size,
                                 int page_number)throws Exception
```

##### 方法说明：使用数据类型和查询条件进行分页搜索文件

| 参数         | 解释                                             | 类型       | 说明                       |
| ------------ | ------------------------------------------------ | ---------- | -------------------------- |
| mainType     | 数据类型大类                                     | String     | 参见《中心-数据类型》文档  |
| subType      | 数据类型小类                                     | String     | 参见《中心-数据类型》文档  |
| query_params | 查询参数 ，根据归档元信息查询，类型在xml文件配置 | QueryParam | 见下文QueryParam和Param类  |
| page_size    | 每页数量                                         | int        | 分页查询中每一页的查询数量 |
| page_number  | 当前页，从1开始（为0查询所有）                   | int        | 分页查询返回页码           |

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

| 异常                      | 解释           |
| ------------------------- | -------------- |
| ParameterIllegalException | 查询参数不合法 |



#### 3.startDownload()方法

##### 3.1 查询后下载

```java
public FileOperationResult startDownload(String localPath,List<FTPFile> filesToDownload)throws Exception
```

##### 方法说明：文件下载，下载由search方法返回搜索结果的文件

| 参数            | 解释             | 类型          | 说明                                                         |
| --------------- | ---------------- | ------------- | ------------------------------------------------------------ |
| filesToDownload | 要下载的文件列表 | List<FTPFile> | 该list由search方法返回的搜索文件列表，非用户手动构建，FTPFile类型见下文 |
| localPath       | 下载到的本地路径 | String        | 路径结尾不需要分隔符                                         |

##### 返回值：FileOperationResult文件操作结果

| 属性            | 解释               | 类型                   | 说明                                                   |
| --------------- | ------------------ | ---------------------- | ------------------------------------------------------ |
| status          | 文件的操作状态     | FileOperationStatus    | 见下文FileOperationStatus枚举状态                      |
| failFiles       | 下载失败的文件列表 | List<String>           | 如果下载成功，failFiles为空                            |
| acceptDateEnd   | 结束下载时间       | Date                   | 本次下载操作结束时间                                   |
| acceptDateStart | 开始下载时间       | Date                   | 本次下载操作开始时间                                   |
| dataCount       | 数据量-文件个数    | Integer                | 本次下载操作文件数量                                   |
| dataSource      | 数据来源-文件中心  | String                 | 下载文件所属中心                                       |
| mainType        | 数据大类           | String                 | 下载文件所属大类，参见《中心-数据类型》文档            |
| subType         | 数据小类           | String                 | 下载文件所属小类，参见《中心-数据类型》文档            |
| fileResults     | 下载操作结果       | Map<String,FileResult> | 文件名-该文件下载结果 键值对，FileResult类型介绍见下文 |

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

##### 方法说明：根据文件名称直接下载文件

| 参数      | 解释                 | 类型         | 说明                                        |
| --------- | -------------------- | ------------ | ------------------------------------------- |
| mainType  | 数据类型大类         | String       | 下载文件所属大类，参见《中心-数据类型》文档 |
| subType   | 数据类型小类         | String       | 下载文件所属小类，参见《中心-数据类型》文档 |
| localDir  | 下载到的本地路径     | String       | 路径结尾不需要分隔符                        |
| fileNames | 要下载的文件名称列表 | List<String> |                                             |

##### 返回值：FileOperationResult 文件操作结果

##### 使用示例

```java
String downloadPath = "D:\\hanbing"; //下载到本地路径;
List<String> fileNames = new ArrayList<>();
fileNames.add("CT_TL1A2_SZ12_20201028052556_20201028052556_20201028052556_M_00001.raw");//文件名称列表 
String mainType = "main_001";  // 数据大类：归档数据
String subType = "sub_001";    // 数据小类：原始数据文件
FileOperationResult result = myFtp.startDownload(mainType, subType, downloadPath, fileNames);
```



#### 4. getTags()方法

```java
public List getTags()
```

##### 方法说明：获取所有标签

##### 返回值：所有标签结果List<Tag> 

##### Tag类属性：

| 属性名  | 解释   | 类型   | 说明                               |
| ------- | ------ | ------ | ---------------------------------- |
| tagName | 标签名 | String | 标签介绍，用户可以在portal界面创建 |
| tagID   | 标签ID | String | 上传时需要传入tagID列表            |

##### 使用示例

```java
//标签
List<Tag> tagList = myFtp.getTags();
```



#### 5. startUpload()方法

##### 5.1 文件自动归档上传

```java
public FileOperationResult startUpload(String cabin，
                                String mianType,
                                String subType,
                                List<String> tagList,
                                List<FTPFile> filesToUpload)throws Exception
```

##### 方法说明：文件上传，归档时从文件名提取元信息

| 参数          | 解释               | 类型          | 说明                                    |
| ------------- | ------------------ | ------------- | --------------------------------------- |
| cabin         | 舱段代号           | String        | 舱段ID                                  |
| mianType      | 数据类型大类代号   | String        | 文件所属大类，参见《中心-数据类型》文档 |
| subType       | 数据类型小类代号   | String        | 文件所属小类，参见《中心-数据类型》文档 |
| tagList       | 标签列表           | List<String>  | 允许为空                                |
| filesToUpload | 批量上传的文件集合 | List<FTPFile> | 上传只需提供FTPFile类中name和path属性   |

##### 返回值：FileOperationResult文件操作结果

| 参数            | 解释                   | 类型                   | 说明                                                   |
| --------------- | ---------------------- | ---------------------- | ------------------------------------------------------ |
| status          | 文件的上传状态         | FileOperationStatus    | 见下文FileOperationStatus枚举状态                      |
| failFiles       | 文件上传失败的文件列表 | List<String>           | 如果上传成功，failFiles为空                            |
| acceptDateEnd   | 结束上传时间           | Date                   | 本次上传操作结束时间                                   |
| acceptDateStart | 开始上传时间           | Date                   | 本次上传操作开始时间                                   |
| dataCount       | 数据量-文件个数        | Integer                | 本次上传操作文件数量                                   |
| dataSource      | 数据来源-文件中心      | String                 | 上传文件所属中心，参见《中心-数据类型》文档            |
| mainType        | 数据大类               | String                 | 上传文件所属大类，参见《中心-数据类型》文档            |
| subType         | 数据小类               | String                 | 上传文件所属小类，参见《中心-数据类型》文档            |
| fileResults     | 数据上传结果           | Map<String,FileResult> | 文件名-该文件下载结果 键值对，FileResult类型介绍见下文 |

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

##### 5.2 手动归档上传

```java
public FileOperationResult startUploadWithInfo(String cabin, 
                               String mainTypeId, 
                               String subTypeId, 
                               List<String> tagList, 
                               Map<FTPFile, BaseFile>uploadFileMap)  throws Exception
```

##### 方法功能：文件上传，需要用户手动录入归档元信息

| 参数          | 解释                             | 类型                   | 说明                                                         |
| ------------- | -------------------------------- | ---------------------- | ------------------------------------------------------------ |
| cabin         | 舱段代号                         | String                 | 舱段ID                                                       |
| mianType      | 数据类型大类代号                 | String                 | 上传文件所属大类，参见《中心-数据类型》文档                  |
| subType       | 数据类型小类代号                 | String                 | 上传文件所属小类，参见《中心-数据类型》文档                  |
| tagList       | 标签列表                         | List<String>           | 允许为空                                                     |
| uploadFileMap | 批量上传的文件和对应归档信息集合 | Map<FTPFile, BaseFile> | 上传文件-归档信息 键值对，BaseFile类型为元信息基类，具体实现类见下文BioMedicalFile、DataResultAchieveReportFile、MedicalTestFile、MicroHazardousGasFile、SpaceSuitSensorFile |

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



#### 6. deleteFile()方法

```java
public FileOperationResult deleteFile(List<FTPFile> filesToDelete)throws Exception
```

##### 方法说明：删除文件

| 参数          | 解释             | 类型          | 说明                                                       |
| ------------- | ---------------- | ------------- | ---------------------------------------------------------- |
| filesToDelete | 要删除的文件列表 | List<FTPFile> | filesToDelete是搜索方法的结果，该FTPFile类型非用户手动构建 |

##### 返回值：FileOperationResult文件操作结果



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



#### 7.monitor()方法

```java
public List<MFileInfo> monitor(String mainTypeId,
                               String subTypeId,
                               String startTime,
                               String endTime, 
                               List<FTPFile> filesToMonitor)
```

##### 方法功能：监控上传或下载中文件的状态信息，包括传输进度，大小，状态

| 参数           | 解释     | 类型          | 说明                                                        |
| -------------- | -------- | ------------- | ----------------------------------------------------------- |
| mainTypeId     | 大类     | String        | 被监控文件所属大类，参见《中心-数据类型》文档               |
| subTypeId      | 小类     | String        | 被监控文件所属小类，参见《中心-数据类型》文档               |
| startTime      | 开始时间 | String        | 时间格式为"yyyy-MM-dd HH:mm:ss" ，例如"2020-10-14 15:36:02" |
| endTime        | 结束时间 | String        | 时间格式为"yyyy-MM-dd HH:mm:ss" ，例如"2020-10-14 15:36:02" |
| filesToMonitor | 文件集合 | List<FTPFile> | 该集合是在调用上传/下载方法中的参数                         |

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



#### 8. listFile()方法

```java
public List<FTPFileInfo> listFile(String mainType, String subType)
```

##### 方法说明：根据数据类型查看所有的文件详细信息，包括文件名，上传时间，文件大小

##### 返回值：所有文件列表List<FTPFileInfo>

##### Tag类属性：

| 属性名   | 解释     | 类型   | 说明                                    |
| -------- | -------- | ------ | --------------------------------------- |
| mainType | 数据大类 | String | 文件所属大类，参见《中心-数据类型》文档 |
| subType  | 数据小类 | String | 文件所属小类，参见《中心-数据类型》文档 |

##### 使用示例

```java
String mainType = "main_001";     // 数据大类：归档数据
String subType = "sub_001";          // 数据小类：原始数据文件
//列表所有文件
List<FTPFileInfo> listFiles = myFtp.listFile(mainType,subType);
```



#### 9. logout()方法

```java
public void logout()
```

##### 方法说明：当前用户退出登录

使用示例

``` java
//退出登录，断开连接
myFtp.logout()
```



### MFileInfo类

#### 类说明：文件监控实体类，监控方法返回该对象列表

#### 属性说明：

| 属性名          | 解释               | 类型   | 说明 |
| --------------- | ------------------ | ------ | ---- |
| currentSize     | 文件目前大小       | Long   |      |
| fileName        | 文件名称           | String |      |
| fileSize        | 文件大小           | Long   |      |
| percentage      | 文件目前大小百分比 | String |      |
| operationTime   | 文件操作时间       | Date   |      |
| operationStatus | 文件操作状态       | String |      |



### FTPFile类

#### 类说明：文件实体类，上传时只需要提供name和path

#### 属性说明：

| 属性名 | 解释         | 类型   | 说明                                                  |
| ------ | ------------ | ------ | ----------------------------------------------------- |
| id     | ID           | String | 文件在数据库中唯一主键                                |
| name   | 文件名       | String | 文件全称                                              |
| path   | 文件路径     | String | 上传文件路径（分隔符建议用"/"，结尾路径不需要分隔符） |
| year   | 文件上传年份 | String | 文件上传时所在年                                      |



### QueryParam类

#### 类说明：查询实体类

#### 方法说明：

1. 获取所有参数项

``` java
public Map<String,Param> getParams()
```

2. 获取某一个参数项

```java
public Param getParam(String name)
```

| 属性名 | 解释     | 类型   | 说明                                                         |
| ------ | -------- | ------ | ------------------------------------------------------------ |
| name   | 参数名字 | String | 归档元信息名称，具体名称可通过getParams()方法查看，示例参照下文Param类查询参数名称 |

3. 设置查询值

```java
public void setParam(Param param, String paramValue)
```

| 属性名     | 解释     | 类型   | 说明                              |
| ---------- | -------- | ------ | --------------------------------- |
| param      | 查询参数 | Param  | 可通过getParam()方法获取param对象 |
| paramValue | 参数值   | String | 对应查询参数的查询值              |

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



### Param类

#### 类说明： 查询参数类

#### 属性说明：

| 查询参数名称 | 解释                  | 类型   | 查询值示例 |
| ------------ | --------------------- | ------ | ---------- |
| dlsc         | 下行舱段标识/任务标识 | String | TGTH       |
| dtg          | 数据集合标识          | String | GCYC       |
| dty          | 数据类型标识          | String | GCYC       |
| eid          | 明密标识              | String | UE         |
| data_end     | 数据接收结束时间      | String | 2020-10-16 |
| data_start   | 数据接收开始时间      | String | 2020-10-16 |
| file_create  | 文件产生时间          | String | 2020-10-16 |
| er           | 数据包状态            | String | 000        |
| suffix       | 文件扩展名            | String | .raw       |

pmc-main_001-sub_001

| 查询参数名称 | 解释                  | 类型   | 查询值示例 |
| ------------ | --------------------- | ------ | ---------- |
| st           | 接收站/数据来路标识   | String | CT         |
| tl           | 中继星标识            | String | TL1A2      |
| mid          | 下行舱段标识/任务标识 | String | SZ12       |
| data_start   | 文件开始时间          | Date   | 2020-10-16 |
| data_end     | 文件结束时间          | Date   | 2020-10-16 |
| file_create  | 文件产生时间          | Date   | 2020-10-16 |
| mb           | 前端接收主备机标识    | String | M          |
| no           | 序号标识              | String | 00001      |
| suffix       | 文件扩展名            | String | raw        |

pmc-main_002-sub_002:

| 查询参数名称 | 解释                  | 类型   | 查询值示例 |
| ------------ | --------------------- | ------ | ---------- |
| dlsc         | 下行舱段标识/任务标识 | String | TGTH       |
| dtg          | 数据集合标识          | String | GCYC       |
| dty          | 数据类型标识          | String | GCYC       |
| eid          | 明密标识              | String | UE         |
| data_end     | 数据接收结束时间      | String | 2020-10-16 |
| data_start   | 数据接收开始时间      | String | 2020-10-16 |
| file_create  | 文件产生时间          | String | 2020-10-16 |
| er           | 数据包状态            | String | 000        |
| suffix       | 文件扩展名            | String | .raw       |
| st           | 接收站/数据来路标识   | String | CT         |
| le           | 数据级别              | String | 00         |
| er           | 数据包状态            | String | 000        |

pmc-main_003-sub_003:

| 查询参数名称 | 解释                  | 类型   | 查询值示例 |
| ------------ | --------------------- | ------ | ---------- |
| creator      | 生产者标志            | String | SADC       |
| dlsc         | 下行舱段标识/任务标识 | String | TGTH       |
| fstr         | 文件开始时间          | Date   | 2020-10-16 |
| fend         | 文件结束时间          | Date   | 2020-10-16 |
| fcreate      | 文件产生时间          | Date   | 2020-10-16 |
| fbak         | 文件归档时间          | Date   | 2020-10-16 |
| suffix       | 文件扩展名            | String | dat        |

pmc-main_003-sub_004:

| 查询参数名称 | 解释                  | 类型   | 查询值示例 |
| ------------ | --------------------- | ------ | ---------- |
| creator      | 生产者标志            | String | SADC       |
| dlsc         | 下行舱段标识/任务标识 | String | TGTH       |
| fstr         | 文件开始时间          | Date   | 2020-10-16 |
| fend         | 文件结束时间          | Date   | 2020-10-16 |
| fcreate      | 文件产生时间          | Date   | 2020-10-16 |
| fbak         | 文件归档时间          | Date   | 2020-10-16 |
| suffix       | 文件扩展名            | String | dat        |

pmc-main_003-sub_005:

| 查询参数名称 | 解释                  | 类型   | 查询值示例 |
| ------------ | --------------------- | ------ | ---------- |
| creator      | 生产者标志            | String | SE         |
| dlsc         | 下行舱段标识/任务标识 | String | TGTH       |
| symbol       | 文件标志              | String | CQYBPG     |
| fstr         | 文件开始时间          | Date   | 2020-10-16 |
| fend         | 文件结束时间          | Date   | 2020-10-16 |
| fcreate      | 文件产生时间          | Date   | 2020-10-16 |
| fbak         | 文件归档时间          | Date   | 2020-10-16 |
| suffix       | 文件扩展名            | String | CQYBPG     |

pmc-main_003-sub_006:

| 查询参数名称 | 解释                  | 类型   | 查询值示例 |
| ------------ | --------------------- | ------ | ---------- |
| creator      | 生产者标志            | String | SE         |
| dlsc         | 下行舱段标识/任务标识 | String | TGTH       |
| symbol       | 文件标志              | String | CQYBPG     |
| explain      | 数据说明              | String | ABCD       |
| fstr         | 文件开始时间          | Date   | 2020-10-16 |
| fend         | 文件结束时间          | Date   | 2020-10-16 |
| fcreate      | 文件产生时间          | Date   | 2020-10-16 |
| fbak         | 文件归档时间          | Date   | 2020-10-16 |
| suffix       | 文件扩展名            | String | CQYBPG     |

pmc-main_004-sub_007:

| 查询参数名称 | 解释             | 类型   | 查询值示例 |
| ------------ | ---------------- | ------ | ---------- |
| pr           | 文件生产者       | String | PA         |
| sc           | 任务标识         | String | TGTH       |
| dty          | 文件标识         | String | XX         |
| fd           | 文件其他属性说明 | String | XXXX       |
| data_end     | 文件结束时间     | String | 2020-10-16 |
| data_start   | 文件开始时间     | String | 2020-10-16 |
| gen_time     | 文件产生时间     | String | 2020-10-16 |
| ext          | 文件扩展名       | String | XML        |

### FileResult类

#### 类说明： 文件操作结果类

#### 属性说明：

| 属性名       | 解释         | 类型                | 说明                               |
| ------------ | ------------ | ------------------- | ---------------------------------- |
| name         | 文件名       | String              |                                    |
| originalSize | 文件原始大小 | int                 |                                    |
| size         | 文件大小     | int                 |                                    |
| status       | 操作状态     | FileOperationStatus | 见上传和下载方法的具体操作状态枚举 |



### BasicFile

#### 类说明：手动上传时，附带归档元信息基类

| 属性名 | 解释   | 类型   | 说明         |
| ------ | ------ | ------ | ------------ |
| id     | 文件ID | String | 文件唯一标识 |
| fileId | 文件ID | String | 文件唯一标识 |

#### BasicFile实现类

1. ##### BioMedicalFile：生理数据文件

| 属性名       | 解释         | 类型   | 说明 |
| ------------ | ------------ | ------ | ---- |
| deviceId     | 设备ID       | Short  |      |
| humanId      | 人员信息     | Byte   |      |
| fileTime     | 文件创建时间 | Date   |      |
| fileFullName | 文件名       | String |      |
| cabin        | 所属飞行器   | String |      |
| flyStage     | 所属飞行阶段 | String |      |
| fileType     | 文件类型     | String |      |

2. ##### DataResultAchieveReportFile ：数据成果和成果报告文件元信息表

| 属性名           | 解释           | 类型   | 说明 |
| ---------------- | -------------- | ------ | ---- |
| mission          | 任务代号       | String |      |
| achieveKind      | 数据成果类型   | String |      |
| achieveName      | 数据成果名称   | String |      |
| achieveFrom      | 数据成果来源   | String |      |
| achieveCopyright | 数据成果版权方 | String |      |
| founder          | 创建人         | String |      |
| founderTime      | 创建时间       | Date   |      |
| descOfAchievs    | 成果描述       | String |      |
| format           | 数据成果格式   | String |      |

3. ##### MedicalTestFile：航天医学实验数据文件表

| 属性名       | 解释         | 类型         | 说明 |
| ------------ | ------------ | ------------ | ---- |
| itemId       | 项目ID       | Short        |      |
| deviceId     | 设备ID       | Short        |      |
| fileTime     | 文件创建时间 | Date         |      |
| fileFullName | 文件名       | String       |      |
| cabin        | String       | 所属飞行器   |      |
| flyStage     | String       | 所属飞行阶段 |      |
| fileType     | String       | 文件类型     |      |
| indentity    | String       | 文件标识     |      |
| comment      | String       | 备注         |      |

4. ##### MicroHazardousGasFile：微量气体检测装备数据文件

| 属性名       | 解释         | 类型   | 说明 |
| ------------ | ------------ | ------ | ---- |
| fileTime     | 文件创建时间 | Date   |      |
| fileFullName | 文件名       | String |      |
| cabin        | 所属飞行器   | String |      |
| flyStage     | 所属飞行阶段 | String |      |
| fileType     | 文件类型     | String |      |
| indentity    | 文件标识     | String |      |
| comment      | 备注         | String |      |

5. ##### SpaceSuitSensorFile：舱外服上注传感器系数数据文件

| 属性名       | 解释         | 类型   | 说明 |
| ------------ | ------------ | ------ | ---- |
| fileTime     | 文件创建时间 | Date   |      |
| fileFullName | 文件名       | String |      |



### FTPFileInfo

类描述：FTP 云存储文件 文件详情信息

| 属性名     | 解释     | 类型   | 说明 |
| ---------- | -------- | ------ | ---- |
| fileName   | 文件名称 | String |      |
| uploadTime | 上传时间 | Date   |      |
| fileSize   | 文件大小 | Long   |      |

