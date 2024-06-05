import os
import numpy as np
import cv2
import subprocess
import torch
from .face_detection import FaceAlignment, LandmarksType
from tqdm import tqdm
from .models.wav2lip import Wav2Lip
from .audio import load_wav, melspectrogram
import platform
import logging
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


class VideoProcessor:
    def __init__(
            self, 
            checkpoint_path, 
            device='cuda' if torch.cuda.is_available() else 'cpu'):
        
        self.checkpoint_path = checkpoint_path
        self.device = device
        self.img_size = 96
        self.mel_step_size = 16
        self.model = self.load_model(checkpoint_path)

        logger.info(f'Using {self.device} for inference.')

    def load_model(self, path):
        model = Wav2Lip()
        logger.info(f"Load checkpoint from: {path}")
        checkpoint = self._load(path)
        s = checkpoint["state_dict"]
        new_s = {k.replace('module.', ''): v for k, v in s.items()}
        model.load_state_dict(new_s)
        model = model.to(self.device)
        return model.eval()

    def _load(self, checkpoint_path):
        if self.device == 'cuda':
            checkpoint = torch.load(checkpoint_path)
        else:
            checkpoint = torch.load(checkpoint_path, map_location=lambda storage, loc: storage)
        return checkpoint

    def get_smoothened_boxes(self, boxes, T):
        for i in range(len(boxes)):
            window = boxes[i:i + T] if i + T <= len(boxes) else boxes[len(boxes) - T:]
            boxes[i] = np.mean(window, axis=0)
        return boxes

    def face_detect(self, images, face_det_batch_size, pads, nosmooth, output_path):
        detector = FaceAlignment(LandmarksType._2D, flip_input=False, device=self.device)
        batch_size = face_det_batch_size
        results = []

        while True:
            predictions = []
            try:
                for i in tqdm(range(0, len(images), batch_size)):
                    predictions.extend(detector.get_detections_for_batch(np.array(images[i:i + batch_size])))
            except RuntimeError:
                if batch_size == 1:
                    raise RuntimeError('Image too big to run face detection on GPU. Please use a resize factor.')
                batch_size //= 2
                continue
            break

        pady1, pady2, padx1, padx2 = pads
        for rect, image in zip(predictions, images):
            if rect is None:
                cv2.imwrite(output_path + '/faulty_frame.jpg', image)
                raise ValueError('Face not detected! Ensure the video contains a face in all frames.')

            y1 = max(0, rect[1] - pady1)
            y2 = min(image.shape[0], rect[3] + pady2)
            x1 = max(0, rect[0] - padx1)
            x2 = min(image.shape[1], rect[2] + padx2)

            results.append([x1, y1, x2, y2])

        boxes = np.array(results)
        if not nosmooth:
            boxes = self.get_smoothened_boxes(boxes, T=5)
        results = [[image[y1:y2, x1:x2], (y1, y2, x1, x2)] for image, (x1, y1, x2, y2) in zip(images, boxes)]
        del detector
        return results

    def datagen(self, frames, mels, face_det_batch_size, pads, nosmooth, wav2lip_batch_size, box, static, output_path):
        img_batch, mel_batch, frame_batch, coords_batch = [], [], [], []
        
        if box[0] == -1:
            if not static:
                face_det_results = self.face_detect(frames, face_det_batch_size, pads, nosmooth, output_path) # BGR2RGB for CNN face detection
            else:
                face_det_results = self.face_detect([frames[0]], face_det_batch_size, pads, nosmooth, output_path)
        else:
            logger.info('Using the specified bounding box instead of face detection...')
            y1, y2, x1, x2 = box
            face_det_results = [[f[y1: y2, x1:x2], (y1, y2, x1, x2)] for f in frames]

        for i, m in enumerate(mels):
            idx = 0 if static else i%len(frames)
            frame_to_save = frames[idx].copy()
            face, coords = face_det_results[idx].copy()

            face = cv2.resize(face, (self.img_size, self.img_size))
                
            img_batch.append(face)
            mel_batch.append(m)
            frame_batch.append(frame_to_save)
            coords_batch.append(coords)

            if len(img_batch) >= wav2lip_batch_size:
                img_batch, mel_batch = np.asarray(img_batch), np.asarray(mel_batch)

                img_masked = img_batch.copy()
                img_masked[:, self.img_size//2:] = 0

                img_batch = np.concatenate((img_masked, img_batch), axis=3) / 255.
                mel_batch = np.reshape(mel_batch, [len(mel_batch), mel_batch.shape[1], mel_batch.shape[2], 1])

                yield img_batch, mel_batch, frame_batch, coords_batch
                img_batch, mel_batch, frame_batch, coords_batch = [], [], [], []

        if len(img_batch) > 0:
            img_batch, mel_batch = np.asarray(img_batch), np.asarray(mel_batch)

            img_masked = img_batch.copy()
            img_masked[:, self.img_size//2:] = 0

            img_batch = np.concatenate((img_masked, img_batch), axis=3) / 255.
            mel_batch = np.reshape(mel_batch, [len(mel_batch), mel_batch.shape[1], mel_batch.shape[2], 1])

            yield img_batch, mel_batch, frame_batch, coords_batch

    def process_video(self, face_path, audio_path, outfile, output_path, face_det_batch_size=6, wav2lip_batch_size=128,
                      static=False, fps=25., pads=[0, 10, 0, 0], resize_factor=1, crop=[0, -1, 0, -1], 
                      box=[-1, -1, -1, -1], rotate=False, nosmooth=False):
        fps, full_frames = self.prepare_data(face_path, resize_factor, crop, rotate, fps)

        if not audio_path.endswith('.wav'):
            logger.info('Extracting raw audio...')
            temp_audio_file = f'{output_path}/temp.wav'
            command = 'ffmpeg -y -i {} -strict -2 {}'.format(audio_path,  temp_audio_file)

            subprocess.call(command, shell=True)
        else:
            temp_audio_file = audio_path

        wav = load_wav(temp_audio_file, 16000)
        mel = melspectrogram(wav)

        if np.isnan(mel.reshape(-1)).sum() > 0:
            raise ValueError('Mel contains nan! Using a TTS voice? Add a small epsilon noise to the wav file and try again')

        mel_chunks = self.create_mel_chunks(mel, fps)
        full_frames = full_frames[:len(mel_chunks)]
        gen = self.datagen(full_frames.copy(), mel_chunks, face_det_batch_size, pads, nosmooth, wav2lip_batch_size, box, static, output_path)
    
        temp_video_file = f'{output_path}/result.avi'

        self.inference(gen, full_frames, fps, mel_chunks, wav2lip_batch_size, temp_video_file)
        self.save(outfile, temp_audio_file, temp_video_file)


    def prepare_data(self, face_path, resize_factor, crop, rotate, fps):
        logger.info('Prepare data')
        if not os.path.isfile(face_path):
            raise ValueError('--face argument must be a valid path to video/image file')

        elif face_path.split('.')[-1] in ['jpg', 'png', 'jpeg']:
            full_frames = [cv2.imread(face_path)]

        else:
            video_stream = cv2.VideoCapture(face_path)
            fps = video_stream.get(cv2.CAP_PROP_FPS)
            logger.info('Reading video frames...')
            full_frames = []
            while 1:
                still_reading, frame = video_stream.read()
                if not still_reading:
                    video_stream.release()
                    break

                if resize_factor > 1:
                    frame = cv2.resize(frame, (frame.shape[1]//resize_factor, frame.shape[0]//resize_factor))
                    
                if rotate:
                    frame = cv2.rotate(frame, cv2.cv2.ROTATE_90_CLOCKWISE)

                y1, y2, x1, x2 = crop
                if x2 == -1: x2 = frame.shape[1]
                if y2 == -1: y2 = frame.shape[0]

                frame = frame[y1:y2, x1:x2]
                full_frames.append(frame)
        
        return fps, full_frames

    def create_mel_chunks(self, mel, fps):
        mel_chunks = []
        mel_idx_multiplier = 80. / fps
        i = 0
        while True:
            start_idx = int(i * mel_idx_multiplier)
            if start_idx + self.mel_step_size > len(mel[0]):
                mel_chunks.append(mel[:, len(mel[0]) - self.mel_step_size:])
                break
            mel_chunks.append(mel[:, start_idx: start_idx + self.mel_step_size])
            i += 1
        return mel_chunks

    def inference(self, gen, full_frames, fps, mel_chunks, wav2lip_batch_size, output_path):

        frame_h, frame_w = full_frames[0].shape[:-1]
        out = cv2.VideoWriter(output_path, cv2.VideoWriter_fourcc(*'DIVX'), fps, (frame_w, frame_h))
        
        for _, (img_batch, mel_batch, frames, coords) in enumerate(tqdm(gen,total=int(np.ceil(float(len(mel_chunks))/ wav2lip_batch_size)))):
            
            img_batch = torch.FloatTensor(np.transpose(img_batch, (0, 3, 1, 2))).to(self.device)
            mel_batch = torch.FloatTensor(np.transpose(mel_batch, (0, 3, 1, 2))).to(self.device)

            with torch.no_grad():
                pred = self.model(mel_batch, img_batch)

            pred = pred.cpu().numpy().transpose(0, 2, 3, 1) * 255.
            
            for p, f, c in zip(pred, frames, coords):
                y1, y2, x1, x2 = c
                p = cv2.resize(p.astype(np.uint8), (x2 - x1, y2 - y1))

                f[y1:y2, x1:x2] = p
                out.write(f)

        out.release()

    
    def save(self, outfile, audio_file, video_file):

        command = 'ffmpeg -y -i {} -i {} -strict -2 -q:v 1 {}'.format(audio_file, video_file, outfile)
        subprocess.call(command, shell=platform.system() != 'Windows')
        
        logger.info(outfile)