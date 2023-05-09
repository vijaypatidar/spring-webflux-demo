package com.vkpapps.demo.controllers;

import com.vkpapps.demo.controllers.helpers.ModelToDtoConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.UUID;

public abstract class AbstractController implements ModelToDtoConverter, ApplicationContextAware {

    protected ApplicationContext applicationContext;
    @Autowired
    protected ModelMapper modelMapper;

    @Override
    public ModelMapper getModelMapper() {
        return this.modelMapper;
    }

    protected String randomUUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
