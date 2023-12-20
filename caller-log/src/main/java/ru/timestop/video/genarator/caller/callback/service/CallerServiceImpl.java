package ru.timestop.video.genarator.caller.callback.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.genarator.caller.callback.CallerService;
import ru.timestop.video.genarator.caller.callback.model.CallbackMessage;
import ru.timestop.video.genarator.caller.callback.entity.CallbackEntity;
import ru.timestop.video.genarator.caller.callback.repository.CallbackRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class CallerServiceImpl implements CallerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallerServiceImpl.class);
    private final CallbackRepository callbackRepository;

    public CallerServiceImpl(@Autowired CallbackRepository callbackRepository) {
        this.callbackRepository = callbackRepository;
    }

    @Override
    public void saveCallbackMessage(CallbackMessage callbackMessage) {
        CallbackEntity callback = new CallbackEntity();
        callback.setPhone(callbackMessage.phone());
        callback.setName(callbackMessage.name());
        callback.setEmail(callbackMessage.email());
        callback.setCreation(Timestamp.valueOf(LocalDateTime.now()));
        this.callbackRepository.saveAndFlush(callback);
    }

    @Override
    @Transactional
    public List<CallbackEntity> getCallbackMessages() {
        LOGGER.warn("Extract all callback messages!");
        List<CallbackEntity> entities = this.callbackRepository.findAll();
        this.callbackRepository.deleteAll();
        return entities;
    }
}
