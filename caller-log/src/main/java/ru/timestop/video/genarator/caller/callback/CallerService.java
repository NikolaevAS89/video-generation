package ru.timestop.video.genarator.caller.callback;

import ru.timestop.video.genarator.caller.callback.entity.CallbackEntity;
import ru.timestop.video.genarator.caller.callback.model.request.Callback;

import java.util.List;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface CallerService {
    CallbackEntity saveCallbackMessage(Callback callbackMessage);

    List<CallbackEntity> getCallbackMessages();
}
