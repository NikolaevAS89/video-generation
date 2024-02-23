FROM nvidia/cuda:11.8.0-base-ubuntu22.04

RUN apt-get update

RUN apt-get install -y python3.10

RUN apt-get install -y wget && \
    wget https://bootstrap.pypa.io/get-pip.py && \
    python3.10 get-pip.py

RUN mkdir "/application"
RUN mkdir "/storage"
COPY "requirements.txt" "/application/requirements.txt"
COPY . "/application/"
RUN python3.10 -m pip install -r "/application/requirements.txt"
RUN ln -s /usr/bin/python3.10 /usr/bin/python
WORKDIR "/application/scripts"
ENTRYPOINT ["python", "main.py"]