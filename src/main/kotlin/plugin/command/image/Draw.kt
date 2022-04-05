package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.sendTo
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.uploadAsImage

object Draw : SimpleCommand(
    SeikiMain, "draw", "素描",
    description = "素描"
) {
    @Handler
    suspend fun UserCommandSender.handle(image: Image) {
        subject.uploadAsImage("http://ovooa.com/API/xian/?url=${image.queryUrl()}").sendTo(subject)
    }
}