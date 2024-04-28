package ru.timestop.video.generator.server.demo.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.timestop.video.generator.server.demo.DemoService;
import ru.timestop.video.generator.server.demo.entity.DemoEntity;
import ru.timestop.video.generator.server.demo.repository.DemoRepository;
import ru.timestop.video.generator.server.template.TemplateService;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.io.InputStream;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Component
public class DemoServiceImpl implements DemoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoServiceImpl.class);
    private final DemoRepository demoRepository;
    private final TemplateService templateService;

    public DemoServiceImpl(@Autowired DemoRepository demoRepository,
                           @Autowired TemplateService templateService) {
        this.demoRepository = demoRepository;
        this.templateService = templateService;
    }

    @Override
    @Transactional
    public TemplateEntity createDemo(String filename, InputStream stream) {
        TemplateEntity template = this.templateService.createTemplate(filename, stream);
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setTemplate(template);
        demoRepository.saveAndFlush(demoEntity);
        return template;
    }

    @Override
    public TemplateEntity getDemo() {
        return demoRepository.findFirstByOrderByIdDesc()
                .orElseThrow()
                .getTemplate();
    }
}
