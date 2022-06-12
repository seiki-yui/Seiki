package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.UnvcodeUtil.unvcode

object Unvcode: SimpleCommand(
    SeikiMain, "unvcode","unv","幼女",
    description = "获取反和谐幼女code"
) {
    @Handler
    suspend fun UserCommandSender.handle(text: String) {
        subject.sendMessage(text.unvcode)
    }
}