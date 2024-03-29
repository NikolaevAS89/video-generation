package ru.timestop.video.genarator.caller.callback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.timestop.video.genarator.caller.callback.entity.CallbackEntity;

import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface CallbackRepository extends JpaRepository<CallbackEntity, UUID> {
}
