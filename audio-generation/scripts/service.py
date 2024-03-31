import logging
import os
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

    def get_generated_audio_path(self, uuid: str):
        """
        :param uuid:
        :return: path to an generated audio
        """
        return f'{self._root_path_}/{uuid}/audio_generated.mp3'


class AudioService:
    def __init__(self,
                 storage_path_service: StoragePathService):
        self._storage_path_service_ = storage_path_service

    def generate_audio(self,
                       uuid: str,
                       words: list[dict]) -> dict:
        """
        Generate new audio by a list of words
        :param uuid: uuid
        :param words: a list of words with timemarks in a following structure:
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
        original_audio_path = self._storage_path_service_.get_original_audio_path(uuid=uuid)
        generated_audio_path = self._storage_path_service_.get_generated_audio_path(uuid=uuid)
        os.popen(f'cp {original_audio_path} {generated_audio_path}')  # TODO make real generation
        return {
            "uuid": uuid,
            "status": "Successes",
            "message": "The audio has been generated."
        }
