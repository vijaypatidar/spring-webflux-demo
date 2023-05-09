package com.vkpapps.demo.exceptions;

public class ServiceNotHealthyException extends Exception {
    public ServiceNotHealthyException() {
        super("Service is not healthy");
    }
}
