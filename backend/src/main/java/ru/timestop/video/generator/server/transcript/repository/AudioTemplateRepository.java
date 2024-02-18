package ru.timestop.video.generator.server.transcript.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.transcript.entity.AudioTemplateEntity;
import ru.timestop.video.generator.server.transcript.entity.TranscriptEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface AudioTemplateRepository extends JpaRepository<AudioTemplateEntity, UUID> {
    Optional<AudioTemplateEntity> findByTemplate(TemplateEntity task);

    void deleteByTemplate(TemplateEntity task);
}
