from faster_whisper import WhisperModel


class TranscriptionService:
    def __init__(self,
                 model_size,
                 device,
                 compute_type,
                 beam_size):
        self.answer = []
        self.__model_size = model_size
        self.__device = device
        self.__compute_type = compute_type
        self.__beam_size = beam_size
        self.__model_ = WhisperModel(model_size_or_path=self.__model_size,
                                     device=self.__device,
                                     compute_type=self.__compute_type)

    def generate_subs(self,
                      uuid: str,
                      audio_path: str) -> dict[str, list | str]:
        segments, info = self.__model_.transcribe(audio=audio_path,
                                                  beam_size=self.__beam_size,
                                                  word_timestamps=True)

        segment_subs = {
            "uuid": uuid,
            "words": []}

        for segment in segments:
            for word in segment.words:
                segment_subs['words'].append({
                    "word": word.word,
                    "start": word.start,
                    "end": word.end
                })

        return segment_subs
