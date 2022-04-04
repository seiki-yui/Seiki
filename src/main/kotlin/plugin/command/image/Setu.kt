package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.MemberCommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.sendImageFormUrl

object Setu : SimpleCommand(
    SeikiMain, "setu",
    description = "获取几张涩图~"
) {
    @Handler
    suspend fun MemberCommandSender.handle(number: Int = 1) {
        if (number in 1..10) repeat(number) {
            subject.sendImageFormUrl("http://ovooa.com/API/Pximg/?type=image")
        }
    }
}