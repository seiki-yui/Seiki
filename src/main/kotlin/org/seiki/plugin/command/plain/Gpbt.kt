package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.PlainText
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching

object Gpbt : SimpleCommand(
    SeikiMain, "gpbt","狗屁不通",
    description = "狗屁不通文章生成器"
) {
    @Handler
    suspend fun UserCommandSender.handle(text: PlainText, long: Int = 300) {
        subject.runCatching {
            sendMessage(SweetBoy.get("http://ovooa.com/API/dog/?type=text&msg=$text&num=$long").body!!.string())
        }
    }
}