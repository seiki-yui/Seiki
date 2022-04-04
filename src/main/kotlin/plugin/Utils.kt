package org.seiki.plugin

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.AbstractEvent
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.seiki.SweetBoy

class TimeTickEvent(
    var bot: Bot,
    var timestamp: Long,
) : AbstractEvent()

val ownerList = arrayListOf(2630557998L, 1812691029L)

fun MemberPermission.getName(): String = when (this) {
    MemberPermission.MEMBER -> "群员"
    MemberPermission.ADMINISTRATOR -> "管理员"
    MemberPermission.OWNER -> "群主"
}

fun downloadImage(url: String) = SweetBoy.get(url).body!!.byteStream()

suspend fun downloadAsImage(url: String, subject: Contact) = downloadImage(url).uploadAsImage(subject)

suspend fun Contact.uploadImageFormUrl(url: String) = downloadAsImage(url,this)

suspend fun Contact.sendImageFormUrl(url: String) = this.uploadImageFormUrl(url).sendTo(this)