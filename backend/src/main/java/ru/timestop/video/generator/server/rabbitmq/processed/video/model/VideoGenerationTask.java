package ru.timestop.video.generator.server.rabbitmq.processed.video.model;

import ru.timestop.video.generator.server.transcript.model.WordMetadata;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public record VideoGenerationTask(UUID templateId,
                                  UUID processedId,
                                  List<Integer> chosen,
                                  Map<String, Integer> mapping,
                                  Map<String, String> replacements,
                                  List<WordMetadata> originalWords) implements Serializable {
}
