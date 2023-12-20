package ru.timestop.video.generator.server.transcript.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.transcript.entity.TranscriptEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface TranscriptRepository extends JpaRepository<TranscriptEntity, UUID> {
    Optional<TranscriptEntity> findByTemplate(TemplateEntity task);

    void deleteByTemplate(TemplateEntity task);
}
