FROM python:slim

RUN mkdir "/application"
RUN mkdir "/storage"
COPY "requirements.txt" "/application/requirements.txt"
COPY . "/application/"
RUN ["pip", "install", "-r", "/application/requirements.txt"]
WORKDIR "/application/scripts"
ENTRYPOINT [ "python", "main.py"]