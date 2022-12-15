package com.vkpapps.demo.exporters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service(value = "TXT_EXPORTER")
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TXTExporter extends CSVExporter {
    @Override
    protected String getExtension() {
        return "txt";
    }
}
