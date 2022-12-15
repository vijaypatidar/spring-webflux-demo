package com.vkpapps.demo.exporters;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ContentDisposition;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class Exporter {
    private ExportDataSource dataSource;

    protected Exporter() {
    }


    protected Mono<Void> preProcessStart() {
        return Mono.empty();
    }

    private Mono<Void> processStart() {
        log.info("Exporter::processStart");
        if (this.inValidDataSource(dataSource))
            return Mono.error(new RuntimeException("ServerWebExchange must be initialized before starting export."));
        dataSource.getServerWebExchange().getResponse().getHeaders().setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename(dataSource.getFileName() + "." + getExtension())
                        .build()
        );
        Flux<Mono<DataBuffer>> flux = processHeaders(getHeader())
                .mergeWith(processRows(getRows()))
                .mergeWith(processComplete())
                .flatMap(db -> Mono.just(Mono.just(db)));
        return this.dataSource.getServerWebExchange().getResponse().writeAndFlushWith(flux);
    }

    private boolean inValidDataSource(ExportDataSource dataSource) {
        return dataSource == null
                || dataSource.getRows() == null
                || dataSource.getFileName() == null
                || dataSource.getServerWebExchange() == null
                || dataSource.getHeaders() == null;
    }

    protected abstract Mono<DataBuffer> processHeaders(Mono<List<String>> headersMono);

    protected abstract Flux<DataBuffer> processRows(Flux<List<String>> rowsFlux);

    protected abstract String getExtension();

    protected Mono<DataBuffer> processComplete() {
        return Mono.empty();
    }


    protected Mono<DataBuffer> buildDataBuffer(byte[] data) {
        return Mono.just(this.getDataSource().getServerWebExchange().getResponse().bufferFactory().wrap(data));
    }

    public Mono<Void> export() {
        return preProcessStart().then(processStart());
    }


    protected Mono<List<String>> getHeader() {
        return this.dataSource.getHeaders();
    }

    protected Flux<List<String>> getRows() {
        return this.dataSource.getRows();
    }

    protected ExportDataSource getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(ExportDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Getter
    @Setter
    @Builder
    public static class ExportDataSource {
        private ServerWebExchange serverWebExchange;
        private String fileName;
        private Mono<List<String>> headers;
        private Flux<List<String>> rows;

    }
}
