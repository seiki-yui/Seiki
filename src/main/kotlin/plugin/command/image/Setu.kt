package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.MemberCommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.warning
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.uploadAsImage

object Setu : SimpleCommand(
    SeikiMain, "setu",
    description = "获取几张涩图~"
) {
    @Handler
    suspend fun MemberCommandSender.handle(number: Int = 1) {
        if (number in 1..10) {
            SeikiMain.logger.info { "Setu - 开始缓存" }
            repeat(number) { now ->
                kotlin.runCatching {
                    subject.uploadAsImage("http://ovooa.com/API/Pximg/?type=image").sendTo(subject)
                }.onSuccess {
                    SeikiMain.logger.info { "Setu - ${now + 1} / $number SUCCESS" }
                }.onFailure {
                    SeikiMain.logger.warning { "Setu - ${now + 1} / $number FAILURE" }
                }
            }
        }
    }
}