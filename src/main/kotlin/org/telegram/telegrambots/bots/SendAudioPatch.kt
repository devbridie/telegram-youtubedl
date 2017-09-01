package org.telegram.telegrambots.bots

import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.BufferedHttpEntity
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.telegram.telegrambots.api.methods.send.SendAudio
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.exceptions.TelegramApiException
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

@Throws(TelegramApiException::class)
fun DefaultAbsSender.sendAudioCorrect(sendAudio: SendAudio): Message {

    sendAudio.validate()
    var responseContent: String

    try {
        val url = baseUrl + SendAudio.PATH
        val httppost = HttpPost(url)
        httppost.config = options.requestConfig
        if (sendAudio.isNewAudio) {
            val builder = MultipartEntityBuilder.create()
            builder.addTextBody(SendAudio.CHATID_FIELD, sendAudio.chatId)
            if (sendAudio.newAudioFile != null) {
                builder.addBinaryBody(SendAudio.AUDIO_FIELD, sendAudio.newAudioFile)
            } else if (sendAudio.newAudioStream != null) {
                builder.addBinaryBody(SendAudio.AUDIO_FIELD, sendAudio.newAudioStream, ContentType.APPLICATION_OCTET_STREAM, sendAudio.audioName)
            } else {
                builder.addBinaryBody(SendAudio.AUDIO_FIELD, java.io.File(sendAudio.audio), ContentType.create("audio/mpeg"), sendAudio.audioName)
            }
            if (sendAudio.replyMarkup != null) {
                //builder.addTextBody(SendAudio.REPLYMARKUP_FIELD, objectMapper.writeValueAsString(sendAudio.replyMarkup), TEXT_PLAIN_CONTENT_TYPE)
            }
            if (sendAudio.replyToMessageId != null) {
                builder.addTextBody(SendAudio.REPLYTOMESSAGEID_FIELD, sendAudio.replyToMessageId!!.toString())
            }
            if (sendAudio.performer != null) {
                builder.addTextBody(SendAudio.PERFOMER_FIELD, sendAudio.performer, ContentType.create("text/plain", Charset.forName("UTF-8")))
            }
            if (sendAudio.title != null) {
                builder.addTextBody(SendAudio.TITLE_FIELD, sendAudio.title, ContentType.create("text/plain", Charset.forName("UTF-8")))
            }
            if (sendAudio.duration != null) {
                builder.addTextBody(SendAudio.DURATION_FIELD, sendAudio.duration!!.toString())
            }
            if (sendAudio.disableNotification != null) {
                builder.addTextBody(SendAudio.DISABLENOTIFICATION_FIELD, sendAudio.disableNotification!!.toString())
            }
            if (sendAudio.caption != null) {
                builder.addTextBody(SendAudio.CAPTION_FIELD, sendAudio.caption)
            }
            val multipart = builder.build()
            httppost.setEntity(multipart)
        } else {
            val nameValuePairs = ArrayList<NameValuePair>()
            nameValuePairs.add(BasicNameValuePair(SendAudio.CHATID_FIELD, sendAudio.chatId))
            nameValuePairs.add(BasicNameValuePair(SendAudio.AUDIO_FIELD, sendAudio.audio))
            if (sendAudio.replyMarkup != null) {
                //nameValuePairs.add(BasicNameValuePair(SendAudio.REPLYMARKUP_FIELD, objectMapper.writeValueAsString(sendAudio.replyMarkup)))
            }
            if (sendAudio.replyToMessageId != null) {
                nameValuePairs.add(BasicNameValuePair(SendAudio.REPLYTOMESSAGEID_FIELD, sendAudio.replyToMessageId!!.toString()))
            }
            if (sendAudio.performer != null) {
                nameValuePairs.add(BasicNameValuePair(SendAudio.PERFOMER_FIELD, sendAudio.performer))
            }
            if (sendAudio.title != null) {
                nameValuePairs.add(BasicNameValuePair(SendAudio.TITLE_FIELD, sendAudio.title))
            }
            if (sendAudio.disableNotification != null) {
                nameValuePairs.add(BasicNameValuePair(SendAudio.DISABLENOTIFICATION_FIELD, sendAudio.disableNotification!!.toString()))
            }
            if (sendAudio.caption != null) {
                nameValuePairs.add(BasicNameValuePair(SendAudio.CAPTION_FIELD, sendAudio.caption))
            }
            httppost.setEntity(UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8))
        }

        val f = DefaultAbsSender::class.java.getDeclaredField("httpclient") //NoSuchFieldException
        f.isAccessible = true

        (f.get(this) as CloseableHttpClient).execute(httppost).use({ response ->
            val ht = response.getEntity()
            val buf = BufferedHttpEntity(ht)
            responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8)
            return sendAudio.deserializeResponse(responseContent)
        })
    } catch (e: IOException) {
        throw TelegramApiException("Unable to send sticker", e)
    }
}
