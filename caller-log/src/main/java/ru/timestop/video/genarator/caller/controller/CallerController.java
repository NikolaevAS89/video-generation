package ru.timestop.video.genarator.caller.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.timestop.video.genarator.caller.callback.CallerService;
import ru.timestop.video.genarator.caller.callback.model.CallbackMessage;
import ru.timestop.video.genarator.caller.callback.entity.CallbackEntity;

import java.util.List;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@RestController
@CrossOrigin
public class CallerController {
    private final CallerService callerService;


    public CallerController(@Autowired CallerService callerService) {
        this.callerService = callerService;
    }

    @PostMapping(value = "/callback")
    public ResponseEntity<Void> saveCallback(@Valid @RequestBody CallbackMessage callbackMessage) {
        this.callerService.saveCallbackMessage(callbackMessage);
        return ResponseEntity.ok()
                .build();
    }

    @GetMapping(value = "/callback")
    public ResponseEntity<List<CallbackEntity>> getCallbacks() {
        return ResponseEntity.ok()
                .body(this.callerService.getCallbackMessages());
    }
}
