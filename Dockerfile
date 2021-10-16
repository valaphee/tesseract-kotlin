# syntax=docker/dockerfile:1

FROM ghcr.io/graalvm/graalvm-ce:21.2.0
WORKDIR /tmp
COPY ./build/libs/ ./
ENTRYPOINT ["tesseract.jar"]
