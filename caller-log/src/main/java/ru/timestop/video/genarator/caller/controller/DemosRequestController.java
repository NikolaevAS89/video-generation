package ru.timestop.video.genarator.caller.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.timestop.video.genarator.caller.callback.DemoRequestService;
import ru.timestop.video.genarator.caller.callback.model.request.DemosRequest;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@RestController
@CrossOrigin
public class DemosRequestController {
    private final DemoRequestService demoRequestService;


    public DemosRequestController(@Autowired DemoRequestService demoRequestService) {
        this.demoRequestService = demoRequestService;
    }


    @PostMapping(value = "/callback/demo")
    public ResponseEntity<Void> saveDemoRequest(@Valid @RequestBody DemosRequest callbackMessage) {
        this.demoRequestService.saveDemosRequest(callbackMessage);
        return ResponseEntity.ok()
                .build();
    }
}
