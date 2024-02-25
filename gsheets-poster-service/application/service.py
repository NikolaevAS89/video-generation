import logging
from datetime import datetime

from GSheetsUploaderService import GSheetsUploader
from client import CallerClient, ServerClient

logging.basicConfig(level=logging.ERROR, format='%(asctime)s - %(levelname)s - %(message)s')


class MessageConsumeService:

    def __init__(self,
                 spreadsheet_id: str,
                 service_account_file: str):
        self.spreadsheet_id = spreadsheet_id
        self.service_account_file = service_account_file

    def upload(self):
        client = CallerClient()
        google_service = GSheetsUploader(spreadsheet_id=self.spreadsheet_id,
                                         service_account_file=self.service_account_file)
        page_name = datetime.now().strftime('%Y-%m-%d')
        values_to_add_original = client.get_callback_list()
        value_to_add_as_strings = []
        for element in values_to_add_original:
            element['words'] = str(element['words'])
            value_to_add_as_strings.append(element)
        google_service.update_google_sheet(values_to_add=value_to_add_as_strings, page_name=page_name)
        logging.info(f"New entries uploaded")

    def generate(self):
        client = ServerClient()
        google_service = GSheetsUploader(spreadsheet_id=self.spreadsheet_id,
                                         service_account_file=self.service_account_file)
        approved_data = google_service.request_approved_data_to_generation()
        response_json = client.make_generate(records=approved_data)

        status_to_update = {}
        for entry in response_json:
            status_to_update[entry['id']] = [entry['status'], entry['uuid']]
        google_service.update_video_statuses(status_to_update)
        logging.info(f"Generation uuid uploaded")

    def update_status(self):
        client = ServerClient()
        google_service = GSheetsUploader(spreadsheet_id=self.spreadsheet_id,
                                         service_account_file=self.service_account_file)
        approved_data = google_service.request_approved_data_to_status_check()
        response_json = client.get_status(approved_data)
        status_to_update = {}
        for entry in response_json:
            status_to_update[entry['id']] = [entry['status'], entry['uuid']]
        google_service.update_video_statuses(status_to_update)
        logging.info(f"UUID generation statuses updated")
