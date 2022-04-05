package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.sendTo
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.uploadAsImage

object Love : SimpleCommand(
    SeikiMain, "love", "比心",
    description = "比心"
) {
    @Handler
    suspend fun UserCommandSender.handle(user: User) {
        subject.uploadAsImage("http://xiaobai.klizi.cn/API/ce/xin.php?qq=${user.id}").sendTo(subject)
    }
}