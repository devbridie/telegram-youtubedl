package com.devbridie.telegramyoutubedl.telegram

import com.devbridie.telegramyoutubedl.youtubedl.wrappers.VideoInformation
import java.io.File

sealed class DownloadStatus
object GettingInfoDownloadStatus : DownloadStatus()
class FailedGettingInfoStatus(val input: String) : DownloadStatus()

class DownloadTooLongFailure(val info: VideoInformation, val maximum: Int) : DownloadStatus()

class DownloadingDownloadStatus(val info: VideoInformation) : DownloadStatus()
class CompletedDownloadStatus(val info: VideoInformation, val file: File) : DownloadStatus()
class FailedDownloadStatus(val info: VideoInformation) : DownloadStatus()
