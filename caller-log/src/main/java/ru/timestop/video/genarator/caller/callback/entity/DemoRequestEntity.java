package ru.timestop.video.genarator.caller.callback.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Entity
@Table(name = "demo_request")
public class DemoRequestEntity implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    @Nonnull
    @JoinColumn(name = "callback_id", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private CallbackEntity callback;

    @Nonnull
    @Column(name = "template_id", nullable = false)
    private UUID templateId;

    @Column(name = "words", columnDefinition = "jsonb", nullable = false)
    @Type(JsonType.class)
    private Map<String, String> words;

    public UUID getId() {
        return id;
    }

    public DemoRequestEntity setId(UUID id) {
        this.id = id;
        return this;
    }


    public CallbackEntity getCallback() {
        return this.callback;
    }

    public DemoRequestEntity setCallback(CallbackEntity callback) {
        this.callback = callback;
        return this;
    }

    public UUID getTemplateId() {
        return this.templateId;
    }

    public DemoRequestEntity setTemplateId(UUID templateId) {
        this.templateId = templateId;
        return this;
    }

    public Map<String, String> getWords() {
        return this.words;
    }

    public DemoRequestEntity setWords(Map<String, String> words) {
        this.words = words;
        return this;
    }
}
