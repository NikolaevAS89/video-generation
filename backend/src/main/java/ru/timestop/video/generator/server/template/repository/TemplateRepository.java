package ru.timestop.video.generator.server.template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface TemplateRepository extends JpaRepository<TemplateEntity, UUID> {
}
