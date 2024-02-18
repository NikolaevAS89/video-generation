package ru.timestop.video.generator.server.storage.service;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.timestop.video.generator.server.storage.SourceVideoStorageService;
import ru.timestop.video.generator.server.storage.model.FilesContent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class LocalSourceVideoStorageStorageService implements SourceVideoStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalSourceVideoStorageStorageService.class);
    private static final String FOLDER = "/storage";

    @Override
    public void deleteFolder(UUID uuid) {
        String path = FOLDER + "/" + uuid.toString();
        try {
            FileUtils.deleteDirectory(new File(path));
        } catch (IOException e) {
            LOGGER.error("Can't delete " + path, e);
        }
    }

    @Override
    public void saveSourceVideo(UUID uuid, InputStream stream) {
        File source_file = getFileName(uuid);
        try {
            Files.createDirectories(source_file.getParentFile().toPath());
            Files.copy(stream, source_file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e); //  TODO make custom exception
        }
    }

    @Override
    public FilesContent readSourceVideo(UUID uuid) {
        File source_file = getFileName(uuid);
        try {
            long size = Files.size(source_file.toPath());
            InputStream content = new FileInputStream(source_file);
            return new FilesContent(size, content);
        } catch (IOException e) {
            LOGGER.error("A read was failed : " + uuid, e);
            throw new RuntimeException(e); //  TODO make custom exception
        }
    }

    @Override
    public FilesContent readGeneratedVideo(UUID template_uuid, UUID request_uuid) {
        File source_file = getFileName(template_uuid, request_uuid);
        try {
            long size = Files.size(source_file.toPath());
            InputStream content = new FileInputStream(source_file);
            return new FilesContent(size, content);
        } catch (IOException e) {
            LOGGER.error("A read was failed : " + source_file, e);
            throw new RuntimeException(e); //  TODO make custom exception
        }
    }

    private static File getFileName(UUID uuid) {
        return new File(FOLDER + "/" + uuid.toString() + "/" + "source");
    }

    private static File getFileName(UUID template_uuid, UUID request_uuid) {
        return new File(FOLDER + "/" + template_uuid.toString() + "/generated/" + request_uuid.toString());
    }
}
