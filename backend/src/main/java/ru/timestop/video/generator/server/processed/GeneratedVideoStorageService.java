package ru.timestop.video.generator.server.processed;

import ru.timestop.video.generator.server.storage.model.FilesContent;

import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface GeneratedVideoStorageService {
    FilesContent readGeneratedVideo(UUID generatedVideosUuid);
}
