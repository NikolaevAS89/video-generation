package ru.timestop.video.generator.server.facade;

import ru.timestop.video.generator.server.facade.model.request.RequestStatus;
import ru.timestop.video.generator.server.facade.model.request.RequestToGenerateVideo;
import ru.timestop.video.generator.server.facade.model.response.RequestsStatus;
import ru.timestop.video.generator.server.storage.model.FilesContent;
import ru.timestop.video.generator.server.rabbitmq.processed.audio.model.AudioGenerationStatus;
import ru.timestop.video.generator.server.rabbitmq.processed.video.model.VideoGenerationStatus;

import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface VideoCompilerService {
    FilesContent readGeneratedVideo(UUID generatedVideosUuid);

    RequestsStatus createRequestToGenerate(RequestToGenerateVideo requestToGenerateVideo);

    void updateStatus(AudioGenerationStatus status);
    void updateStatus(VideoGenerationStatus status);

    List<RequestsStatus> getStatuses(List<RequestStatus> requestStatus);

    List<RequestsStatus> createRequestsToGenerate(List<RequestToGenerateVideo> requestToGenerateVideo);
}
