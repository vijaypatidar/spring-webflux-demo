package com.vkpapps.demo.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public abstract class AbstractController implements ModelToDtoConverter{

    @Autowired
    protected ModelMapper modelMapper;
    @Override
    public ModelMapper getModelMapper() {
        return this.modelMapper;
    }

    protected String randomUUID(){
        return UUID.randomUUID().toString();
    }
}
