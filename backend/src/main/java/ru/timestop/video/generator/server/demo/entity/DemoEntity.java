package ru.timestop.video.generator.server.demo.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Entity
@Table(name = "demos")
public class DemoEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "demos_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    private long id;

    @Nonnull
    @JoinColumn(name = "template_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TemplateEntity template;

    public long getId() {
        return id;
    }

    public DemoEntity setId(long id) {
        this.id = id;
        return this;
    }

    public TemplateEntity getTemplate() {
        return template;
    }

    public DemoEntity setTemplate(TemplateEntity template) {
        this.template = template;
        return this;
    }
}
