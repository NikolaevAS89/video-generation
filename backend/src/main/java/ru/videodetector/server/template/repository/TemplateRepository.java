package ru.videodetector.server.template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.videodetector.server.template.entity.TemplateEntity;

import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface TemplateRepository extends JpaRepository<TemplateEntity, UUID> {
}
