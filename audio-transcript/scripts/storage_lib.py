import os
from moviepy.editor import VideoFileClip
from faster_whisper import WhisperModel

class StorageService:
    def __init__(self, path):
        self.__path = path
        self.__video_path = None
        self.__source = None
        self.answer = []
    def get_video_path(self, uuid):
        self.__video_path = self.__path + "/storage/" + uuid + "/"
        return self.__video_path
    def get_source(self):
        self.__source = rf'{self.__video_path + "source"}'
        return self.__source

class StorageManipulatorService:
    def __init__(self, storage, model_size, device, compute_type, beam_size):
        self.storage = storage
        self.answer = []
        self.__model_size = model_size 
        self.__device = device
        self.__compute_type = compute_type
        self.__beam_size = beam_size

    def split_source(self, uuid) -> None:
        video_path = self.storage.get_video_path(uuid)
        source_path = self.storage.get_source()

        video_clip = VideoFileClip(source_path)
        audio_clip = video_clip.audio

        audio_name_with_ext = video_path + "audio.mp3"
        audio_clip.write_audiofile(audio_name_with_ext)

        #audio_name_without_ext = video_path + "audio"
        #os.rename(audio_name_with_ext,audio_name_without_ext)

        video_name_with_ext = video_path + "video.mp4"
        video_clip.without_audio().write_videofile(video_name_with_ext)

        #video_name_without_ext = video_path + "video"
        #os.rename(video_name_with_ext,video_name_without_ext)
        


    def generate_subs(self, uuid: str) -> None:

        video_path = self.storage.get_video_path(uuid)

        model = WhisperModel(self.__model_size, device=self.__device, compute_type=self.__compute_type)
        segments, info = model.transcribe(video_path + "audio", beam_size=self.__beam_size, word_timestamps=True)

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