package com.bjsasc.FTPClientSDK.model.entry;

import com.bjsasc.FTPClientSDK.model.enu.QueryParamEnum;
import org.apache.http.client.utils.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 文件列表查询对象
 */
public class QueryParam {

    private Map<String,Date> paramsDate;
    private Map<String,String> paramsString;
    public QueryParam(){
        paramsDate = new HashMap();
        paramsString = new HashMap();
    }
/**
 * 设置查询值
 */
    public void setParam(QueryParamEnum param, String paramValue){
        if(param.paramType().equals("Date")){
            //如果是日期类型，转换成日期
            Date dateValue = DateUtils.parseDate(paramValue,new String[]{"yyyy-MM-dd"});
            paramsDate.put(param.paramName(), dateValue);
        }else {
            paramsString.put(param.paramName(), paramValue);
        }
    }

    public Map<String, Date> getParamsDate() {
        return paramsDate;
    }


    public Map<String, String> getParamsString() {
        return paramsString;
    }
}
