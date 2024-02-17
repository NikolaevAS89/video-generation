package ru.timestop.video.genarator.caller.callback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.genarator.caller.callback.CallbackUploaderService;
import ru.timestop.video.genarator.caller.callback.CallerService;
import ru.timestop.video.genarator.caller.callback.DemoRequestService;
import ru.timestop.video.genarator.caller.callback.entity.CallbackEntity;
import ru.timestop.video.genarator.caller.callback.entity.DemoRequestEntity;
import ru.timestop.video.genarator.caller.callback.model.response.CallbackRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class CallbackUploaderServiceImpl implements CallbackUploaderService {
    private final CallerService callerService;
    private final DemoRequestService demoRequestService;

    public CallbackUploaderServiceImpl(@Autowired CallerService callerService,
                                       @Autowired DemoRequestService demoRequestService) {
        this.callerService = callerService;
        this.demoRequestService = demoRequestService;
    }

    @Override
    public List<CallbackRequest> getCallbackRequests() {
        Map<UUID, DemoRequestEntity> demoRequestEntities = this.demoRequestService
                .getDemosRequests()
                .stream()
                .collect(Collectors.toMap(item -> item.getCallback().getId(), Function.identity()));
        return this.callerService
                .getCallbackMessages()
                .stream()
                .map(item -> toCallbackRequest(item, demoRequestEntities))
                .toList();
    }

    private static CallbackRequest toCallbackRequest(CallbackEntity callbackEntity,
                                                     Map<UUID, DemoRequestEntity> demos) {
        DemoRequestEntity demo = demos.get(callbackEntity.getId());
        return new CallbackRequest(callbackEntity.getPhone(),
                callbackEntity.getEmail(),
                callbackEntity.getName(),
                demo == null ? null : demo.getTemplateId(),
                demo == null ? Collections.emptyMap() : demo.getWords()
        );
    }
}
