package ru.timestop.video.generator.server.processed.model.response;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public record RequestsStatus(
        @Pattern(regexp = "^[0-9]+$", message = "Incorrect id. Only numbers is allowed")
        @NotNull(message = "A id should not be Null")
        String id,
        @NotNull(message = "A request id should not be Null")
        UUID uuid,
        @Nullable
        String status
) {
}
