package ru.timestop.video.genarator.caller.callback.model.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public record CallbackMessage(
        @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$", message = "Incorrect phone number")
        @NotNull(message = "Phone number should not be Null")
        String phone,
        @Pattern(regexp = "[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+", message = "Incorrect email")
        @NotNull(message = "Email should not be Null")
        String email,
        @Nullable
        @Size(max = 90, message = "To long name")
        String name) implements Callback {

}
