package ru.timestop.video.generator.server.storage.model;

import java.io.InputStream;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public record FilesContent(long size, InputStream content) {
}
