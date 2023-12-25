import json
import time
import os
import sys
import pika
from pika.exchange_type import ExchangeType
from storage_lib import StorageService, StorageManipulatorService

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


# Directories
current_directory = os.path.dirname(os.path.realpath(__file__))
parent_directory = os.path.dirname(current_directory)
grandparent_directory = os.path.dirname(parent_directory)
if grandparent_directory not in sys.path:
    sys.path.append(grandparent_directory)


credentials = pika.credentials.PlainCredentials(username=username,
                                                password=password)

connection = pika.BlockingConnection(pika.ConnectionParameters(host="rabbitmq",
                                                               credentials=credentials))

storage = StorageService(path=grandparent_directory)
manipulator = StorageManipulatorService()

def callback(ch, method, properties, body):

    uuid = body.decode("utf-8")

    storage.get_video_path(uuid=uuid)
    source = storage.get_source()
    manipulator.split_source(source)
    manipulator.generate_subs(source_path = source, uuid=uuid, model_size=model_size, device=device, compute_type=compute_type, beam_size=beam_size)

    ch.basic_publish(exchange=exchange_name,
                     routing_key=routing_key_out,
                     body=json.dumps(manipulator.answer).encode("UTF-8"))
    
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
