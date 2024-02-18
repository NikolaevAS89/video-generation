package ru.timestop.video.generator.server.processed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.timestop.video.generator.server.processed.entity.ProcessedEntity;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface ProcessedRepository extends JpaRepository<ProcessedEntity, UUID> {
    Optional<ProcessedEntity> findByTemplate(TemplateEntity task);
}
