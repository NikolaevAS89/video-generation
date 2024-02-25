import json
import logging
import os
import time
from sys import stdout

import pika
from pika.exceptions import AMQPConnectionError
from pika.exchange_type import ExchangeType

from service import MessageConsumeService

# RambbitMQ Settings
username = str(os.getenv("RABBITMQ_DEFAULT_USER"))
password = str(os.getenv("RABBITMQ_DEFAULT_PASS"))
rabbit_host = str(os.getenv("RABBITMQ_DEFAULT_HOST"))

queue_name = str(os.getenv("UPLOADER_QUEUE"))
exchange_name = str(os.getenv("UPLOADER_EXCHANGE"))
routing_key = str(os.getenv("UPLOADER_ROUTING_KEY"))

# GOOGLE SERVICE ACCOUNT

service_account_path = str(os.getenv("GOOGLE_SERVICE_ACCOUNT"))

SPREADSHEET_ID = str(os.getenv("SPREADSHEET_ID"))

# Логи
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
logFormatter = logging.Formatter("%(name)-12s %(asctime)s %(levelname)-8s %(filename)s:%(funcName)s %(message)s")
consoleHandler = logging.StreamHandler(stdout)
consoleHandler.setFormatter(logFormatter)
logger.addHandler(consoleHandler)
credentials = pika.credentials.PlainCredentials(username=username,
                                                password=password)


def make_connection(host, cred):
    for count in range(0, 10):
        try:
            logger.info(f'Try to connect to RabbitMQ {count}')
            connection = pika.BlockingConnection(pika.ConnectionParameters(host=host,
                                                                           credentials=cred))
            return connection
        except AMQPConnectionError as e:
            if count > 9:
                raise e
            else:
                time.sleep(15)


connection = make_connection(rabbit_host, credentials)


def callback(ch, method, properties, body):
    try:
        service = MessageConsumeService(spreadsheet_id=SPREADSHEET_ID,
                                        service_account_file=service_account_path)
        rabbit_message = json.loads(body.decode("utf-8"))

        logger.info(f"New message was read: {str(rabbit_message)}")
        if str(rabbit_message['action']) == 'upload':
            service.upload()
        elif str(rabbit_message['action']) == 'generate':
            service.generate()
        elif str(rabbit_message['action']) == 'checkStatus':
            service.update_status()
        else:
            logger.info(f"Command is not supposrted: {str(rabbit_message['action'])}")
    except Exception as e:
        logger.error(e, exc_info=True)


if __name__ == '__main__':
    channel = connection.channel()
    channel.exchange_declare(exchange=exchange_name,
                             durable=True,
                             auto_delete=False,
                             exchange_type=ExchangeType.direct)
    channel.queue_declare(queue=queue_name,
                          durable=True)
    channel.queue_bind(exchange=exchange_name,
                       queue=queue_name,
                       routing_key=routing_key)
    channel.basic_consume(queue=queue_name,
                          auto_ack=True,
                          on_message_callback=callback)
    logger.info(' [+] Waiting for messages. To exit press CTRL+C')
    while True:
        try:
            if channel.is_closed:
                if connection.is_closed:
                    connection = make_connection(rabbit_host, credentials)
                channel = connection.channel()
            channel.start_consuming()
        except Exception as e:
            logger.error(f"{str(e)}")
            time.sleep(5)
