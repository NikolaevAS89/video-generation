package ru.timestop.video.genarator.caller.callback.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.genarator.caller.callback.CallerService;
import ru.timestop.video.genarator.caller.callback.DemoRequestService;
import ru.timestop.video.genarator.caller.callback.entity.CallbackEntity;
import ru.timestop.video.genarator.caller.callback.entity.DemoRequestEntity;
import ru.timestop.video.genarator.caller.callback.model.request.DemosRequest;
import ru.timestop.video.genarator.caller.callback.repository.DemosRequestRepository;

import java.util.List;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class DemoRequestServiceImpl implements DemoRequestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoRequestServiceImpl.class);
    private final DemosRequestRepository demosRequestRepository;
    private final CallerService callerService;

    public DemoRequestServiceImpl(@Autowired DemosRequestRepository demosRequestRepository,
                                  @Autowired CallerService callerService) {
        this.demosRequestRepository = demosRequestRepository;
        this.callerService = callerService;
    }

    @Override
    public void saveDemosRequest(DemosRequest demosRequest) {
        CallbackEntity callbackEntity = this.callerService.saveCallbackMessage(demosRequest);
        DemoRequestEntity request = new DemoRequestEntity();
        request.setTemplateId(demosRequest.templateId());
        request.setWords(demosRequest.words());
        request.setCallback(callbackEntity);
        this.demosRequestRepository.saveAndFlush(request);
    }

    @Override
    public List<DemoRequestEntity> getDemosRequests() {
        LOGGER.warn("Extract all demos request messages!");
        List<DemoRequestEntity> entities = this.demosRequestRepository.findAll();
        this.demosRequestRepository.deleteAll();
        return entities;
    }
}
