package ru.timestop.video.generator.scheduler.quartz;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.simpl.SimpleJobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import ru.timestop.video.generator.scheduler.quartz.job.SendTriggerToCheckStatusJob;
import ru.timestop.video.generator.scheduler.quartz.job.SendTriggerToGenerateVideosJob;
import ru.timestop.video.generator.scheduler.quartz.job.SendTriggerToUploadCallbackListJob;

import java.util.Arrays;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Configuration
public class QuartzContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendTriggerToUploadCallbackListJob.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Bean(name = "SendTriggerToUploadCallbackListJob")
    public JobDetailFactoryBean jobDetailFireUploadDataToGoogle() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(SendTriggerToUploadCallbackListJob.class);
        jobDetailFactory.setDurability(true);
        jobDetailFactory.setApplicationContext(applicationContext);
        return jobDetailFactory;
    }

    @Bean(name = "SendTriggerToGenerateVideosJob")
    public JobDetailFactoryBean jobDetailFireGenerateVideosByGoogleData() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(SendTriggerToGenerateVideosJob.class);
        jobDetailFactory.setDurability(true);
        jobDetailFactory.setApplicationContext(applicationContext);
        return jobDetailFactory;
    }

    @Bean(name = "SendTriggerToCheckStatusJob")
    public JobDetailFactoryBean SendTriggerToCheckStatusJob() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(SendTriggerToCheckStatusJob.class);
        jobDetailFactory.setDurability(true);
        jobDetailFactory.setApplicationContext(applicationContext);
        return jobDetailFactory;
    }

    @Bean
    public CronTriggerFactoryBean triggerFireUploadDataToGoogle(@Qualifier("SendTriggerToUploadCallbackListJob") JobDetail job) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setCronExpression("0 0 6 * * ?"); // TODO
        trigger.setBeanName("FireUploadDataToGoogleTrigger");
        trigger.setDescription("FireUploadDataToGoogle");
        return trigger;
    }

    @Bean
    public CronTriggerFactoryBean triggerFireGenerateVideosByGoogleData(@Qualifier("SendTriggerToGenerateVideosJob") JobDetail job) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setCronExpression("0 0 20 * * ?"); // TODO
        trigger.setBeanName("FireGenerateVideosByGoogleDataTrigger");
        trigger.setDescription("FireGenerateVideosByGoogleData");
        return trigger;
    }

    @Bean
    public CronTriggerFactoryBean triggerFireCheckStatus(@Qualifier("SendTriggerToCheckStatusJob") JobDetail job) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setCronExpression("0 * * * * ?"); // TODO
        trigger.setBeanName("FireCheckStatusTrigger");
        trigger.setDescription("FireCheckStatus");
        return trigger;
    }

    @Bean
    public SchedulerFactoryBean scheduler(Trigger... triggers) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));
        schedulerFactory.setJobFactory(new SimpleJobFactory());
        JobDetail[] jobDetails = (JobDetail[]) Arrays.stream(triggers)
                .map(trigger -> (JobDetail) trigger.getJobDataMap().get("jobDetail"))
                .toArray();
        schedulerFactory.setJobDetails(jobDetails);
        schedulerFactory.setTriggers(triggers);
        schedulerFactory.setApplicationContextSchedulerContextKey("applicationContext");
        return schedulerFactory;
    }
}
