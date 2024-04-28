package ru.timestop.video.generator.server.rabbitmq.transcript.model;

import ru.timestop.video.generator.server.transcript.model.WordMetadata;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public record AudioTranscription(UUID uuid, String status, String message, List<WordMetadata> words) implements Serializable {
}
