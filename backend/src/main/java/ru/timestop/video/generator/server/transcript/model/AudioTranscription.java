package ru.timestop.video.generator.server.transcript.model;

import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public record AudioTranscription(UUID uuid, List<WordMetadata> words) {
}
