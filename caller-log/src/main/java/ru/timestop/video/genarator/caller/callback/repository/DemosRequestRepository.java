package ru.timestop.video.genarator.caller.callback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.timestop.video.genarator.caller.callback.entity.DemoRequestEntity;

import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface DemosRequestRepository extends JpaRepository<DemoRequestEntity, UUID> {
}
