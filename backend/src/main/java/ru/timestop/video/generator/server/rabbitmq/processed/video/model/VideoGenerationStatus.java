package ru.timestop.video.generator.server.rabbitmq.processed.video.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public record VideoGenerationStatus(UUID processedId,
                                    String status,
                                    String message) implements Serializable {
}
