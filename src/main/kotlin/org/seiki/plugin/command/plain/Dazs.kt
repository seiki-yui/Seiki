package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.sendTo
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching

object Dazs : SimpleCommand(
    SeikiMain, "dazs", "答案之书",
    description = "答案之书"
) {
    @Handler
    suspend fun UserCommandSender.handle(name: String) {
        subject.runCatching {
            buildMessageChain {
                +PlainText("答案之书对于问题\"$name\"的回答是:\n")
                +PlainText(SweetBoy.get("http://ovooa.com/API/daan/?type=text").body()!!.string())
            }.sendTo(subject)
        }
    }
}