package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching

object Diana : SimpleCommand(
    SeikiMain, "diana",
    description = "发病"
) {
    @Handler
    suspend fun UserCommandSender.handle(name: String = "然然") {
        subject.runCatching {
            sendMessage(SweetBoy.get("http://139.224.249.110:8008/api/diana/?name=$name").body!!.string())
        }
    }
}