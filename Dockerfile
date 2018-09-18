FROM java:8
COPY . /usr/src/telegram-youtubedl
WORKDIR /usr/src/telegram-youtubedl
RUN apt-get update && apt-get install -y curl ffmpeg
RUN curl -L https://yt-dl.org/downloads/latest/youtube-dl -o /usr/local/bin/youtube-dl
RUN chmod a+rx /usr/local/bin/youtube-dl
RUN ./gradlew build

CMD ./gradlew run