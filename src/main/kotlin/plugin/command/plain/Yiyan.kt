package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain

object Yiyan : SimpleCommand(
    SeikiMain, "yiyan", "1y", "一言",
    description = "一言"
) {
    @Handler
    suspend fun CommandSender.handle() {
        sendMessage(SweetBoy.get("http://ovooa.com/API/dmyiyan/").body!!.string())
    }
}