package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain

object Dazs : SimpleCommand(
    SeikiMain, "dazs",
    description = "答案之书"
) {
    @Handler
    suspend fun CommandSender.handle(name: String) {
        sendMessage(buildMessageChain {
            +PlainText("答案之书对于问题\"$name\"的回答是:\n")
            +PlainText(SweetBoy.get("http://ovooa.com/API/daan/?type=text").body!!.string())
        })
    }
}