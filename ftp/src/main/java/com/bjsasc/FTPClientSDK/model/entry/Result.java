package com.bjsasc.FTPClientSDK.model.entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 操作返回结果
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class Result<T> {

    private boolean success = true;
    private int code = 200;
    private String message = "success";
    private T data;

    public Result setErrorMsgInfo(String msg){
        this.setCode(500);
        this.success = false;
        this.setMessage(msg);
        return this;

    }
}
