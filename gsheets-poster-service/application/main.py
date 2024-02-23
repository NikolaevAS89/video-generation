import json
import time
import os
import sys
import pika
from pika.exceptions import AMQPConnectionError
from pika.exchange_type import ExchangeType
from GSheetsUploaderService import GSheetsUploader
import requests
from datetime import datetime
from requests.auth import HTTPBasicAuth

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

HOST = 'http://callback:8080/'

SERVER_HOST = 'http://server:8080/'


credentials = pika.credentials.PlainCredentials(username=username,
                                                password=password)

gsheets_uploader_service = GSheetsUploader(spreadsheet_id = SPREADSHEET_ID, service_account_file=service_account_path)

def make_connection(host, cred):
    for count in range(0,10):
        try:
            print(f'Try to connect to RabbitMQ {count}')
            connection = pika.BlockingConnection(pika.ConnectionParameters(host=host,
                                                                           credentials=cred))
            return connection
        except AMQPConnectionError as e:
            if count>9:
                raise e
            else:
                time.sleep(15)

connection = make_connection(rabbit_host, credentials)

def callback(ch, method, properties, body):


    rabbit_message = json.loads(body.decode("utf-8"))

    if rabbit_message['action'] == 'upload':

        page_name = datetime.now().strftime('%Y-%m-%d')

        response = requests.get(HOST + 'callback/data')

        values_to_add = response.json() 

        """ values_to_add - массив данных типа [
                                                {'phone':'89232223334','email':'myemail@email.com', ''FullName': 'Ivan Ivanov', 'uuid':'455a-24s4-b532', "words":'{"word1":X,
                                                                                                                                    "word2":Y}'},
                                                {...},
                                                ]"""


        gsheets_uploader_service.update_google_sheet(values_to_add=values_to_add, page_name = page_name)


    elif rabbit_message['action'] == 'generate':

        approved_data = gsheets_uploader_service.request_approved_data_to_generation()


        response = requests.post(SERVER_HOST + 'generator/list',json=approved_data)

        response_json = response.json() # response_json = [{'id': '1', 'uuid':'455a-24s4-b532666','status':'in process'}]

        status_to_update = {}

        for entry in response_json:

            status_to_update[entry['id']] = [entry['status'], entry['uuid']

        gsheets_uploader_service.update_video_statuses(status_to_update)
            

    elif rabbit_message['action'] == 'checkStatus':

        approved_data = gsheets_uploader_service.request_approved_data_to_generation()


        response = requests.post(SERVER_HOST + 'generator/list/status',json=approved_data)

        response_json = response.json() # response_json = [{'id': '1', 'uuid':'455a-24s4-b532666','status':'in process'}]
        
        status_to_update = {}

        for entry in response_json:

            status_to_update[entry['id']] = [entry['status'], entry['uuid']

        gsheets_uploader_service.update_video_statuses(status_to_update)

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
    print(' [*] Waiting for messages. To exit press CTRL+C')

    channel.start_consuming()
