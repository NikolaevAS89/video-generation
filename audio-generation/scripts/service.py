import logging
import os
import time
from sys import stdout
import torchaudio
import torch
from TTS.tts.configs.xtts_config import XttsConfig
from TTS.tts.models.xtts import Xtts


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
        return f'{self._root_path_}/{uuid}/{task_uuid}/video_generated.mp4'


class AudioService:
    def __init__(self,
                 storage_path_service: StoragePathService):
        self._storage_path_service_ = storage_path_service
        # Initialize the TTS model
        config = XttsConfig()
        config.load_json("/application/tts/config_xtts2.json")
        self.model = Xtts.init_from_config(config)
        self.model.load_checkpoint(config, checkpoint_dir="/application/tts/tts_models--multilingual--multi-dataset--xtts_v2", eval=True)
        self.model.cuda()

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
        # os.popen(f'mkdir {generated_root_dir} & cp {original_audio_path} {generated_audio_path}')  # TODO make real generation

        os.makedirs(generated_root_dir, exist_ok=True)

        text_to_generate = ""
        i = 0
        while i < len(chosen):
            if chosen[i] == 0:
                text_to_generate += originalWords[i]['word']
                i += 1
            else:
                group_num = chosen[i]
                group_name = next(key for key, value in mapping.items() if value == group_num)
                replacement_text = replacements[group_name]
                text_to_generate += replacement_text
                
                # Skip over all words in the current group
                while i < len(chosen) and chosen[i] == group_num:
                    i += 1
        
        # Generate conditioning latents
        gpt_cond_latent, speaker_embedding = self.model.get_conditioning_latents(audio_path=[original_audio_path])

        # Generate the audio using the TTS model
        out = self.model.inference(
            text_to_generate,
            "ru",
            gpt_cond_latent,
            speaker_embedding
        )

        # Save the generated audio to the specified path
        torchaudio.save(generated_audio_path, torch.tensor(out["wav"]).unsqueeze(0), 24000)

        return {
            "processedId": processedId,
            "status": "Successes",
            "message": "The audio has been generated."
        }
