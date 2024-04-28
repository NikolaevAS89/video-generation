import logging
import os
import time
from sys import stdout

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

    def get_generated_directory(self, uuid: str, task_uuid: str):
        """
        :param uuid: same as templateId
        :param task_uuid: same as processedId
        :return: path to an generated audio
        """
        return f'{self._root_path_}/{uuid}/{task_uuid}'

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
        return f'{self._root_path_}/{uuid}/{task_uuid}/video_generated'


class AudioService:
    def __init__(self,
                 storage_path_service: StoragePathService):
        self._storage_path_service_ = storage_path_service

    def generate_audio(self,
                       templateId: str,
                       processedId: str,
                       chosen: list[int],
                       mapping: dict[str, int],
                       replacements: dict[str, str],
                       originalWords: list[dict]) -> dict:
        """
        Generate new audio by a list of words
        :param templateId: uuid
        :param processedId: uuid
        :param chosen: an array with same length as originalWords and contain an indexes groups of original words
        For example [0,1,1,0,... , 0, 5] mean that 2d and 3t original words replace together as 1st group
        :param mapping: groups names with their indexes
        :param replacements: mapping target values to groups
        :param originalWords: a list of original words with timemarks in a following structure:
        [
            {
                "word": str,
                "start": str,
                "end": str,
            },
            ...
        ]
        an example:
        [
            {
                "word": " Hallow,",
                "start": "0.0",
                "end": "0.88"
            },
            ....
        ]
        :return: result of generation
        """
        original_audio_path = self._storage_path_service_.get_original_audio_path(uuid=templateId)
        generated_audio_path = self._storage_path_service_.get_generated_audio_path(uuid=templateId,
                                                                                    task_uuid=processedId)
        generated_root_dir = self._storage_path_service_.get_generated_directory(uuid=templateId,
                                                                                 task_uuid=processedId)
        os.popen(f'mkdir {generated_root_dir} & cp {original_audio_path} {generated_audio_path}')  # TODO make real generation
        time.sleep(30)  # TODO delete after tests
        return {
            "processedId": processedId,
            "status": "Successes",
            "message": "The audio has been generated."
        }
