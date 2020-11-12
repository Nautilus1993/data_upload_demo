package com.bjsasc.FTPClientSDK.model.enu;

/**
 * 文件列表查询参数
 */
public enum QueryParamEnum {
    /**
     * 排序字段
     */
    SORT("sort","String","排序字段"),
    /**
     * 排序规则
     */
    ORDER("order","String","排序规则"),
    /**
     * 上传时间
     */
    UPLOAD_TIME("upload_time","Date","上传时间"),
    /**
     * 接收站/数据来路标识
     */
    ST("st","String","接收站/数据来路标识"),
    /**
     * 明密标识
     */
    EID("eid","String","明密标识"),
    /**
     * 下行舱段标识/任务标识
     */
    DLSC("dlsc","String","下行舱段标识/任务标识"),
    /**
     * 数据集合标识
     */
    DTG("dtg","String","数据集合标识"),
    /**
     * 数据级别
     */
    LE("le","String","数据级别"),
    /**
     * 数据类型标识
     */
    DTY("dty","String","数据类型标识"),
    /**
     * 数据接收开始时间
     */
    START_TIME("start_time","Date","数据接收开始时间"),
    /**
     * 数据接收结束时间
     */
    END_TIME("end_time","Date","数据接收结束时间"),
    /**
     * 文件产生时间
     */
    GENERATE_TIME("generate_time","Date","文件产生时间"),
    /**
     * 数据包状态
     */
    ER("er","String","数据包状态"),
    /**
     * 文件扩展名
     */
    EXT("ext","String","文件扩展名");

    private String paramName;
    private String paramType;
    private String paramMessage;
    QueryParamEnum(String paramName, String paramType,String paramMessage){
        this.paramName = paramName;
        this.paramType = paramType;
        this.paramMessage = paramMessage;
    }
    public String paramName(){return this.paramName;};
    public String paramType(){return this.paramType;};
    public String paramMessage(){return this.paramMessage;};
}
