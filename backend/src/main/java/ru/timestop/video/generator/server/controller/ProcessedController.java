package ru.timestop.video.generator.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.timestop.video.generator.server.facade.VideoCompilerService;
import ru.timestop.video.generator.server.facade.model.request.RequestStatus;
import ru.timestop.video.generator.server.facade.model.request.RequestToGenerateVideo;
import ru.timestop.video.generator.server.facade.model.response.RequestsStatus;
import ru.timestop.video.generator.server.storage.model.FilesContent;
import ru.timestop.video.generator.server.utilites.IOUtils;

import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@RestController
@CrossOrigin
public class ProcessedController {
    private final VideoCompilerService videoCompilerService;

    public ProcessedController(@Autowired VideoCompilerService videoCompilerService) {
        this.videoCompilerService = videoCompilerService;
    }

    @GetMapping(value = "/generator/list/status")
    public List<RequestsStatus> getStatuses(@RequestBody List<RequestStatus> requests) {
        return videoCompilerService.getStatuses(requests);
    }

    @PostMapping(value = "/generator/list")
    public List<RequestsStatus> createTasks(@RequestBody List<RequestToGenerateVideo> requests) {
        return videoCompilerService.createRequestsToGenerate(requests);
    }

    // проиграть видео с измененной звуковой дорожкой GET /video/{uuid}/processed request body {"{patern_name1}": "....", ....}
    @GetMapping(value = "/processed/{uuid}")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> getProcessedToPlay(@PathVariable("uuid") String uuid,
                                                                    @RequestHeader(value = "Range", required = false)
                                                                    String rangeHeader) {
        FilesContent filesContent = this.videoCompilerService.readGeneratedVideo(UUID.fromString(uuid));
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
