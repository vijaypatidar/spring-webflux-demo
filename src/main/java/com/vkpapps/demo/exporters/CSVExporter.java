package com.vkpapps.demo.exporters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service(value = "CSV_EXPORTER")
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CSVExporter extends Exporter {
    @Override
    protected Mono<DataBuffer> processHeaders(Mono<List<String>> headersMono) {
        return headersMono.flatMap(headers -> {
            StringBuilder stringBuilder = new StringBuilder();

            for (String header : headers) {
                stringBuilder.append(header).append(",");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            stringBuilder.append("\n");
            return buildDataBuffer(stringBuilder.toString().getBytes());
        });
    }

    @Override
    protected Flux<DataBuffer> processRows(Flux<List<String>> rowsFlux) {
        return rowsFlux.buffer(20).flatMap(rows -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (List<String> row : rows) {
                for (String cell : row) {
                    stringBuilder.append(cell).append(",");
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.setLength(stringBuilder.length() - 1);
                }
                stringBuilder.append("\n");
            }
            if (stringBuilder.length() > 0) stringBuilder.setLength(stringBuilder.length() - 1);
            return buildDataBuffer(stringBuilder.toString().getBytes());
        });
    }

    @Override
    protected String getExtension() {
        return "csv";
    }
}
