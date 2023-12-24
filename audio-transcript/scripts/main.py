import json
import time
import os
import sys
import pika
from pika.exchange_type import ExchangeType
from moviepy.editor import VideoFileClip
from faster_whisper import WhisperModel

# RambbitMQ Settings
username = str(os.getenv("RABBITMQ_DEFAULT_USER"))
password = str(os.getenv("RABBITMQ_DEFAULT_PASS"))
rabbit_host = str(os.getenv("RABBITMQ_DEFAULT_HOST"))

queue_in_name = str(os.getenv("QUEUE_IN_NAME"))
queue_out_name = str(os.getenv("QUEUE_OUT_NAME"))
exchange_name = str(os.getenv("EXCHANGE_NAME"))
routing_key_in = str(os.getenv("ROUTUNG_KEY_IN"))
routing_key_out = str(os.getenv("ROUTING_KEY_OUT"))

# Faster-Whisper Settings
model_size = str(os.getenv("MODEL_SIZE"))
device = str(os.getenv("DEVICE"))
compute_type = str(os.getenv("COMPUTE_TYPE"))
beam_size = int(os.getenv("BEAM_SIZE"))

credentials = pika.credentials.PlainCredentials(username=username,
                                                password=password)

connection = pika.BlockingConnection(pika.ConnectionParameters(host=rabbit_host,
                                                               credentials=credentials))


class StorageService:
    def __init__(self, path):
        self.__path = path
        self.answer = []
    def get_video_path(self, uuid):
        self.__video_path = self.__path + "/storage/" + uuid + "/"
    def get_source(self):
        self.__source = self.__video_path + "source"
    def split_source(self):
        video_clip = VideoFileClip(rf"{self.__source}")
        audio_clip = video_clip.audio
        audio_clip.write_audiofile(self.__video_path + "audio.mp3")
        video_clip.without_audio().write_videofile(self.__video_path +"video.mp4")
    def generate_subs(self, uuid):
        model = WhisperModel(model_size, device=device, compute_type=compute_type)
        segments, info = model.transcribe(self.__video_path + "audio.mp3", beam_size=beam_size, word_timestamps=True)
        for segment in segments:
            for word in segment.words:
                segment_subs = {
                "uuid": bytes(uuid).decode("UTF-8"),
                "words": [{
                    "word": word.word,
                    "start": word.start,
                    "end": word.end
                }]}
                self.answer.append(segment_subs)

def callback(ch, method, properties, body):
    print(f" [x] Received   {str(body)}")
    print(f" [x] Method     {str(method)}")
    print(f" [x] Properties {str(properties)}")

    current_directory = os.path.dirname(os.path.realpath(__file__))
    parent_directory = os.path.dirname(current_directory)
    grandparent_directory = os.path.dirname(parent_directory)
    if grandparent_directory not in sys.path:
        sys.path.append(grandparent_directory)
    uuid = body.decode("utf-8")

    print(f" [x] Received   {str(body)}")
    print(f" [x] Method     {str(method)}")
    print(f" [x] Properties {str(properties)}")

    # TODO place code here.
    storage = StorageService(path=grandparent_directory)
    storage.get_video_path(uuid=uuid)
    storage.get_source()
    storage.split_source()
    storage.generate_subs(uuid=uuid)

    ch.basic_publish(exchange=exchange_name,
                     routing_key=routing_key_out,
                     body=json.dumps(storage.answer).encode("UTF-8"))
    print(storage.answer)

if __name__ == '__main__':
    channel = connection.channel()
    channel.exchange_declare(exchange=exchange_name,
                             durable=True,
                             auto_delete=False,
                             exchange_type=ExchangeType.direct)
    channel.queue_declare(queue=queue_in_name,
                          durable=True)
    channel.queue_bind(exchange=exchange_name,
                       queue=queue_in_name,
                       routing_key=routing_key_in)
    channel.queue_declare(queue=queue_out_name,
                          durable=True)
    channel.queue_bind(exchange=exchange_name,
                       queue=queue_out_name,
                       routing_key=routing_key_out)
    channel.basic_consume(queue=queue_in_name,
                          auto_ack=True,
                          on_message_callback=callback)
    print(' [*] Waiting for messages. To exit press CTRL+C')
    channel.start_consuming()
