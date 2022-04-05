package org.seiki.plugin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.AudioSupported
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.AbstractEvent
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.seiki.SweetBoy
import java.io.File

class TimeTickEvent(
    var bot: Bot,
    var timestamp: Long,
) : AbstractEvent()

val ownerList = arrayListOf(2630557998L, 1812691029L)

val biliUrlRegex = """(?:(?:https?://)?(?:www\.)?bilibili\.com/video/)?([aA][vV]\d+|[bB][vV][a-zA-Z0-9]+).*""".toRegex()
val bili23tvRegex = """.*((?:https?://)?(?:www\.)?b23\.tv/[a-zA-Z0-9]+).*""".toRegex()

fun MemberPermission.getName(): String = when (this) {
    MemberPermission.MEMBER -> "群员"
    MemberPermission.ADMINISTRATOR -> "管理员"
    MemberPermission.OWNER -> "群主"
}

suspend fun Contact.uploadAsImage(url: String) = withContext(Dispatchers.IO) {
    SweetBoy.downloadAsByteStream(url).uploadAsImage(this@uploadAsImage)
}

suspend fun Contact.uploadAsImage(file: File) = withContext(Dispatchers.IO) {
    file.uploadAsImage(this@uploadAsImage)
}

suspend fun Contact.uploadAsAudio(url: String) = withContext(Dispatchers.IO) {
    (this@uploadAsAudio as AudioSupported).uploadAudio(SweetBoy.downloadAsByteStream(url).toExternalResource())
}

suspend fun Contact.uploadAsAudio(file: File) = withContext(Dispatchers.IO) {
    (this@uploadAsAudio as AudioSupported).uploadAudio(file.toExternalResource())
}