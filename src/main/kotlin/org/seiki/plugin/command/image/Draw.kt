package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.MemberCommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.sendTo
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.getOrWaitImage
import org.seiki.plugin.runCatching
import org.seiki.plugin.uploadAsImage

object Draw : SimpleCommand(
    SeikiMain, "draw", "素描",
    description = "素描"
) {
    @Handler
    suspend fun MemberCommandSenderOnMessage.handle(image: Image? = null) {
        subject.runCatching {
            val img = image ?: fromEvent.getOrWaitImage() ?: return@runCatching
            subject.uploadAsImage("http://ovooa.com/API/xian/?url=${img.queryUrl()}").sendTo(subject)
        }
    }
}