FROM nvidia/cuda:12.2.2-cudnn8-runtime-ubuntu22.04

USER root

RUN apt-get update && \
apt-get install -y software-properties-common && \
add-apt-repository ppa:deadsnakes/ppa && \
apt-get update && \
apt-get install -y python3.10-minimal && \
apt-get install -y python3-distutils && \
apt-get install -y wget && \
wget https://bootstrap.pypa.io/get-pip.py && \
python3.10 get-pip.py

RUN mkdir "/application"
RUN mkdir "/storage"
COPY "requirements.txt" "/application/requirements.txt"
COPY . "/application/"

RUN python3.10 -m pip install -r "/application/requirements.txt"
RUN ln -s /usr/bin/python3.10 /usr/bin/python
WORKDIR "/application/scripts"
ENTRYPOINT ["python", "-u", "main.py"]