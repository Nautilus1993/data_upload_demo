## 1.1点击 “开始上传”按钮触发事件

###### 请求方式

| 方式 | 请求地址            |
| ---- | ------------------- |
| post | / task/upload/start |

> 请求参数结构

| BODY字段     | 类型   | 说明         | 可选性 |
| ------------ | ------ | ------------ | ------ |
| cabinName    | string | 舱段ID       | 必选   |
| dataCenter   | string | 用户所属中心 | 必选   |
| mainType     | string | 数据大类     | 必选   |
| subType      | string | 数据小类     | 必选   |
| uploadUser   | string | 上传用户     | 必选   |
| userGroup    | string | 用户组       | 必选   |
| getTime      | Date   | 采集时间     | 必选   |
| dataDesc     | string | 数据描述信息 | 必选   |
| labelsIds    | string | 标签数据     | 必选   |
| token        | string | 用户令牌。   | 必选   |
| fileInfoDtos | string | 上传文件列表 | 必选   |

> 请求示例 (Jquery)

```javascript
$.post(contextPath + "/task/upload/start", {
                cabinName: "cabin_id",
                dataCenter: "hty",
                mainType: "test2",
                subType:"test2",
                uploadUser: "admin",
                userGroup:"userGroup",
                getTime:"Fri Oct 23 15:32:05 CST 2020",
                dataDesc: "数据描述",
                labelsIds: JSON.stringify([{"id":"8171b09d3ca1465f86025cf4cfae4b4d"},{"id":"2e48a1b6cc9f4e75905bfc22a83444b6"}]]),
                token: "token",
                fileInfoDtos: JSON.stringify([{"name":"hty2.raw","size":403787776,"lastModifyTime":"2016-09-18T06:41:38.000Z"}])
            })
```

> 示例返回

```javascript
{
   code: 200
   data:{
        files: {hty2.raw: 1999911}
		result: {success: true, code: 200, message: "success", data: null}
		taskId: 1000084
		tempDir: "/root/carbinName/centerName2/mainTypeName2/subTypeName2/2020/1015"
    }

message: "success"
success: true
}
```

