package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.sendTo
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain

object Dazs : SimpleCommand(
    SeikiMain, "dazs",
    description = "答案之书"
) {
    @Handler
    suspend fun UserCommandSender.handle(name: String) {
        buildMessageChain {
            +PlainText("答案之书对于问题\"$name\"的回答是:\n")
            +PlainText(SweetBoy.get("http://ovooa.com/API/daan/?type=text").body!!.string())
        }.sendTo(subject)
    }
}