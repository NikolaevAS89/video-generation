package ru.videodetector.server.processed;

import ru.videodetector.server.storage.model.FilesContent;

import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface GeneratedVideoStorageService {
    FilesContent readGeneratedVideo(UUID generatedVideosUuid);
}
