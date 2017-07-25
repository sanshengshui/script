package com.sanshengshui.bean;

/**
 * @ClassName RpcRequest
 * @author 穆书伟
 * @description 封装RPC响应
 * @date 2017/7/23 15:23:02
 */
public class RpcResponse {
    private String requestId;
    private Exception exception;
    private Object result;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
