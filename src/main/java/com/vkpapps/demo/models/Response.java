package com.vkpapps.demo.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.util.StackLocatorUtil;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    boolean success;
    T       data;
    Object  error;
    String  warning;

    public Response() {
    }


    public static Response success() {
        return success(null);
    }

    public static <T> Response success(T data) {
        return new Response<T>().setSuccess(true).setData(data);
    }

    public static Response error(Throwable e) {
        LogManager.getLogger(StackLocatorUtil.getCallerClass(2)).info(e);
        return new Response().setSuccess(false).setError(e);
    }

    public static Response error(Object e) {
        return new Response().setSuccess(false).setError(e);
    }

    public static Response error(String e) {
        return new Response().setSuccess(false).setError(e);
    }

    /* get success */
    public boolean isSuccess() {
        return success;
    }

    /* set success */
    public Response setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    /* get data */
    public Object getData() {
        return data;
    }

    /* set data */
    public Response setData(T data) {
        this.data = data;
        return this;
    }

    /* get error */
    public Object getError() {
        return error;
    }

    /* set error */
    public Response setError(Object error) {
        this.error = error;
        return this;
    }
}