package ru.timestop.video.generator.gateway;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.timestop.video.generator.gateway.model.BasicAuthProperties;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@SpringBootApplication
@EnableConfigurationProperties(BasicAuthProperties.class)
public class Application {
    /**
        https://cloud.google.com/docs/authentication/provide-credentials-adc#local-dev
        https://cloud.google.com/iam/docs/keys-create-delete#iam-service-account-keys-create-java
        TODO add recapcha
        https://developers.google.com/recaptcha/docs/verify
        https://www.google.com/recaptcha/admin/site/691258300
        https://console.cloud.google.com/security/recaptcha/6LebqzMpAAAAAB2y-gG6KSGPUaM5lRGSlbvduxgU/integration?orgonly=true&project=copper-guide-356420&supportedpurview=organizationId
        TODO add google Time-driven triggers
        https://developers.google.com/apps-script/guides/triggers/installable#time-driven_triggers
        TODO add simple security to:
        - delete template ???
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .run(args);
    }
}

