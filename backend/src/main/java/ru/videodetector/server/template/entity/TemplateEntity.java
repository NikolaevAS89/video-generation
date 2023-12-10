package ru.videodetector.server.template.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Entity
@Table(name = "templates")
public class TemplateEntity implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    @Nonnull
    private String name;

    @Nullable
    private String status;

    @Nonnull
    private Timestamp creation;

    public UUID getId() {
        return id;
    }

    public TemplateEntity setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TemplateEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public TemplateEntity setStatus(String status) {
        this.status = status;
        return this;
    }

    public Timestamp getCreation() {
        return creation;
    }

    public TemplateEntity setCreation(Timestamp creation) {
        this.creation = creation;
        return this;
    }
}
