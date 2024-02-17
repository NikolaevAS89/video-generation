package ru.timestop.video.generator.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.timestop.video.generator.server.demo.DemoService;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.io.IOException;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public class DemoController {
    private final DemoService demoService;

    public DemoController(@Autowired DemoService demoService) {
        this.demoService = demoService;
    }

    @PostMapping(value = "/demo/upload")
    public TemplateEntity createDemo(@RequestParam(name = "file") MultipartFile file) throws IOException {
        return this.demoService.createDemo(file.getOriginalFilename(), file.getInputStream());
    }

    @GetMapping(value = "/demo")
    public ResponseEntity<TemplateEntity> getDemo() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.demoService.getDemo());
    }
}
