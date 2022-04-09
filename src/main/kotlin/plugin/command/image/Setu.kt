package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import org.seiki.plugin.SeikiMain

object Setu : SimpleCommand(
    SeikiMain, "setu",
    description = "获取几张涩图~"
) {
    @Handler
    suspend fun UserCommandSender.handle(/*number: Int = 1*/) {
        sendMessage("由于bot多次被封, 暂时停止setu功能的使用 - ${System.currentTimeMillis()}")
//        if (number in 1..10) {
//            SeikiMain.logger.info { "Setu - 开始缓存" }
//            repeat(number) { now ->
//                kotlin.runCatching {
//                    subject.uploadAsImage("http://ovooa.com/API/Pximg/?type=image").sendTo(subject)
//                }.onSuccess {
//                    SeikiMain.logger.info { "Setu - ${now + 1} / $number SUCCESS" }
//                }.onFailure {
//                    SeikiMain.logger.warning { "Setu - ${now + 1} / $number FAILURE" }
//                }
//            }
//        }
    }
}