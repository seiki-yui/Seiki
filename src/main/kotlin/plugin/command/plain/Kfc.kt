package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain

object Kfc : SimpleCommand(
    SeikiMain, "kfc",
    description = "肯德基疯狂星期四"
) {
    @Handler
    suspend fun UserCommandSender.handle() {
        sendMessage(SweetBoy.get("http://139.224.249.110:8008/api/kfc/").body!!.string())
    }
}