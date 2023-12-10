package ru.videodetector.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.videodetector.server.template.TemplateService;
import ru.videodetector.server.template.entity.TemplateEntity;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@RestController
@CrossOrigin
public class TemplateController {
    private final TemplateService templateService;

    public TemplateController(@Autowired TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping(value = "/template/upload")
    public TemplateEntity createTemplate(@RequestParam(name = "file") MultipartFile file) throws IOException {
        return this.templateService.createTask(file.getOriginalFilename(), file.getInputStream());
    }

    @GetMapping(value = "/template/{uuid}")
    public ResponseEntity<TemplateEntity> getTemplate(@PathVariable("uuid") String uuid) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.templateService.getTask(UUID.fromString(uuid)));
    }

    @GetMapping(value = "/template")
    public ResponseEntity<List<TemplateEntity>> getTemplates() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.templateService.getAllTasks());
    }

    @DeleteMapping(value = "/template/{uuid}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable("uuid") String uuid) {
        this.templateService.delete(UUID.fromString(uuid));
        return ResponseEntity.ok()
                .build();
    }
}
