package com.vkpapps.demo.exporters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ContentDisposition;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public abstract class Exporter {
    private ServerWebExchange serverWebExchange = null;
    private String fileName;
    private Mono<List<String>> headers;
    private Flux<List<String>> rows;

    protected Exporter() {
    }


    protected Mono<Void> processStart() {
        log.info("Exporter::processStart");
        if (this.serverWebExchange == null)
            return Mono.error(new RuntimeException("ServerWebExchange must be initialized before starting export."));
        serverWebExchange.getResponse().getHeaders().setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename(fileName + "." + getExtension())
                        .build()
        );
        Flux<Mono<DataBuffer>> flux = processHeaders(getHeader()).mergeWith(processRows(getRows())).flatMap(db -> Mono.just(Mono.just(db)));
        return this.serverWebExchange.getResponse().writeAndFlushWith(flux);
    }

    protected abstract Mono<DataBuffer> processHeaders(Mono<List<String>> headersMono);

    protected abstract Flux<DataBuffer> processRows(Flux<List<String>> rowsFlux);

    protected abstract String getExtension();

    protected Mono<Void> processComplete() {
        log.info("Exporter::processComplete");
        return Mono.create(voidMonoSink -> {
            log.info("Exporter::processComplete::Mono");
            voidMonoSink.success();
        });
    }



    protected Mono<DataBuffer> buildDataBuffer(byte[] data) {
        return Mono.just(this.serverWebExchange.getResponse().bufferFactory().wrap(data));
    }

    public Mono<Void> export() {
        return processStart()
                .and(processComplete());
    }

    public void setServerWebExchange(ServerWebExchange serverWebExchange) {
        this.serverWebExchange = serverWebExchange;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    protected Mono<List<String>> getHeader(){
        return this.headers;
    }

    protected Flux<List<String>> getRows(){
        return this.rows;
    }

    public void setHeaders(Mono<List<String>> headers) {
        this.headers = headers;
    }

    public void setRows(Flux<List<String>> rows) {
        this.rows = rows;
    }
}
