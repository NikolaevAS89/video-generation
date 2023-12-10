import json
import time

import pika
from pika.exchange_type import ExchangeType

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
    time.sleep(10)
    # TODO delete example
    answer = {
        "uuid": bytes(body).decode("UTF-8"),
        "words": [{
            "word": "Свобода",
            "start": "00:00:01",
            "end": "00:00:03"
        }]
    }
    #
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
