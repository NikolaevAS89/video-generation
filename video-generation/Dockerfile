FROM python:3.10.14-slim

RUN mkdir "/application"
RUN mkdir "/storage"
COPY . "/application/"

RUN python3.10 -m pip install -r "/application/requirements.txt"
WORKDIR "/application/scripts"
ENTRYPOINT ["python3.10", "-u", "main.py"]