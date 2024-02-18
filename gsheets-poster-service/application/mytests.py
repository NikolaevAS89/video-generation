import requests
from datetime import datetime
from GSheetsUploaderService import GSheetsUploader
import json

SPREADSHEET_ID = '1ukZdHp8_Qsbksyyasl-8on6ujFSQ1B35Yf07mek6IqM'

HOST = 'https://callback'

gsheets_uploader_service = GSheetsUploader(spreadsheet_id=SPREADSHEET_ID)


def callback_mock(rabbit_message):

    if rabbit_message['action'] == 'upload':

        page_name = datetime.now().strftime('%Y-%m-%d')

        # response = requests.get(HOST+'/callback/data')

        # values_to_add = response.json()

        values_to_add = [{'phone':'89232223334','email':'myemail@email.com', 'FullName': 'Ivan Ivanov', 'uuid':'455a-24s4-b532', "words":'{"word1":"heroes","word2":"three"}'},{'phone':'89232223334','email':'myemail@email.com', 'FullName': 'Ivan Ivanov', 'uuid':'455a-24s4-b532666', "words":'{"word1":"heroes","word2":"three"}'}]

        """ values_to_add - массив данных типа [
                                                {'phone':'89232223334','email':'myemail@email.com', ''FullName': 'Ivan Ivanov', 'uuid':'455a-24s4-b532', "words":'{"word1":X,
                                                                                                                                    "word2":Y}'},
                                                {...},
                                                ]"""


        gsheets_uploader_service.update_google_sheet(values_to_add=values_to_add, page_name = page_name)


    elif rabbit_message['action'] == 'generate':

        approved_data = gsheets_uploader_service.request_approved_data_to_generation()

        json_data = {'values':approved_data}

        response = requests.post(f'https://server/generator/list/',json=json_data)

        response_json = response.json() # response_json = [{'id': 1, 'uuid':'455a-24s4-b532666','status':'in process'}]

        status_to_update = {}

        for entry in response_json:

            status_to_update[entry['uuid']] = [entry['status']]

        gsheets_uploader_service.update_video_statuses(status_to_update)
            

    elif rabbit_message['action'] == 'checkStatus':

        approved_data = gsheets_uploader_service.request_approved_data_to_generation()

        json_data = {'values':approved_data}

        response = requests.post(f'https://server/generator/list/status/',json=json_data)

        response_json = response.json() # response_json = [{'id': 1, 'uuid':'455a-24s4-b532666','status':'in process'}]
        
        response_json = [{'id': 1, 'uuid':'455a-24s4-b532666','status':'in progress', 'uuid_video':'111'},{'id': 1, 'uuid':'455a-24s4-b532665','status':'failed', 'uuid_video':'111'}]

        status_to_update = {}

        for entry in response_json:

            status_to_update[entry['uuid']] = [entry['status'], entry['uuid_video']]

        gsheets_uploader_service.update_video_statuses(status_to_update)

if __name__ == "__main__":
    callback_mock(rabbit_message={'action':'checkStatus'})