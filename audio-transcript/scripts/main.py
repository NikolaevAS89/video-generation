import json
import time
import os
import sys
import pika
from pika.exchange_type import ExchangeType
from moviepy import VideoFileClip
from faster_whisper import WhisperModel

credentials = pika.credentials.PlainCredentials(username='videodetector',
                                                password='123456')

connection = pika.BlockingConnection(pika.ConnectionParameters(host="rabbitmq",
                                                               credentials=credentials))

queue_in_name = "audio.transcription.queue.in"
queue_out_name = "audio.transcription.queue.out"
exchange_name = "audio.transcription.exchange"
routing_key_in = "audio.transcript.in"
routing_key_out = "audio.transcript.out"


def callback(ch, method, properties, body):
    print(f" [x] Received   {str(body)}")
    print(f" [x] Method     {str(method)}")
    print(f" [x] Properties {str(properties)}")

    # TODO place code here.
    current_directory = os.path.dirname(os.path.realpath(__file__))
    parent_directory = os.path.dirname(current_directory)
    grandparent_directory = os.path.dirname(parent_directory)
    if grandparent_directory not in sys.path:
        sys.path.append(grandparent_directory)

    print(f" [x] Received   {str(body)}")
    print(f" [x] Method     {str(method)}")
    print(f" [x] Properties {str(properties)}")

    # TODO place code here.
    video = body.name
    path_to_vid_dir = current_directory+"/storage/"+str(body.id)
    path_to_original_vid = current_directory+"/storage/"+str(body.id)+"/"+video
    print(path_to_original_vid)
    print(video)
    video_clip = VideoFileClip(rf"{path_to_original_vid}")
    audio_clip = video_clip.audio
    audio_clip.write_audiofile(path_to_vid_dir+ "/"+ video+'_audio.mp3')
    video_clip.without_audio().write_videofile(path_to_vid_dir+ "/"+video+'_mute.mp4')
    model_size = "medium"
    model = WhisperModel(model_size, device="cuda", compute_type="float32")
    segments, info = model.transcribe(path_to_vid_dir+ "/"+ video+'_audio.mp3', beam_size=5, word_timestamps=True)
    answer = []
    
    for segment in segments:
        for word in segment.words:
            segment_subs = {
            "uuid": bytes(body.id).decode("UTF-8"),
            "words": [{
                "word": word.word,
                "start": word.start,
                "end": word.end
            }]}
            answer.append(segment_subs)
    
    ch.basic_publish(exchange=exchange_name,
                     routing_key=routing_key_out,
                     body=json.dumps(answer).encode("UTF-8"))


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
