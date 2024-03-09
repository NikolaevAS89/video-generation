import os
from google.oauth2.credentials import Credentials
from google.auth.transport.requests import Request
from google_auth_oauthlib.flow import InstalledAppFlow
from google.oauth2 import service_account


class GSheetsAuthentacatorService:

    def __init__(self, service_account_file):
        self.service_account_file = service_account_file
        self.scopes = ['https://www.googleapis.com/auth/spreadsheets']
        self.creds = self.authenticate_service_account()



    def authenticate_service_account(self):
        creds = service_account.Credentials.from_service_account_file(self.service_account_file, scopes=self.scopes)
        if not creds or not creds.valid:
            if creds and creds.expired and creds.refresh_token:
                creds.refresh(Request())
        return creds
