package ru.videodetector.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@ControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Mono<String>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(Mono.just(e.getMessage()));
    }
}
