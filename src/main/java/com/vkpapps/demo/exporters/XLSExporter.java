package com.vkpapps.demo.exporters;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service(value = "XLS_EXPORTER")
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class XLSExporter extends Exporter {
    private final AtomicInteger count = new AtomicInteger(0);
    private ByteArrayOutputStream outputStream = null;
    private Sheet notedSheet = null;
    private Workbook workbook = null;

    @Override
    protected Mono<Void> preProcessStart() {
        return Mono.create(sink -> {
            try {
                workbook = new HSSFWorkbook();
                notedSheet = workbook.createSheet(getDataSource().getFileName());
                outputStream = new ByteArrayOutputStream();
            } catch (Exception e) {
                sink.error(e);
            }
            sink.success();
        });
    }

    @Override
    protected Mono<DataBuffer> processHeaders(Mono<List<String>> headersMono) {
        return headersMono.flatMap(headers -> {
            var headerRow = notedSheet.createRow(count.get());
            for (var i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }
            try {
                return buildDataBuffer(new byte[]{});
            } catch (Exception e) {
                return Mono.error(e);
            }
        });
    }

    @Override
    protected Flux<DataBuffer> processRows(Flux<List<String>> rowsFlux) {
        return rowsFlux.buffer(20).flatMap(rows -> {
            rows.forEach(r -> {
                var row = notedSheet.createRow(count.addAndGet(1));
                for (var i = 0; i < r.size(); i++) {
                    row.createCell(i).setCellValue(r.get(i));
                }
            });
            return buildDataBuffer(new byte[]{});
        });
    }

    @Override
    protected Mono<DataBuffer> processComplete() {
        return Mono.create(sink -> {
            try {
                workbook.write(outputStream);
                sink.success(getDataSource()
                        .getServerWebExchange()
                        .getResponse()
                        .bufferFactory()
                        .wrap(outputStream.toByteArray())
                );
                workbook.close();
                outputStream.close();
            } catch (IOException e) {
                sink.error(e);
            }
        });
    }

    @Override
    protected String getExtension() {
        return "xls";
    }
}
