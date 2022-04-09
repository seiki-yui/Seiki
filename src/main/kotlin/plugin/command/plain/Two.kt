package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain

object Two : SimpleCommand(
    SeikiMain, "2cy",
    description = "二次元少女的你"
) {
    @Handler
    suspend fun UserCommandSender.handle(name: String) {
        sendMessage(SweetBoy.get("http://ovooa.com/API/Ser/?type=text&name=$name").body!!.string())
    }
}