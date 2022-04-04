package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.User
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.sendImageFormUrl

object Love : SimpleCommand(
    SeikiMain, "love", "比心",
    description = "比心"
) {
    @Handler
    suspend fun CommandSender.handle(user: User) {
        subject!!.sendImageFormUrl("http://xiaobai.klizi.cn/API/ce/xin.php?qq=${user.id}")
    }
}