import logging
from datetime import datetime
from sys import stdout

import os
from openpyxl import load_workbook
from datetime import datetime
from google.auth.transport.requests import Request
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build
from google.oauth2.credentials import Credentials
from google.oauth2 import service_account
from googleapiclient.http import MediaFileUpload, MediaIoBaseDownload, HttpError
import json

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
logFormatter = logging.Formatter("%(name)-12s %(asctime)s %(levelname)-8s %(filename)s:%(funcName)s %(message)s")
consoleHandler = logging.StreamHandler(stdout)
consoleHandler.setFormatter(logFormatter)
logger.addHandler(consoleHandler)


SHEET_CONTENT_HEADERS = ['id', 'phone', 'email', 'name', 'templateId', 'words', 'ManualMark','status_of_processing', 'uuid_video_demo']

class GSheetsUploader:

    def __init__(self, spreadsheet_id, creds):
        self.spreadsheet_id = spreadsheet_id
        self.creds = creds
        self.scopes = ['https://www.googleapis.com/auth/spreadsheets']

    def create_new_sheet(self, sheets, sheet_name) -> None:

        logger.debug(f"Creating new sheet {sheet_name}")
            
        add_new_sheet_request_body = {
            'requests':[{
                'addSheet':{
                    'properties':{
                        'title': f'{sheet_name}',
                        'tabColor':{
                            'red': 0.44,
                            'green': 0.99,
                            'blue': 0.50
                        }
                    }
                }
            }]
        }

        response = sheets.batchUpdate(
            spreadsheetId=self.spreadsheet_id,
            body=add_new_sheet_request_body
        ).execute()
        

        
    def get_all_sheet_content(self, sheets) -> list:

        logger.debug(f"Getting all sheet content")

        sheet_content = sheets.get(spreadsheetId=self.spreadsheet_id).execute()

        all_sheet_content = sheet_content.get('sheets')

        return all_sheet_content

    def get_last_sheet_title(self, all_sheet_content) -> str:

        logger.debug(f"Retrieving last sheet title")

        last_sheet_title = all_sheet_content[-1]['properties']['title']

        return last_sheet_title


    def transform_sheet_content_entries_into_dict(self, sheet_content):

        logger.debug(f"Transforming sheet entries to dictionaries")

        transformed_entry_list = []

        for entry in sheet_content:

            transformed_entry = {}

            for element, header in zip(entry, SHEET_CONTENT_HEADERS):

                transformed_entry[header] = element

            transformed_entry_list.append(transformed_entry)

        return transformed_entry_list


    def get_sheet_content(self, sheets, sheet_name) -> list:

        logger.debug(f"Gettong sheet content {sheet_name}")

        try:

            sheet_content = sheets.values().get(spreadsheetId=self.spreadsheet_id, range = sheet_name).execute()
            
            sheet_content = sheet_content['values']

            logger.debug(f"{sheet_name} has been retrieved")

        except HttpError as e:

            logger.debug(f"{sheet_name} hasn't been found")

            error_content = json.loads(e.content.decode('utf-8'))['error']

            if "Unable to parse range" in error_content['message']:

                self.create_new_sheet(sheets=sheets,sheet_name=sheet_name)

                sheet_content = [SHEET_CONTENT_HEADERS]

        except KeyError:

                sheet_content = [SHEET_CONTENT_HEADERS]

        return sheet_content


    def upload_new_content_to_sheet(self, sheets, content_to_upload, sheet_name):

        logger.debug(f"Sending new content to {self.spreadsheet_id}, {sheet_name}")

        sheets.values().update(spreadsheetId = self.spreadsheet_id, range=sheet_name,
                        valueInputOption='USER_ENTERED', body={"values" : content_to_upload}).execute()


    def update_google_sheet(self, values_to_add, sheet_name) -> None:

        logger.debug(f"Building new service")

        service = build('sheets', 'v4', credentials=self.creds)

        logger.debug(f"Creating sheets")

        sheets = service.spreadsheets()

        last_sheet_content = self.get_sheet_content(sheets=sheets, sheet_name=sheet_name)

        entries_now = len(last_sheet_content)

        entries_to_add = len(values_to_add)

        content_to_add = []

        logger.debug(f"Preparing new content based on values_to_add: {values_to_add}")

        ids = range(entries_now, entries_now + entries_to_add + 1)

        for entry, id in zip(values_to_add, ids):

            row = [str(id)] + list(entry.values())

            content_to_add.append(row)
        
        content_to_upload = last_sheet_content + content_to_add

        logger.debug(f"Content to upload: \n {content_to_upload}")

        self.upload_new_content_to_sheet(sheets, content_to_upload=content_to_upload, sheet_name=sheet_name)


    def update_video_statuses(self, statuses):

        logger.debug(f"Building new service")

        service = build('sheets', 'v4', credentials=self.creds)

        logger.debug(f"Creating sheets")

        sheets = service.spreadsheets()

        all_sheet_content = self.get_all_sheet_content(sheets=sheets)

        last_page_title = self.get_last_sheet_title(all_sheet_content=all_sheet_content)

        last_sheet_content = self.get_sheet_content(sheets=sheets, sheet_name=last_page_title)

        last_sheet_content_with_dicts = self.transform_sheet_content_entries_into_dict(sheet_content=last_sheet_content)

        last_sheet_content_columns = last_sheet_content[0]

        updated_values = []

        updated_values.append(last_sheet_content_columns)

        for entry in last_sheet_content_with_dicts[1:]:
        
            try:
                status = statuses.get(entry['id'])

                if len(entry) > 7:

                    entry = list(entry.values())[:7]

                for item in status:

                    entry['status_of_processing'] = item


            except:
                pass

            entry_value_list = list(entry.values())

            updated_values.append(entry_value_list)

        self.upload_new_content_to_sheet(sheets=sheets, content_to_upload=updated_values, sheet_name=last_page_title)


        self.upload_new_content_to_sheet(sheets=sheets, content_to_upload=updated_values, sheet_name=last_page_title)

    def request_approved_data_to_generation(self):

        service = build('sheets', 'v4', credentials=self.creds)

        sheets = service.spreadsheets()

        sheet_name = self.get_last_sheet_title(sheets=sheets)

        last_sheet_content = self.get_sheet_content(sheets=sheets, sheet_name=sheet_name)

        last_sheet_content_with_dicts = self.transform_sheet_content_entries_into_dict(sheet_content=last_sheet_content)

        approved_content = []

        for entry in last_sheet_content_with_dicts:

            if len(entry) > 6 and entry['ManualMark'].strip().upper() == 'TRUE':

                if len(entry) > 7:
                    
                    continue

                else:

                    approved_data = {'id':entry['id'], 'uuid':entry['templateId'], 'words':json.loads(entry['words'].replace("'",'"'))}

                    approved_content.append(approved_data)
        
        return approved_content


    def request_approved_data_to_status_check(self):


        logger.debug(f"Building new service")

        service = build('sheets', 'v4', credentials=self.creds)

        logger.debug(f"Creating sheets")

        sheets = service.spreadsheets()

        sheet_name = self.get_last_sheet_title(sheets=sheets)

        last_sheet_content = self.get_sheet_content(sheets=sheets, sheet_name=sheet_name)

        approved_content = []

        for entry in last_sheet_content:

            if len(entry) >= 7 and entry['ManualMark'].strip().upper() == 'TRUE':

                if len(entry) < 9 or entry['status_of_processing'].strip().lower() == 'done':

                    continue

                else:

                    approved_data = {'id':entry['id'], 'uuid':entry['uuid_video_demo']}

                approved_content.append(approved_data)

        
        logger.debug(f"Approved content {approved_content}")

        
        return approved_content

