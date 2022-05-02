package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.sendTo
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching
import org.seiki.plugin.uploadAsImage

object Qian : SimpleCommand(
    SeikiMain, "qian", "牵",
    description = "牵"
) {
    @Handler
    suspend fun UserCommandSender.handle(user1: User, user2: User) {
        subject.runCatching {
            subject.uploadAsImage("http://xiaobai.klizi.cn/API/ce/qian.php?qq=${user1.id}&qq1=${user2.id}")
                .sendTo(subject)
        }
    }
}