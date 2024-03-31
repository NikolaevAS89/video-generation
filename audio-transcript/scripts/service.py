import logging
from sys import stdout

from faster_whisper import WhisperModel
from moviepy.video.io.VideoFileClip import VideoFileClip

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
logFormatter = logging.Formatter("%(name)-12s %(asctime)s %(levelname)-8s %(filename)s:%(funcName)s %(message)s")
consoleHandler = logging.StreamHandler(stdout)
consoleHandler.setFormatter(logFormatter)
logger.addHandler(consoleHandler)


class StoragePathService:

    def __init__(self, root_path: str = "/storage"):
        self._root_path_ = root_path

    def get_root_dir(self):
        return self._root_path_

    def get_sources_path(self, uuid: str):
        return f'{self._root_path_}/{uuid}/source'

    def get_audio_transcription_path(self, uuid: str):
        return f'{self._root_path_}/{uuid}/audio.mp3'


class TranscriptionService:
    def __init__(self,
                 storage_path_service: StoragePathService,
                 model_size: str = "medium",
                 device: str = "cuda",
                 compute_type: str = "float32",
                 beam_size: int = 5):
        self._beam_size_ = beam_size
        self._model_ = WhisperModel(model_size_or_path=model_size,
                                    device=device,
                                    compute_type=compute_type)
        self._storage_path_service_ = storage_path_service

    def generate_subs(self,
                      uuid: str) -> dict[str, list | str]:
        audio_path = self._storage_path_service_.get_audio_transcription_path(uuid=uuid)
        segments, info = self._model_.transcribe(audio=audio_path,
                                                 beam_size=self._beam_size_,
                                                 word_timestamps=True)

        segment_subs = {
            "uuid": uuid,
            "status": "Successes",
            "message": "The audio has been transcribed.",
            "words": []}

        for segment in segments:
            for word in segment.words:
                segment_subs['words'].append({
                    "word": word.word,
                    "start": word.start,
                    "end": word.end
                })

        return segment_subs


class MediaService:

    def __init__(self, storage: StoragePathService):
        self._storage_ = storage

    def split_source(self, uuid: str) -> None:
        video_clip = VideoFileClip(self._storage_.get_sources_path(uuid))
        audio_clip = video_clip.audio
        audio_clip.write_audiofile(self._storage_.get_audio_transcription_path(uuid))
