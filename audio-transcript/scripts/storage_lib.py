from moviepy.editor import VideoFileClip


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
    def __init__(self, storage, model, beam_size):
        self.storage = storage
        self.answer = []
        self.__beam_size = beam_size
        self.__model = model

    def split_source(self, uuid) -> None:
        video_path = self.storage.get_video_path(uuid)
        source_path = self.storage.get_source()

        video_clip = VideoFileClip(source_path)
        audio_clip = video_clip.audio

        audio_name_with_ext = video_path + "audio.mp3"
        audio_clip.write_audiofile(audio_name_with_ext)

        # audio_name_without_ext = video_path + "audio"
        # os.rename(audio_name_with_ext,audio_name_without_ext)

        video_name_with_ext = video_path + "video.mp4"
        video_clip.without_audio().write_videofile(video_name_with_ext)

        # video_name_without_ext = video_path + "video"
        # os.rename(video_name_with_ext,video_name_without_ext)

    def generate_subs(self, uuid: str) -> None:
        video_path = self.storage.get_video_path(uuid)
        return self.__model.generate_subs(uuid, video_path + "audio.mp3")
