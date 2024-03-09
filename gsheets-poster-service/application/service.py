import logging
from datetime import datetime
from sys import stdout

from GSheetsUploaderService import GSheetsUploader
from client import CallerClient, ServerClient
from GSheetsAuthenticatorService import GSheetsAuthentacatorService

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
logFormatter = logging.Formatter("%(name)-12s %(asctime)s %(levelname)-8s %(filename)s:%(funcName)s %(message)s")
consoleHandler = logging.StreamHandler(stdout)
consoleHandler.setFormatter(logFormatter)
logger.addHandler(consoleHandler)


class MessageConsumeService:

    def __init__(self,
                 spreadsheet_id: str,
                 service_account_file: str):
        self.spreadsheet_id = spreadsheet_id
        self.service_account_file = service_account_file

    def upload(self):
        client = CallerClient()
        google_authenticator = GSheetsAuthentacatorService(service_account_file=self.service_account_file)
        google_service = GSheetsUploader(spreadsheet_id=self.spreadsheet_id,
                                         creds = google_authenticator.creds)
        sheet_name = datetime.now().strftime('%Y-%m-%d')
        values_to_add_original = client.get_callback_list()
        value_to_add_as_strings = []
        for element in values_to_add_original:
            element['words'] = str(element['words'])
            value_to_add_as_strings.append(element)
        google_service.update_google_sheet(values_to_add=value_to_add_as_strings, sheet_name=sheet_name)
        logger.info(f"New entries uploaded")

    def generate(self):
        client = ServerClient()
        google_authenticator = GSheetsAuthentacatorService(service_account_file=self.service_account_file)
        google_service = GSheetsUploader(spreadsheet_id=self.spreadsheet_id,
                                         creds = google_authenticator.creds)
        approved_data = google_service.request_approved_data_to_generation()
        response_json = client.make_generate(records=approved_data)

        status_to_update = {}
        for entry in response_json:
            status_to_update[entry['id']] = [entry['status'], entry['uuid']]
        google_service.update_video_statuses(status_to_update)
        logger.info(f"Generation uuid uploaded")

    def update_status(self):
        logger.info(f"Start update status")
        client = ServerClient()
        google_authenticator = GSheetsAuthentacatorService(service_account_file=self.service_account_file)
        google_service = GSheetsUploader(spreadsheet_id=self.spreadsheet_id,
                                         creds = google_authenticator.creds)
        approved_data = google_service.request_approved_data_to_status_check()
        logger.info(f"approved_data={str(approved_data)}")
        response_json = client.get_status(approved_data)
        logger.info(f"response_json={str(response_json)}")
        status_to_update = {}
        for entry in response_json:
            status_to_update[entry['id']] = [entry['status'], entry['uuid']]
        logger.info(f"status_to_update={str(status_to_update)}")
        google_service.update_video_statuses(status_to_update)
        logger.info(f"UUID generation statuses updated")
