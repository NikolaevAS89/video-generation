package ru.timestop.video.generator.server.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.timestop.video.generator.server.demo.entity.DemoEntity;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.transcript.entity.TranscriptEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface DemoRepository extends JpaRepository<DemoEntity, Long> {
    Optional<DemoEntity> findFirstByOrderByIdDesc();
}
