FROM openjdk:11-jdk
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/install/trefu-be/ /app/
WORKDIR /app/bin
RUN ["chmod", "+x", "./trefu-be"]
CMD ["./trefu-be"]