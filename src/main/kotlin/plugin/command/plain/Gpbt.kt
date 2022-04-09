package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.PlainText
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain

object Gpbt : SimpleCommand(
    SeikiMain, "gpbt",
    description = "狗屁不通文章生成器"
) {
    @Handler
    suspend fun UserCommandSender.handle(text: PlainText, long: Int = 300) {
        sendMessage(SweetBoy.get("http://ovooa.com/API/dog/?type=text&msg=$text&num=$long").body!!.string())
    }
}