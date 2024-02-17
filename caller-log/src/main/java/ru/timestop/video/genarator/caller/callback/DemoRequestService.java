package ru.timestop.video.genarator.caller.callback;

import ru.timestop.video.genarator.caller.callback.entity.DemoRequestEntity;
import ru.timestop.video.genarator.caller.callback.model.request.DemosRequest;

import java.util.List;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface DemoRequestService {
    void saveDemosRequest(DemosRequest callbackMessage);

    List<DemoRequestEntity> getDemosRequests();
}
