package ru.videodetector.server.controller;

import org.reactivestreams.Publisher;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Mono;
import ru.videodetector.server.controller.model.Status;
import ru.videodetector.server.utilites.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@RestController
@CrossOrigin
public class FrontendController {
    // загрузить видео POST /video/upload responce {"uuid": "......"}
    @PostMapping(value = "/videos/upload")
    public Mono<ResponseEntity<Mono<UUID>>> uploadFile(@RequestPart("file") Mono<FilePart> filePartMono) {
        return filePartMono.doOnNext(fp -> System.out.println("Receiving File:" + fp.filename()))
                .flatMap(filePart -> {
                    UUID uuid = UUID.randomUUID();
                    String filename = filePart.filename();
                    return filePart.transferTo(new File(filename)).then(Mono.just(uuid));
                })
                .map(uuid -> ResponseEntity.ok().body(Mono.just(uuid)));
    }

    // получить текст аудио GET /audio/{uuid}/text
    @GetMapping(value = "/audio/{uuid}/text")
    public ResponseEntity<Mono<List<String>>> getText(@PathVariable("uuid") String uuid) {
        List<String> text = Arrays.stream(new String[]{"Hi", "man", "!"}).toList(); //TODO
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(text));
    }

    // отправить размеченный текст POST /audio/{uuid}/text [n1, n2, .... ]
    @PostMapping(value = "/audio/{uuid}/text")
    public ResponseEntity<Void> setText(@PathVariable("uuid") String uuid,
                                        @RequestBody Publisher<List<Integer>> chose) {
        return ResponseEntity.ok()
                .build(); // TODO
    }

    // скачать видео GET /video/{uuid}/download
    @GetMapping(value = "/video/{uuid}/download",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadVideo(@PathVariable("uuid") String uuid) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statistic.csv");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        InputStreamResource isr = new InputStreamResource(new FileInputStream("example.mp4")); // TODO
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(isr);
    }

    // проиграть исходное видео GET /video/{uuid}
    @GetMapping(value = "/video/{uuid}")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> getVideoToPlay(@PathVariable("uuid") String uuid,
                                                                @RequestHeader(value = "Range", required = false)
                                                                String rangeHeader) throws Exception {
        StreamingResponseBody responseStream;
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "video/mp4");
        headers.add("Accept-Ranges", "bytes");
        headers.set("Content-Transfer-Encoding", "binary");
        long contentLength = Files.size(new File("example.mp4").toPath());
        if (rangeHeader == null) {
            String range = "bytes 0-" + (contentLength - 1L) + "/" + contentLength;
            headers.add("Content-Length", String.valueOf(contentLength));
            headers.add("Content-Range", range);
            responseStream = os -> {
                try (InputStream is = new FileInputStream("example.mp4")) {
                    IOUtils.transferTo(is, os, 0L, contentLength);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            return new ResponseEntity<>(responseStream, headers, HttpStatus.OK);
        } else {
            String[] ranges = rangeHeader.replaceAll("bytes=", "")
                    .split("-");
            long rangeStart = Long.parseLong(ranges[0]);
            long rangeEnd = ranges.length > 1 ? Math.min(Long.parseLong(ranges[1]), contentLength) : contentLength;

            String range = "bytes" + " " +
                    rangeStart + "-" + (rangeEnd - 1L) + "/" + contentLength;
            headers.add("Content-Length", String.valueOf(rangeEnd - rangeStart));
            headers.add("Content-Range", range);
            responseStream = os -> {
                try (InputStream is = new FileInputStream("example.mp4")) {
                    IOUtils.transferTo(is, os, rangeStart, rangeEnd);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            return new ResponseEntity<>(responseStream, headers, HttpStatus.PARTIAL_CONTENT);
        }
    }

    // проиграть видео с измененной звуковой дорожкой GET /video/{uuid}/processed request body {"{patern_name1}": "....", ....}
    @GetMapping(value = "/video/{uuid}/processed")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> getProcessedToPlay(@PathVariable("uuid") String uuid,
                                                                    @RequestHeader(value = "Range", required = false)
                                                                    String rangeHeader,
                                                                    @RequestBody Publisher<List<Integer>> chose) throws Exception {
        StreamingResponseBody responseStream;
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "video/mp4");
        headers.add("Accept-Ranges", "bytes");
        headers.set("Content-Transfer-Encoding", "binary");
        long contentLength = Files.size(new File("example.mp4").toPath());
        if (rangeHeader == null) {
            String range = "bytes 0-" + (contentLength - 1L) + "/" + contentLength;
            headers.add("Content-Length", String.valueOf(contentLength));
            headers.add("Content-Range", range);
            responseStream = os -> {
                try (InputStream is = new FileInputStream("example.mp4")) {
                    IOUtils.transferTo(is, os, 0L, contentLength);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            return new ResponseEntity<>(responseStream, headers, HttpStatus.OK);
        } else {
            String[] ranges = rangeHeader.replaceAll("bytes=", "")
                    .split("-");
            long rangeStart = Long.parseLong(ranges[0]);
            long rangeEnd = ranges.length > 1 ? Math.min(Long.parseLong(ranges[1]), contentLength) : contentLength;

            String range = "bytes" + " " +
                    rangeStart + "-" + (rangeEnd - 1L) + "/" + contentLength;
            headers.add("Content-Length", String.valueOf(rangeEnd - rangeStart));
            headers.add("Content-Range", range);
            responseStream = os -> {
                try (InputStream is = new FileInputStream("example.mp4")) {
                    IOUtils.transferTo(is, os, rangeStart, rangeEnd);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            return new ResponseEntity<>(responseStream, headers, HttpStatus.PARTIAL_CONTENT);
        }
    }

    // статус обработки /status/{uuid} {"status": "....."}
    @GetMapping(value = "/status/{uuid}")
    public ResponseEntity<Mono<Status>> getStatus(@PathVariable("uuid") String uuid) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new Status("All right!"))); // TODO v
    }
}
