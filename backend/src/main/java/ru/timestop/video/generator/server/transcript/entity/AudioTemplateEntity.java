package ru.timestop.video.generator.server.transcript.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Entity
@Table(name = "audio_template")
public class AudioTemplateEntity implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    @Nonnull
    @JoinColumn(name = "template_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TemplateEntity template;

    @Column(name = "mapping", columnDefinition = "jsonb", nullable = false)
    @Type(JsonType.class)
    private Map<String, Integer> mapping;

    @Column(name = "chosen", columnDefinition = "jsonb", nullable = false)
    @Type(JsonType.class)
    private List<Integer> chosen;

    @Nonnull
    private Timestamp creation;

    public UUID getId() {
        return id;
    }

    public AudioTemplateEntity setId(UUID id) {
        this.id = id;
        return this;
    }

    public TemplateEntity getTemplate() {
        return template;
    }

    public AudioTemplateEntity setTemplate(TemplateEntity template) {
        this.template = template;
        return this;
    }

    public Map<String, Integer> getMapping() {
        return mapping;
    }

    public AudioTemplateEntity setMapping(Map<String, Integer> mapping) {
        this.mapping = mapping;
        return this;
    }

    public List<Integer> getChosen() {
        return chosen;
    }

    public AudioTemplateEntity setChosen(List<Integer> chosen) {
        this.chosen = chosen;
        return this;
    }

    public Timestamp getCreation() {
        return creation;
    }

    public AudioTemplateEntity setCreation(Timestamp creation) {
        this.creation = creation;
        return this;
    }
}
