from moviepy.editor import VideoFileClip
from faster_whisper import WhisperModel

class StorageService:
    def __init__(self, path):
        self.__path = path
        self.answer = []
    def get_video_path(self, uuid):
        self.__video_path = self.__path + "/storage/" + uuid + "/"
    def get_source(self):
        self.__source = rf'{self.__video_path + "source"}'
        return self.__source

class StorageManipulatorService:
    def __init__(self):
        self.answer = []
    def split_source(self, source) -> None:
        video_clip = VideoFileClip(source)
        storage_path = source.replace("source", "")
        print(storage_path)
        audio_clip = video_clip.audio
        audio_clip.write_audiofile(storage_path + "audio.mp3")
        video_clip.without_audio().write_videofile(storage_path +"video.mp4")

    def generate_subs(self, source_path, uuid: str, model_size: str, device:str, compute_type:str, beam_size: int) -> None:
        storage_path = source_path.replace("source", "")
        model = WhisperModel(model_size, device=device, compute_type=compute_type)
        segments, info = model.transcribe(storage_path + "audio.mp3", beam_size=beam_size, word_timestamps=True)
        for segment in segments:
            for word in segment.words:
                segment_subs = {
                "uuid": uuid,
                "words": [{
                    "word": word.word,
                    "start": word.start,
                    "end": word.end
                }]}
                self.answer.append(segment_subs)