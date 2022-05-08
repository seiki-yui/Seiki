package org.seiki.plugin.command.image

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.warning
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.uploadAsImage

object Setu : SimpleCommand(
    SeikiMain, "setu","涩图",
    description = "获取几张涩图~"
) {
    @Handler
    suspend fun UserCommandSender.handle(number: Int = 1) {
        if (number in 1..10) {
            SeikiMain.logger.info { "Setu - START" }
            repeat(number) { now ->
                runCatching {
                    val msg = subject.uploadAsImage("http://iw233.fgimax2.fgnwctvip.com/API/Ghs.php").sendTo(subject)
                    launch {
                        delay(60000L)
                        msg.recall()
                    }
                }.onSuccess {
                    SeikiMain.logger.info { "Setu - ${now + 1} / $number SUCCESS" }
                }.onFailure {
                    SeikiMain.logger.warning { "Setu - ${now + 1} / $number FAILURE" }
                }
            }
        }
    }
}