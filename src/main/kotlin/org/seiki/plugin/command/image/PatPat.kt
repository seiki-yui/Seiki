package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.events.UserMessageEvent
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.seiki.SweetBoy
import org.seiki.plugin.PatPatUtil.patpat
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.getOrWaitImage
import org.seiki.plugin.runCatching
import org.jetbrains.skia.Image as SkImage

/**
 * 好像是llt写的（）
 */
object PatPat : SimpleCommand(
    SeikiMain, "pat", "patpat", "摸",
    description = "摸一摸"
) {
    @Handler
    suspend fun CommandSenderOnMessage<UserMessageEvent>.handle(user: User? = null, delay: Double = .05) {
        subject!!.runCatching {
            val img = SkImage.makeFromEncoded(
                SweetBoy.getStream(
                    user?.avatarUrl ?: fromEvent.getOrWaitImage()?.queryUrl() ?: return@runCatching
                ).use { it.readBytes() })
            patpat(img, delay).bytes.toExternalResource("GIF").use { subject!!.sendImage(it) }
        }
    }
}