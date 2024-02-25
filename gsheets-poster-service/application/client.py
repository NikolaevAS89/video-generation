import requests


class CallerClient:

    def __init__(self, host: str = 'http://callback:8080'):
        self._host_ = host

    def get_callback_list(self) -> list:
        response = requests.get(self._host_ + '/callback/data')
        response.raise_for_status()
        return response.json()


class ServerClient:

    def __init__(self, host: str = 'http://server:8080'):
        self._host_ = host

    def get_status(self, records: list) -> list:
        response = requests.get(self._host_ + '/generator/list/status', json=records)
        response.raise_for_status()
        return response.json()

    def make_generate(self, records: list) -> list:
        response = requests.get(self._host_ + '/generator/list')
        response.raise_for_status()
        return response.json()
