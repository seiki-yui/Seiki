package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.sendTo
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching
import org.seiki.plugin.uploadAsImage

object Bishi : SimpleCommand(
    SeikiMain, "bishi", "鄙视",
    description = "鄙视"
) {
    @Handler
    suspend fun UserCommandSender.handle(user: User) {
        subject.runCatching {
            subject.uploadAsImage("http://xiaobai.klizi.cn/API/ce/bishi.php?qq=${user.id}").sendTo(subject)
        }
    }
}