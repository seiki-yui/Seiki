package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.User
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.sendImageFormUrl

object Qian : SimpleCommand(
    SeikiMain, "qian", "牵",
    description = "牵"
) {
    @Handler
    suspend fun CommandSender.handle(user1: User, user2: User) {
        subject!!.sendImageFormUrl("http://xiaobai.klizi.cn/API/ce/qian.php?qq=${user1.id}&qq1=${user2.id}")
    }
}