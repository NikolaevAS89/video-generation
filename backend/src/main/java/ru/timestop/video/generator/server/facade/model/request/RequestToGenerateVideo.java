package ru.timestop.video.generator.server.facade.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Map;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public record RequestToGenerateVideo(
        @Pattern(regexp = "^[0-9]+$", message = "Incorrect id. Only numbers is allowed")
        @NotNull(message = "A id should not be Null")
        String id,
        @NotNull(message = "A template id should not be Null")
        UUID uuid,
        @NotNull(message = "A words map should not be Null")
        Map<String, String> words
) {
}
