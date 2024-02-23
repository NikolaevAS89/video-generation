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


class GSheetsUploader:

    def __init__(self, spreadsheet_id, service_account_file):
        self.spreadsheet_id = spreadsheet_id
        self.service_account_file = service_account_file
        self.scopes = ['https://www.googleapis.com/auth/spreadsheets']
        self.creds = self.authenticate_service_account()
    


    def authenticate(self):
        creds = None
        if os.path.exists('token.json'):
            creds = Credentials.from_authorized_user_file('token.json', self.scopes)
        if not creds or not creds.valid:
            if creds and creds.expired and creds.refresh_token:
                creds.refresh(Request())
            else:
                flow = InstalledAppFlow.from_client_secrets_file(
                    'credentials.json', self.scopes)
                creds = flow.run_local_server(port=0)
            with open('token.json', 'w') as token:
                token.write(creds.to_json())
        return creds
    

    def authenticate_service_account(self):
        creds = service_account.Credentials.from_service_account_file(self.service_account_file, scopes=self.scopes)
        return creds

                

    def create_new_page(self, sheets, page_name) -> None:
            
        add_new_sheet_request_body = {
            'requests':[{
                'addSheet':{
                    'properties':{
                        'title': f'{page_name}',
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
                
    def get_last_sheet_title(self, sheets) -> str:

        sheet_content = sheets.get(spreadsheetId=self.spreadsheet_id).execute()

        last_sheet_name = sheet_content.get('sheets')[-1]['properties']['title']

        return last_sheet_name

    def get_sheet_content(self, sheets, page_name) -> list:

        try:    
            sheet_content = sheets.values().get(spreadsheetId=self.spreadsheet_id, range = page_name).execute()
            
            sheet_content = sheet_content['values']

        except HttpError as e:
            error_content = json.loads(e.content.decode('utf-8'))['error']

            if "Unable to parse range" in error_content['message']:

                self.create_new_page(sheets=sheets,page_name=page_name)

                sheet_content = [['id', 'phone', 'email', 'name', 'templateId', 'words', 'ManualMark','status_of_processing', 'uuid_video_demo']]

        except KeyError:

                sheet_content = [['id', 'phone', 'email', 'name', 'templateId', 'words', 'ManualMark', 'status_of_processing', 'uuid_video_demo']]

        return sheet_content


    def upload_new_content_to_sheet(self, sheets, content_to_upload, page_name):

        sheets.values().update(spreadsheetId = self.spreadsheet_id, range=page_name,
                        valueInputOption='USER_ENTERED', body={"values" : content_to_upload}).execute()


    def update_google_sheet(self, values_to_add, page_name) -> None:

        service = build('sheets', 'v4', credentials=self.creds)

        sheets = service.spreadsheets()

        last_sheet_content = self.get_sheet_content(sheets=sheets, page_name=page_name)

        entries_now = len(last_sheet_content)

        entries_to_add = len(values_to_add)

        content_to_add = []

        ids = range(entries_now, entries_now + entries_to_add + 1)

        for entry, id in zip(values_to_add, ids):

            row = [str(id)] + list(entry.values())

            content_to_add.append(row)
        
        content_to_upload = last_sheet_content + content_to_add

        self.upload_new_content_to_sheet(sheets, content_to_upload=content_to_upload, page_name=page_name)


    def update_video_statuses(self, statuses):

        service = build('sheets', 'v4', credentials=self.creds)

        sheets = service.spreadsheets()

        last_page_name = self.get_last_sheet_title(sheets=sheets)

        last_sheet_content = self.get_sheet_content(sheets=sheets, page_name=last_page_name)

        last_sheet_content_columns = last_sheet_content[0]

        updated_values = []

        updated_values.append(last_sheet_content_columns)

        for entry in last_sheet_content[1:]:
        

            try:
                status = statuses.get(entry[0])

                if len(entry) > 7:

                    entry = entry[:7]

                for item in status:

                    entry.append(item)


            except:
                pass

            updated_values.append(entry)

        self.upload_new_content_to_sheet(sheets=sheets, content_to_upload=updated_values, page_name=last_page_name)


        self.upload_new_content_to_sheet(sheets=sheets, content_to_upload=updated_values, page_name=last_page_name)

    def request_approved_data_to_generation(self):

        service = build('sheets', 'v4', credentials=self.creds)

        sheets = service.spreadsheets()

        page_name = self.get_last_sheet_title(sheets=sheets)

        last_sheet_content = self.get_sheet_content(sheets=sheets, page_name=page_name)

        approved_content = []

        for entrie in last_sheet_content:

            if len(entrie) > 6 and entrie[6].strip().upper() == 'TRUE':

                if len(entrie) > 7:
                    
                    continue

                else:

                    approved_data = {'id':entrie[0], 'uuid':entrie[4], 'words':json.loads(entrie[5])}

                    approved_content.append(approved_data)
        
        return approved_content


    def request_approved_data_to_status_check(self):

        service = build('sheets', 'v4', credentials=self.creds)

        sheets = service.spreadsheets()

        page_name = self.get_last_sheet_title(sheets=sheets)

        last_sheet_content = self.get_sheet_content(sheets=sheets, page_name=page_name)

        approved_content = []

        for entrie in last_sheet_content:

            if len(entrie) > 6 and entrie[6].strip().upper() == 'TRUE' and entrie[7] != 'Processed':

                approved_data = {'id':entrie[0], 'uuid':entrie[4]}

                approved_content.append(approved_data)
        
        return approved_content

