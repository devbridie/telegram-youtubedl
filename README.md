# telegram-youtubedl

An implementation of a [Telegram](https://telegram.org/) bot that uses [youtube-dl](https://github.com/rg3/youtube-dl) to download videos from youtube.com and sends them as mp3.

## Usage (CLI)
1. Make sure `youtube-dl` is available in the path. Tested with version 2018.09.18.
2. Create a new bot using [BotFather](https://telegram.me/botfather).
3. Copy `/src/main/resources/configuration.properties.example` to `/src/main/resources/configuration.properties`.
4. Edit `/src/main/resources/configuration.properties` and fill in the bot name that was selected and the bot token that was given by BotFather.
5. Run with `./gradlew start`.
6. Start a chat with the newly created bot or invite it to a group chat.

## Usage (Docker)
1. Create a new bot using [BotFather](https://telegram.me/botfather).
2. Use `docker build -t telegramyoutubedl .` to build the image.
3. Start the image with `docker run -dit -e "BotConfiguration.token=<token from step 1>" -e "BotConfiguration.name=<name from step 1>" telegramyoutubedl`
4. Start a chat with the newly created bot or invite it to a group chat.

## Contributions
PRs will be accepted. Currently, the following items need some work:

- [ ] Windows support verification
- [ ] Support URLs that come from YouTube Mix
- [ ] Allow users to set custom metadata for incoming audio files

## License
This project is licensed under the terms of the MIT license.