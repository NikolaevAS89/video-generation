package ru.timestop.video.generator.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.timestop.video.generator.server.storage.SourceVideoStorageService;
import ru.timestop.video.generator.server.storage.model.FilesContent;
import ru.timestop.video.generator.server.utilites.IOUtils;

import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@RestController
@CrossOrigin
public class FileStorageController {

    private final SourceVideoStorageService sourceVideoStorageService;

    public FileStorageController(@Autowired SourceVideoStorageService sourceVideoStorageService) {
        this.sourceVideoStorageService = sourceVideoStorageService;
    }

    // скачать видео GET /video/{uuid}/download
    @GetMapping(value = "/storage/{uuid}/download",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadSourceVideo(@PathVariable("uuid") String uuid) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=source.mp4");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        FilesContent filesContent = sourceVideoStorageService.readSourceVideo(UUID.fromString(uuid));
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(filesContent.content()));
    }

    // проиграть исходное видео GET /video/{uuid}
    @GetMapping(value = "/storage/{uuid}")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> getSourceToPlay(@PathVariable("uuid") String uuid,
                                                                 @RequestHeader(value = "Range", required = false)
                                                                 String rangeHeader) {
        FilesContent filesContent = this.sourceVideoStorageService.readSourceVideo(UUID.fromString(uuid));
        return prepareContent(filesContent, rangeHeader);
    }

    private static ResponseEntity<StreamingResponseBody> prepareContent(FilesContent filesContent, String rangeHeader) {
        StreamingResponseBody responseStream;
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "video/mp4");
        headers.add("Accept-Ranges", "bytes");
        headers.set("Content-Transfer-Encoding", "binary");
        long contentLength = filesContent.size();
        if (rangeHeader == null) {
            String range = "bytes 0-" + (contentLength - 1L) + "/" + contentLength;
            headers.add("Content-Length", String.valueOf(contentLength));
            headers.add("Content-Range", range);
            responseStream = os -> IOUtils.transferTo(filesContent.content(), os, 0L, contentLength);
            return new ResponseEntity<>(responseStream, headers, HttpStatus.OK);
        } else {
            String[] ranges = rangeHeader.replaceAll("bytes=", "").split("-");
            long rangeStart = Long.parseLong(ranges[0]);
            long rangeEnd = ranges.length > 1 ? Math.min(Long.parseLong(ranges[1]), contentLength) : contentLength;
            String range = "bytes" + " " + rangeStart + "-" + (rangeEnd - 1L) + "/" + contentLength;
            headers.add("Content-Length", String.valueOf(rangeEnd - rangeStart));
            headers.add("Content-Range", range);
            responseStream = os -> IOUtils.transferTo(filesContent.content(), os, rangeStart, rangeEnd);
            return new ResponseEntity<>(responseStream, headers, HttpStatus.PARTIAL_CONTENT);
        }
    }
}
