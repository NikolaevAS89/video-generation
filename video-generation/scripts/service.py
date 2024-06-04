import logging
import os
import time
from sys import stdout
from Wav2Lip.video_processor import VideoProcessor
from GFPGAN.video_postprocessor import VideoPostProcessor
import shutil

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
        """
        :return: a root directory to store all data
        """
        return self._root_path_

    def get_sources_path(self, uuid: str):
        """
        :param uuid:
        :return: path to an original video
        """
        return f'{self._root_path_}/{uuid}/source'

    def get_original_audio_path(self, uuid: str):
        """
        :param uuid:
        :return: path to an original audio
        """
        return f'{self._root_path_}/{uuid}/audio.mp3'

    def get_generated_audio_path(self, uuid: str, task_uuid: str):
        """
        :param uuid: same as templateId
        :param task_uuid: same as processedId
        :return: path to an generated audio
        """
        return f'{self._root_path_}/{uuid}/{task_uuid}/audio_generated.mp3'

    def get_generated_video_path(self, uuid: str, task_uuid: str):
        """
        :param uuid: same as templateId
        :param task_uuid: same as processedId
        :return: path to an generated audio
        """
        return f'{self._root_path_}/{uuid}/{task_uuid}/video_generated.mp4'


class VideoService:
    def __init__(self, storage_path_service: StoragePathService):
        self._storage_path_service_ = storage_path_service
        self.temp_path = '/temp'
        # self.video_processor = VideoProcessor(checkpoint_path='./Wav2Lip/wav2lip.pth', temp_path=self.temp_path)
        # self.video_postprocessor = VideoPostProcessor(temp_path=self.temp_path)
        self.video_processor = VideoProcessor(checkpoint_path='./Wav2Lip/wav2lip.pth')
        self.video_postprocessor = VideoPostProcessor()

    def generate_video(
            self, 
            templateId: str, 
            processedId: str, 
            chosen: list[int], 
            mapping: dict[str, int],
            replacements: dict[str, str], 
            originalWords: list[dict]
        ) -> dict:

        temp_dir_path = self.temp_path + f'/{templateId}/{processedId}'

        if os.path.exists(temp_dir_path):
            shutil.rmtree(temp_dir_path)
        os.makedirs(temp_dir_path, exist_ok=True)



        original_video_path = self._storage_path_service_.get_sources_path(uuid=templateId)
        logger.info(original_video_path)
        generated_audio_path = self._storage_path_service_.get_generated_audio_path(uuid=templateId, task_uuid=processedId)
        logger.info(generated_audio_path)
        generated_video_path = self._storage_path_service_.get_generated_video_path(uuid=templateId, task_uuid=processedId)
        logger.info(generated_video_path)

        # # Ensure directories exist
        # os.makedirs(generated_video_path, exist_ok=True)

        # Define paths
        # result_video_path = os.path.join(generated_video_path, 'result_voice.mp4')

        # Call the video processing method

        logger.info('processing video')
        self.video_processor.process_video(
            face_path=original_video_path,
            audio_path=generated_audio_path,
            # outfile=result_video_path
            # outfile=generated_video_path,
            outfile=temp_dir_path + f'/video_generated_raw.mp4',
            output_path=temp_dir_path
        )

        self.video_postprocessor.process(
            video_path=temp_dir_path + f'/video_generated_raw.mp4',
            audio_path=generated_audio_path,
            outfile=generated_video_path,
            output_path=temp_dir_path
        )

        return {
            "processedId": processedId,
            "status": "Success",
            "message": "The video has been generated."
        }
    

# storage_path_service = StoragePathService('./storage')

# video_service = VideoService(storage_path_service)
# video_service.generate_video(
#     'test_uuid',
#     'test_process_uuid'
# )
