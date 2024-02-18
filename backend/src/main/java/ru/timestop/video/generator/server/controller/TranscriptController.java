package ru.timestop.video.generator.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.timestop.video.generator.server.facade.TranscriptFacade;
import ru.timestop.video.generator.server.transcript.model.AudioTemplate;
import ru.timestop.video.generator.server.transcript.model.WordMetadata;

import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@RestController
@CrossOrigin
public class TranscriptController {
    private final TranscriptFacade transcriptFacade;


    public TranscriptController(@Autowired TranscriptFacade transcriptFacade) {
        this.transcriptFacade = transcriptFacade;
    }

    // получить текст аудио GET /transcript/{uuid}/text
    @GetMapping(value = "/transcript/{uuid}/text")
    public ResponseEntity<List<WordMetadata>> getFullText(@PathVariable("uuid") String uuid) {
        List<WordMetadata> transcription = this.transcriptFacade.getTranscript(UUID.fromString(uuid));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(transcription);
    }

    // отправить размеченный текст POST /transcript/{uuid}/text [n1, n2, .... ]
    @PostMapping(value = "/transcript/{uuid}/chosen")
    public ResponseEntity<Void> setChosen(@PathVariable("uuid") String template_uuid,
                                          @RequestBody AudioTemplate audioTemplate) {
        this.transcriptFacade.setChosen(UUID.fromString(template_uuid), audioTemplate);
        return ResponseEntity.ok()
                .build();
    }

    // отправить размеченный текст POST /transcript/{uuid}/text [n1, n2, .... ]
    @GetMapping(value = "/transcript/{uuid}/chosen")
    public ResponseEntity<AudioTemplate> getChosen(@PathVariable("uuid") String template_uuid) {
        AudioTemplate chosen = this.transcriptFacade.getChosen(UUID.fromString(template_uuid));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(chosen);
    }
}
