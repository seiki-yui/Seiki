package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.warning
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.uploadAsImage

object Aotu : SimpleCommand(
    SeikiMain, "aotu",
    description = "获取几张好康的"
) {
    @Handler
    suspend fun UserCommandSender.handle(number: Int = 1) {
        if (number in 1..10) {
            SeikiMain.logger.info { "Aotu - 开始缓存" }
            repeat(number) { now ->
                kotlin.runCatching {
                    subject.uploadAsImage("https://iw233.cn/api.php?sort=random").sendTo(subject)
                }.onSuccess {
                    SeikiMain.logger.info { "Aotu - ${now + 1} / $number SUCCESS" }
                }.onFailure {
                    SeikiMain.logger.warning { "Aotu - ${now + 1} / $number FAILURE" }
                }
            }
        }
    }
}