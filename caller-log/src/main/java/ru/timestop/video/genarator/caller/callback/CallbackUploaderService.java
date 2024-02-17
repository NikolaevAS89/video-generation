package ru.timestop.video.genarator.caller.callback;

import ru.timestop.video.genarator.caller.callback.model.response.CallbackRequest;

import java.util.List;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface CallbackUploaderService {

    List<CallbackRequest> getCallbackRequests();
}
