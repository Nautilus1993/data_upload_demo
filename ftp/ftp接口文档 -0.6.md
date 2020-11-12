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

#### 

#### 2.searchFiles()

```java
public List<FTPFile> searchFiles(String mainType,String subType,QueryParam query_params，int page_size,int page_number)
```

##### 方法功能：分页搜索文件

| 参数         | 解释         |
| ------------ | ------------ |
| mainType     | 数据类型大类 |
| subType      | 数据类型小类 |
| query_params | 查询参数     |
| page_size    | 每页数量     |
| page_number  | 当前页       |

##### 返回值：文件信息list集合

#### 3.startDownload()

```java
public FileOperationResult startDownload(String localPath,List<FTPFile> filesToDownload)
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

#### 4. startUpload()

```java
public FileOperationResult startUpload(String mianType,
                                String subType,
                                List<String> tagList,
                                List<FTPFile> filesToUpload)
```

##### 方法功能：用户开始上传

| 参数          | 解释               |
| ------------- | ------------------ |
| mianType      | 数据类型大类       |
| subType       | 数据类型           |
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

##### 返回值：void

7. #### deleteFile()

```java
public FileOperationResult deleteFile(List<FTPFile> filesToDelete)
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

8. #### monitor()

   ```java
   public List<MFileInfo> monitor(String mainTypeId,String subTypeId,String startTime,String endTime, List<FTPFile> filesToUpload)
   ```

   ##### 方法功能：文件监控

| 参数          | 解释     |
| ------------- | -------- |
| mainTypeId    | 大类     |
| subTypeId     | 小类     |
| startTime     | 开始时间 |
| endTime       | 结束时间 |
| filesToUpload | 文件集合 |

   ##### 返回值：MFileInfo 监控文件信息（见MFileInfo类）

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

设置查询值

```java
public void setParam(QueryParamEnum param, String paramValue)
```

### 类：FileResult

#### 属性：

| 类型                | 属性名       | 解释         |
| ------------------- | ------------ | ------------ |
| String              | name         | 文件名       |
| int                 | originalSize | 文件原始大小 |
| int                 | size         | 文件大小     |
| FileOperationStatus | status       | 操作状态     |

### 枚举类：QueryParamEnum

| 枚举常量说明  | 解释                  |
| ------------- | --------------------- |
| DLSC          | 下行舱段标识/任务标识 |
| DTG           | 数据集合标识          |
| DTY           | 数据类型标识          |
| EID           | 明密标识              |
| END_TIME      | 数据接收结束时间      |
| START_TIME    | 数据接收开始时间      |
| GENERATE_TIME | 文件产生时间          |
| ER            | 数据包状态            |
| EXT           | 文件扩展名            |
| LE            | 数据级别              |
| ST            | 接收站/数据来路标识   |
| ORDER         | 排序方式              |
| SORT          | 排序字段              |
| UPLOAD_TIME   | 上传时间              |




