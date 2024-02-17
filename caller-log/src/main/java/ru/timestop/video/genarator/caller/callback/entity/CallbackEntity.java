package ru.timestop.video.genarator.caller.callback.entity;

import jakarta.annotation.Nonnull;
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
@Table(name = "callback")
public class CallbackEntity implements Serializable {


    @Id
    @GeneratedValue
    private UUID id;

    @Nonnull
    private String phone;

    @Nonnull
    private String name;
    @Nonnull
    private String email;

    @Nonnull
    private Timestamp creation;


    public UUID getId() {
        return id;
    }

    public CallbackEntity setId(UUID id) {
        this.id = id;
        return this;
    }


    public String getName() {
        return name;
    }

    public CallbackEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CallbackEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public CallbackEntity setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Timestamp getCreation() {
        return creation;
    }

    public CallbackEntity setCreation(Timestamp creation) {
        this.creation = creation;
        return this;
    }
}
