package ru.timestop.video.generator.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.timestop.video.generator.server.facade.TemplateFacade;
import ru.timestop.video.generator.server.template.TemplateService;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@RestController
@CrossOrigin
public class TemplateController {
    private final TemplateFacade templateFacade;

    public TemplateController(@Autowired TemplateFacade templateFacade) {
        this.templateFacade = templateFacade;
    }

    @PostMapping(value = "/template/upload")
    public TemplateEntity createTemplate(@RequestParam(name = "file") MultipartFile file) throws IOException {
        return this.templateFacade.createTemplate(file.getOriginalFilename(), file.getInputStream());
    }

    @GetMapping(value = "/template/{uuid}")
    public ResponseEntity<TemplateEntity> getTemplate(@PathVariable("uuid") String uuid) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.templateFacade.getTemplate(UUID.fromString(uuid)));
    }

    @GetMapping(value = "/template")
    public ResponseEntity<List<TemplateEntity>> getTemplates() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.templateFacade.getTemplates());
    }

    @DeleteMapping(value = "/template/{uuid}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable("uuid") String uuid) {
        this.templateFacade.delete(UUID.fromString(uuid));
        return ResponseEntity.ok()
                .build();
    }
}
