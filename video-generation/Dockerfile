FROM huecker.io/library/python:3.10.14-slim

RUN apt-get update && \
apt-get install -y ffmpeg

RUN mkdir "/application"
RUN mkdir "/storage"
RUN mkdir "/temp"
RUN mkdir "/models_backup"
COPY requirements.txt "/application/"

RUN python3.10 -m pip install -r "/application/requirements.txt"

COPY . "/application/"

WORKDIR "/application/scripts"

ENTRYPOINT ["python3.10", "-u", "main.py"]