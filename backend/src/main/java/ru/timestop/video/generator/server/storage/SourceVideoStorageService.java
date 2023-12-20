package ru.timestop.video.generator.server.storage;

import ru.timestop.video.generator.server.storage.model.FilesContent;

import java.io.InputStream;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface SourceVideoStorageService {

    void deleteFolder(UUID sourceVideosUuid);

    void saveSourceVideo(UUID sourceVideosUuid, InputStream stream);

    FilesContent readSourceVideo(UUID sourceVideosUuid);

}
