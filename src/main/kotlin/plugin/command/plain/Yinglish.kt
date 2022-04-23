package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.YinglishUtil.yinglish

object Yinglish : SimpleCommand(
    SeikiMain, "yinglish",
    description = "淫语翻译器"
) {
    @Handler
    suspend fun UserCommandSender.handle(text: String) {
        subject.sendMessage(text.yinglish)
    }
}