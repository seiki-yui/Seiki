package org.seiki.plugin.command.card

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import org.seiki.plugin.SeikiMain

object BuildForward : SimpleCommand(
    SeikiMain, "bf",
    description = "构造一个聊天记录"
) {
    @Handler
    fun CommandSender.handle() {

    }
}