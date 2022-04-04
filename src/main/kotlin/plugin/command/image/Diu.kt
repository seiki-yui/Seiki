package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.User
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.sendImageFormUrl

object Diu : SimpleCommand(
    SeikiMain, "diu", "丢",
    description = "丟"
) {
    @Handler
    suspend fun CommandSender.handle(user: User) {
        subject!!.sendImageFormUrl("http://ovooa.com/API/diu/?QQ=${user.id}")
    }
}