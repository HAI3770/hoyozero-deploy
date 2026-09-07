FROM python:3.12-alpine
RUN apk add --no-cache docker-cli docker-cli-buildx git
COPY runner.py /runner.py
CMD ["python", "/runner.py"]
