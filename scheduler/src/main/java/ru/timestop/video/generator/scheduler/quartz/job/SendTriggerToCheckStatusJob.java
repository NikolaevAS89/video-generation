package ru.timestop.video.generator.scheduler.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.timestop.video.generator.scheduler.rabbitmq.MessageSender;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Component
public class SendTriggerToCheckStatusJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendTriggerToCheckStatusJob.class);

    @Autowired
    private MessageSender messageSender;

    public void execute(JobExecutionContext context) {
        LOGGER.info("Send message to check status of generation");
        this.messageSender.send("{\"action\":\"CheckStatus\"}");
    }
}