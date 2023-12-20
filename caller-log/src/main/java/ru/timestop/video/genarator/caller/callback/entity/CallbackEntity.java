package ru.timestop.video.genarator.caller.callback.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Entity
@Table(name = "callback")
public class CallbackEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "caller_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private long id;

    @Nonnull
    private String phone;

    @Nonnull
    private String name;
    @Nonnull
    private String email;

    @Nonnull
    private Timestamp creation;


    public Long getId() {
        return id;
    }

    public CallbackEntity setId(Long id) {
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
