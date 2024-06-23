import functools
import json
import logging
import os
import time
from sys import stdout
from threading import Thread

import pika
from pika.exchange_type import ExchangeType

from service import AudioService, StoragePathService

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
logFormatter = logging.Formatter("%(name)-12s %(asctime)s %(levelname)-8s %(filename)s:%(funcName)s %(message)s")
consoleHandler = logging.StreamHandler(stdout)
consoleHandler.setFormatter(logFormatter)
logger.addHandler(consoleHandler)

# RambbitMQ Settings
username = str(os.getenv("RABBITMQ_DEFAULT_USER"))
password = str(os.getenv("RABBITMQ_DEFAULT_PASS"))
rabbit_host = str(os.getenv("RABBITMQ_DEFAULT_HOST"))

queue_in_name = str(os.getenv("QUEUE_IN_NAME"))
queue_out_name = str(os.getenv("QUEUE_OUT_NAME"))
exchange_name = str(os.getenv("EXCHANGE_NAME"))
routing_key_in = str(os.getenv("ROUTING_KEY_IN"))
routing_key_out = str(os.getenv("ROUTING_KEY_OUT"))

# Directories
root_storage_path = str(os.getenv("STORAGE_PATH"))

# Init
storage_path_service = StoragePathService(root_path=root_storage_path)
audio_service = AudioService(storage_path_service=storage_path_service)
credentials = pika.credentials.PlainCredentials(username=username,
                                                password=password)


def run_ml(request: dict, answers: list):
    try:
        answer = audio_service.generate_audio(**request)
    except Exception as e:
        logger.error(e)
        answer = {
            "processedId": request.get('processedId', "Undefined"),
            "status": "Error",
            "message": "An error while an audio generating."
        }
    answers.append(answer)


def callback(ch, method, properties, body, threads: list, answers: list):
    try:
        source = body.decode("utf-8")
        logger.info(f"Message:{str(source)}")
        request = json.loads(source)
        # run ml
        thread = Thread(target=run_ml, args=(request, answers))
        thread.start()
        threads.append(thread)
        # send status
        answer = {
            "processedId": request.get('processedId', "Undefined"),
            "status": "Audio is generating",
            "message": ""
        }
        ch.basic_publish(exchange=exchange_name,
                         routing_key=routing_key_out,
                         body=json.dumps(answer).encode("UTF-8"))
        if ch.is_open:
            ch.basic_ack()
            ch.stop_consuming()
    except Exception as e:
        logger.error(e)


def consume(threads: list, answers: list):
    channel = None
    connection = None
    while len(threads) <= 0:
        try:
            if connection is None or connection.is_closed:
                logger.info('Make new connection')
                connection = pika.BlockingConnection(pika.ConnectionParameters(host=rabbit_host,
                                                                               credentials=credentials))
            elif channel is None or channel.is_closed:
                logger.info('Make new channel')
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
                kvargs = {
                    "threads": threads,
                    "answers": answers
                }
                on_message_callback = functools.partial(callback, **kvargs)
                channel.basic_consume(queue=queue_in_name,
                                      on_message_callback=on_message_callback)
            else:
                logger.info('Waiting for messages. To exit press CTRL+C')
                channel.start_consuming()
        except Exception as e:
            logger.error(f"{str(e)}")
    connection.close()


def answer(answers: list):
    channel = None
    connection = None
    while len(answers) > 0:
        try:
            if connection is None or connection.is_closed:
                logger.info('Make new connection')
                connection = pika.BlockingConnection(pika.ConnectionParameters(host=rabbit_host,
                                                                               credentials=credentials))
            elif channel is None or channel.is_closed:
                logger.info('Make new channel')
                channel = connection.channel()
                channel.exchange_declare(exchange=exchange_name,
                                         durable=True,
                                         auto_delete=False,
                                         exchange_type=ExchangeType.direct)
                channel.queue_declare(queue=queue_out_name,
                                      durable=True)
                channel.queue_bind(exchange=exchange_name,
                                   queue=queue_out_name,
                                   routing_key=routing_key_out)
            else:
                answer = answers.pop(0)
                channel.basic_publish(exchange=exchange_name,
                                      routing_key=routing_key_out,
                                      body=json.dumps(answer).encode("UTF-8"))
        except Exception as e:
            logger.error(f"{str(e)}")
    connection.close()


if __name__ == '__main__':
    threads = list()
    answers = list()
    while True:
        try:
            consume(threads=threads, answers=answers)
            for thread in threads:
                thread.join()
            threads = list()
            answer(answers=answers)
        except Exception as e:
            logger.error(f"{str(e)}")
            time.sleep(15)  # TODO need to be reviewed
