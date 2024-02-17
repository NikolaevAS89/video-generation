package ru.timestop.video.genarator.caller.callback.model.response;

import java.util.Map;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public record CallbackRequest(
        String phone,
        String email,
        String name,
        UUID templateId,
        Map<String, String> words
) {

}