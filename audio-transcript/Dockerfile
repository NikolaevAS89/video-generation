FROM python:3.10

RUN mkdir "/application"
RUN mkdir "/storage"
COPY "requirements.txt" "/application/requirements.txt"
COPY . "/application/"
RUN ["pip", "install", "-r", "/application/requirements.txt"]
RUN pip install nvidia-cublas-cu11 nvidia-cudnn-cu11
WORKDIR "/application/scripts"
ENTRYPOINT ["sh", "-c", "export LD_LIBRARY_PATH=$(python3 -c 'import os; import nvidia.cublas.lib; import nvidia.cudnn.lib; print(os.path.dirname(nvidia.cublas.lib.__file__) + \":\" + os.path.dirname(nvidia.cudnn.lib.__file__))') && exec \"$@\"", "--"]