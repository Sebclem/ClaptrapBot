FROM python:3
ENV PYTHONUNBUFFERED 1
#RUN apt-get update \
#    && apt-get install -y python-mysqldb

RUN mkdir /code
WORKDIR /code
ADD src/requirements.txt /code/
RUN pip install -r requirements.txt
ADD . /code/
