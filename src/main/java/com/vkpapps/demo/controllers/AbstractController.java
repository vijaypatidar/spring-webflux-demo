package com.vkpapps.demo.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractController {

    @Autowired
    protected ModelMapper modelMapper;
}
