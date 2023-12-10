package ru.videodetector.server.transcript.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.videodetector.server.template.entity.TemplateEntity;
import ru.videodetector.server.transcript.entity.TranscriptEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface TranscriptRepository extends JpaRepository<TranscriptEntity, UUID> {
    Optional<TranscriptEntity> findByTemplate(TemplateEntity task);

    void deleteByTemplate(TemplateEntity task);
}
