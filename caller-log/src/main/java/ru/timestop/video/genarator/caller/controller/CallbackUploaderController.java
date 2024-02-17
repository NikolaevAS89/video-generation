package ru.timestop.video.genarator.caller.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.timestop.video.genarator.caller.callback.CallbackUploaderService;
import ru.timestop.video.genarator.caller.callback.model.response.CallbackRequest;

import java.util.List;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@RestController
@CrossOrigin
public class CallbackUploaderController {
    private final CallbackUploaderService callbackUploaderService;

    public CallbackUploaderController(@Autowired CallbackUploaderService callbackUploaderService) {
        this.callbackUploaderService = callbackUploaderService;
    }

    @GetMapping(value = "/callback/data")
    public ResponseEntity<List<CallbackRequest>> getAllCallbackRequests() {
        return ResponseEntity.ok()
                .body(this.callbackUploaderService.getCallbackRequests());
    }
}
