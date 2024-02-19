package ru.timestop.video.generator.server.processed.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Entity
@Table(name = "processed")
public class ProcessedEntity implements Serializable {
    @Id
    @GeneratedValue
    private UUID id;

    @Nonnull
    @JoinColumn(name = "template_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private TemplateEntity template;

    @Nullable
    private String status;
    @Column(name = "words", columnDefinition = "jsonb", nullable = false)
    @Type(JsonType.class)
    private Map<String, String> words;

    @Nonnull
    private Timestamp creation;

    public UUID getId() {
        return id;
    }

    public ProcessedEntity setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ProcessedEntity setStatus(String status) {
        this.status = status;
        return this;
    }

    public TemplateEntity getTemplate() {
        return template;
    }

    public ProcessedEntity setTemplate(TemplateEntity template) {
        this.template = template;
        return this;
    }

    public Map<String, String> getWords() {
        return words;
    }

    public ProcessedEntity setWords(Map<String, String> words) {
        this.words = words;
        return this;
    }

    public Timestamp getCreation() {
        return creation;
    }

    public ProcessedEntity setCreation(Timestamp creation) {
        this.creation = creation;
        return this;
    }
}
