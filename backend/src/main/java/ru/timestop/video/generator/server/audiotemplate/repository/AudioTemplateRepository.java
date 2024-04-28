package ru.timestop.video.generator.server.audiotemplate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.audiotemplate.entity.AudioTemplateEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface AudioTemplateRepository extends JpaRepository<AudioTemplateEntity, UUID> {
    Optional<AudioTemplateEntity> findByTemplate(TemplateEntity task);

    void deleteByTemplate(TemplateEntity task);
}
