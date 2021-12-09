FROM ghcr.io/graalvm/graalvm-ce:latest as BASE
WORKDIR /opt/graalvm
RUN gu install native-image
COPY ../day08/src/main/java .
RUN javac *.java && native-image "--static" App

FROM alpine
COPY --from=BASE /opt/graalvm/app app
COPY ../day08/input.txt .
CMD ["./app"]