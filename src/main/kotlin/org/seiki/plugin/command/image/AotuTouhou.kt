package org.seiki.plugin.command.image

import com.google.gson.Gson
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.warning
import org.seiki.SweetBoy
import org.seiki.plugin.runCatching
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.uploadAsImage

object AotuTouhou : SimpleCommand(
    SeikiMain, "aotutouhou", "东方好图","东方图","th","touhou","touhouaotu",
    description = "获取几张好康的"
) {
    @Handler
    suspend fun UserCommandSender.handle(number: Int = 1) {
        if (number in 1..10) {
            SeikiMain.logger.info { "AotuTouhou - 开始缓存" }
            repeat(number) { now ->
                subject.runCatching {
//                    https://api.r10086.com/img-api.php?type=东方project1
                    subject.uploadAsImage("https://img.paulzzh.com/touhou/random").sendTo(subject)
                }.onSuccess {
                    SeikiMain.logger.info { "AotuTouhou - ${now + 1} / $number SUCCESS" }
                }.onFailure {
                    SeikiMain.logger.warning { "AotuTouhou - ${now + 1} / $number FAILURE" }
                }
            }
        }
    }
}