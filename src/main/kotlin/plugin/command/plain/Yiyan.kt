package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain

object Yiyan : SimpleCommand(
    SeikiMain, "yiyan", "1y", "一言",
    description = "一言"
) {
    @Handler
    suspend fun UserCommandSender.handle() {
        sendMessage(SweetBoy.get("http://ovooa.com/API/dmyiyan/").body!!.string())
    }
}