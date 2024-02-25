import logging
from sys import stdout

import requests

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
logFormatter = logging.Formatter("%(name)-12s %(asctime)s %(levelname)-8s %(filename)s:%(funcName)s %(message)s")
consoleHandler = logging.StreamHandler(stdout)
consoleHandler.setFormatter(logFormatter)
logger.addHandler(consoleHandler)


class CallerClient:

    def __init__(self, host: str = 'http://callback:8080'):
        self._host_ = host

    def get_callback_list(self) -> list:
        logger.info(f'GET {self._host_}/callback/data')
        response = requests.get(self._host_ + '/callback/data')
        response.raise_for_status()
        return response.json()


class ServerClient:

    def __init__(self, host: str = 'http://server:8080'):
        self._host_ = host

    def get_status(self, records: list) -> list:
        logger.info(f'GET {self._host_}/generator/list/status')
        response = requests.get(self._host_ + '/generator/list/status', json=records)
        response.raise_for_status()
        return response.json()

    def make_generate(self, records: list) -> list:
        logger.info(f'POST {self._host_}/generator/list')
        response = requests.post(self._host_ + '/generator/list', json=records)
        response.raise_for_status()
        return response.json()
