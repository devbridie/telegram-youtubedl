package com.devbridie.telegramyoutubedl.telegram.bot

import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.stringType

object BotConfiguration : PropertyGroup() {
    val token by stringType
    val name by stringType
}