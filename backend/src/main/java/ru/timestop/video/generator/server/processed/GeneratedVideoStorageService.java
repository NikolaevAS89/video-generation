package ru.timestop.video.generator.server.processed;

import ru.timestop.video.generator.server.processed.model.request.RequestStatus;
import ru.timestop.video.generator.server.processed.model.request.RequestToGenerateVideo;
import ru.timestop.video.generator.server.processed.model.response.RequestsStatus;
import ru.timestop.video.generator.server.storage.model.FilesContent;

import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface GeneratedVideoStorageService {
    FilesContent readGeneratedVideo(UUID generatedVideosUuid);

    RequestsStatus getStatus(RequestStatus requestStatus);

    RequestsStatus createRequestToGenerate(RequestToGenerateVideo requestToGenerateVideo);

    List<RequestsStatus> getStatuses(List<RequestStatus> requestStatus);

    List<RequestsStatus> createRequestsToGenerate(List<RequestToGenerateVideo> requestToGenerateVideo);
}
