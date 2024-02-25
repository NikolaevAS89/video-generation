package ru.timestop.video.generator.scheduler.quartz;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import ru.timestop.video.generator.scheduler.quartz.job.SendTriggerToCheckStatusJob;
import ru.timestop.video.generator.scheduler.quartz.job.SendTriggerToGenerateVideosJob;
import ru.timestop.video.generator.scheduler.quartz.job.SendTriggerToUploadCallbackListJob;


/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Configuration
public class QuartzContext {

    @Bean(name = "SendTriggerToUploadCallbackListJob")
    public JobDetailFactoryBean jobDetailFireUploadDataToGoogle(ApplicationContext applicationContext) {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(SendTriggerToUploadCallbackListJob.class);
        jobDetailFactory.setDurability(true);
        jobDetailFactory.setApplicationContext(applicationContext);
        return jobDetailFactory;
    }

    @Bean(name = "SendTriggerToGenerateVideosJob")
    public JobDetailFactoryBean jobDetailFireGenerateVideosByGoogleData(ApplicationContext applicationContext) {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(SendTriggerToGenerateVideosJob.class);
        jobDetailFactory.setDurability(true);
        jobDetailFactory.setApplicationContext(applicationContext);
        return jobDetailFactory;
    }

    @Bean(name = "SendTriggerToCheckStatusJob")
    public JobDetailFactoryBean SendTriggerToCheckStatusJob(ApplicationContext applicationContext) {
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
        trigger.setCronExpression("0 0 20 * * ?"); // TODO
        trigger.setBeanName("FireUploadDataToGoogleTrigger");
        trigger.setDescription("FireUploadDataToGoogle");
        return trigger;
    }

    @Bean
    public CronTriggerFactoryBean triggerFireGenerateVideosByGoogleData(@Qualifier("SendTriggerToGenerateVideosJob") JobDetail job) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setCronExpression("0 30 19 * * ?"); // TODO
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
    public SpringBeanJobFactory springBeanJobFactory(ApplicationContext applicationContext) {
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean scheduler(SpringBeanJobFactory springBeanJobFactory, Trigger... triggers) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));
        schedulerFactory.setJobFactory(springBeanJobFactory);
        schedulerFactory.setTriggers(triggers);
        schedulerFactory.setApplicationContextSchedulerContextKey("applicationContext");
        return schedulerFactory;
    }
}
