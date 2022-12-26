package com.vkpapps.demo.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.util.StackLocatorUtil;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {
    boolean success;
    T data;
    Object error;
    String warning;

    public static <T> ResponseDto<T> success() {
        return success(null);
    }

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<T>().setSuccess(true).setData(data);
    }

    public static <T> ResponseDto<T> error(Throwable e) {
        LogManager.getLogger(StackLocatorUtil.getCallerClass(2)).info(e);
        return new ResponseDto<T>().setSuccess(false).setError(e);
    }

    public static <T> ResponseDto<T> error(Object e) {
        return new ResponseDto<T>().setSuccess(false).setError(e);
    }

    public static <T> ResponseDto<T> error(String e) {
        return new ResponseDto<T>().setSuccess(false).setError(e);
    }

    /* get success */
    public boolean isSuccess() {
        return success;
    }

    /* set success */
    public ResponseDto<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    /* get data */
    public Object getData() {
        return data;
    }

    /* set data */
    public ResponseDto<T> setData(T data) {
        this.data = data;
        return this;
    }

    /* get error */
    public Object getError() {
        return error;
    }

    /* set error */
    public ResponseDto<T> setError(Object error) {
        this.error = error;
        return this;
    }
}