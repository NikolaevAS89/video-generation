FROM python:3.12

RUN mkdir "/application"

COPY "requirements.txt" "/application/requirements.txt"

COPY . "/application/"

RUN python3.12 -m pip install -r "/application/requirements.txt"

RUN ln -s /usr/bin/python3.12 /usr/bin/python

WORKDIR "/application"

ENTRYPOINT ["python", "main.py"]