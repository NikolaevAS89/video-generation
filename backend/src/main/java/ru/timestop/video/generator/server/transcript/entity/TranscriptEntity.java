package ru.timestop.video.generator.server.transcript.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.transcript.model.WordMetadata;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Entity
@Table(name = "transcript")
public class TranscriptEntity implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    @Nonnull
    @JoinColumn(name = "template_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TemplateEntity template;

    @Column(name = "transcript", columnDefinition = "jsonb", nullable = false)
    @Type(JsonType.class)
    private List<WordMetadata> transcript;

    @Column(name = "chosen", columnDefinition = "jsonb", nullable = false)
    @Type(JsonType.class)
    private List<Integer> chosen;

    @Nonnull
    private Timestamp creation;

    public UUID getId() {
        return id;
    }

    public TranscriptEntity setId(UUID id) {
        this.id = id;
        return this;
    }

    public TemplateEntity getTemplate() {
        return template;
    }

    public TranscriptEntity setTemplate(TemplateEntity template) {
        this.template = template;
        return this;
    }

    public List<WordMetadata> getTranscript() {
        return transcript;
    }

    public TranscriptEntity setTranscript(List<WordMetadata> transcript) {
        this.transcript = transcript;
        return this;
    }

    public List<Integer> getChosen() {
        return chosen;
    }

    public TranscriptEntity setChosen(List<Integer> chosen) {
        this.chosen = chosen;
        return this;
    }

    public Timestamp getCreation() {
        return creation;
    }

    public TranscriptEntity setCreation(Timestamp creation) {
        this.creation = creation;
        return this;
    }
}
