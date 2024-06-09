import os
import shutil
import cv2
import torch
import glob
from gfpgan import GFPGANer
from basicsr.archs.rrdbnet_arch import RRDBNet
from realesrgan import RealESRGANer
import subprocess
import requests
import logging
from sys import stdout


logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
logFormatter = logging.Formatter("%(name)-12s %(asctime)s %(levelname)-8s %(filename)s:%(funcName)s %(message)s")
consoleHandler = logging.StreamHandler(stdout)
consoleHandler.setFormatter(logFormatter)
logger.addHandler(consoleHandler)



class VideoPostProcessor:
    def __init__(self):
        self.models_backup_dir = '/models_backup'

    def download_file(self, url, dest_path):
        if os.path.exists(dest_path):
            logger.info(f"File {dest_path} already exists. Skipping download.")
            return
        logger.info(f"Downloading {url} to {dest_path}...")
        response = requests.get(url, stream=True)
        if response.status_code == 200:
            with open(dest_path, 'wb') as f:
                shutil.copyfileobj(response.raw, f)
            logger.info(f"Downloaded {url} to {dest_path}.")
        else:
            logger.info(f"Failed to download {url}")


    def video_to_images(self, video_path, output_dir):
        logger.info('Video to images start')
        
        os.makedirs(output_dir, exist_ok=True)

        vidcap = cv2.VideoCapture(video_path)
        success, image = vidcap.read()
        count = 1
        while success:
            cv2.imwrite(os.path.join(output_dir, f"image_{count}.jpg"), image)
            success, image = vidcap.read()
            logger.info(f'Saved image {count}')
            count += 1

    def enhance_images(self, input_dir, output_dir, version='1.3', upscale=1, bg_upsampler='realesrgan'):
        logger.info('Enhance images start')

        os.makedirs(output_dir, exist_ok=True)

        img_list = sorted(glob.glob(os.path.join(input_dir, '*')))
        
        if bg_upsampler == 'realesrgan':
            if not torch.cuda.is_available():
                logger.info('RealESRGAN is slow on CPU. It is recommended to use GPU.')
                bg_upsampler = None
            else:
                model = RRDBNet(num_in_ch=3, num_out_ch=3, num_feat=64, num_block=23, num_grow_ch=32, scale=2)

                bg_model_path = os.path.join(self.models_backup_dir, 'RealESRGAN_x2plus.pth')
                self.download_file('https://github.com/xinntao/Real-ESRGAN/releases/download/v0.2.1/RealESRGAN_x2plus.pth', bg_model_path)

                bg_upsampler = RealESRGANer(
                    scale=2,
                    model_path=bg_model_path,
                    model=model,
                    tile=400,
                    tile_pad=10,
                    pre_pad=0,
                    half=True)
        else:
            bg_upsampler = None

        if version == '1':
            arch = 'original'
            channel_multiplier = 1
            model_name = 'GFPGANv1'
            url = 'https://github.com/TencentARC/GFPGAN/releases/download/v0.1.0/GFPGANv1.pth'
        elif version == '1.2':
            arch = 'clean'
            channel_multiplier = 2
            model_name = 'GFPGANCleanv1-NoCE-C2'
            url = 'https://github.com/TencentARC/GFPGAN/releases/download/v0.2.0/GFPGANCleanv1-NoCE-C2.pth'
        elif version == '1.3':
            arch = 'clean'
            channel_multiplier = 2
            model_name = 'GFPGANv1.3'
            url = 'https://github.com/TencentARC/GFPGAN/releases/download/v1.3.0/GFPGANv1.3.pth'
        elif version == '1.4':
            arch = 'clean'
            channel_multiplier = 2
            model_name = 'GFPGANv1.4'
            url = 'https://github.com/TencentARC/GFPGAN/releases/download/v1.3.0/GFPGANv1.4.pth'
        elif version == 'RestoreFormer':
            arch = 'RestoreFormer'
            channel_multiplier = 2
            model_name = 'RestoreFormer'
            url = 'https://github.com/TencentARC/GFPGAN/releases/download/v1.3.4/RestoreFormer.pth'
        else:
            raise ValueError(f'Wrong model version {version}.')

        model_path = os.path.join(self.models_backup_dir, url.split('/')[-1])
        self.download_file(url, model_path)

        restorer = GFPGANer(
            model_path=model_path,
            upscale=upscale,
            arch=arch,
            channel_multiplier=channel_multiplier,
            bg_upsampler=bg_upsampler
        )
        
        for img_path in img_list:
            img_name = os.path.basename(img_path)
            logger.info(f'Processing {img_name} ...')
            input_img = cv2.imread(img_path, cv2.IMREAD_COLOR)
            _, _, restored_img = restorer.enhance(input_img, paste_back=True)

            if restored_img is not None:
                save_path = os.path.join(output_dir, img_name)
                cv2.imwrite(save_path, restored_img)

    def images_to_video(self, image_folder, video_path, fps=30):

        logger.info('Images to video start')

        images = []

        for i in range(1, len(os.listdir(image_folder))+1):
            images.append( os.path.join(image_folder, 'image_'+str(i)+'.jpg') )

        frame = cv2.imread(images[0])
        height, width, layers = frame.shape
        video = cv2.VideoWriter(video_path, cv2.VideoWriter_fourcc(*'DIVX'), fps, (width, height))

        for image in images:
            video.write(cv2.imread(os.path.join(image_folder, image)))

        cv2.destroyAllWindows()
        video.release()

    def final_result(self, audio_path, video_path, outfile):

        logger.info('Final result start')

        command = f'ffmpeg -y -i {audio_path} -i {video_path} -strict -2 -q:v 1 {outfile}'
        subprocess.call(command, shell=True)

        logger.info('Final result finish')

    def process(self, video_path, audio_path, outfile, output_path):

        logger.info('Process start')

        # Step 1: Convert video to images
        whole_image_dir = output_path + '/whole_imgs'
        self.video_to_images(video_path, whole_image_dir)

        # Step 2: Enhance images
        enhanced_image_dir = output_path + '/restored_imgs'
        self.enhance_images(whole_image_dir, enhanced_image_dir)

        # Step 3: Combine images back into video with audio
        video_path_output = output_path + '/video.avi'
        self.images_to_video(enhanced_image_dir, video_path_output)

        self.final_result(audio_path, video_path_output, outfile)
        