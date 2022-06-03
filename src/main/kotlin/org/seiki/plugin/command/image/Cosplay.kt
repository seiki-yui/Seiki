package org.seiki.plugin.command.image

import com.google.gson.Gson
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.warning
import org.seiki.SweetBoy
import org.seiki.SweetBoy.transToNum
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.uploadAsImage

object Cosplay : SimpleCommand(
    SeikiMain, "cos", "cosplay",
    description = "Cosplay图片，打包为聊天记录"
) {
    @Handler
    suspend fun UserCommandSender.handle() {
        val json =
            Gson().fromJson(SweetBoy.get("http://ovooa.com/API/cosplay/").body!!.string(), Cosplay::class.java)
        if (json.code == "1") {
            val start = System.currentTimeMillis()
            val data = json.data.data
            sendMessage(if (data.size >= 100) "oh shit 图片超过100张！" else "有${data.size}张图片待发送，请稍等……")
            buildForwardMessage(subject) {
                add(bot, PlainText(json.data.Title))
                var time = System.currentTimeMillis()
                SeikiMain.logger.info { "Cosplay - 开始缓存..." }
                data.forEachIndexed { index, s ->
                    kotlin.runCatching {
                        val image = subject.uploadAsImage(s)
                        add(bot, image)
                        SeikiMain.logger.info {
                            "${index + 1} / ${data.size} = ${(image.size / 1024.0).transToNum(2)}kb : ${
                                (time - System.currentTimeMillis() / 1000.0).transToNum(2)
                            }s SUCCESS -> $s"
                        }
                        time = System.currentTimeMillis()
                    }.onFailure {
                        SeikiMain.logger.warning { "Cosplay - ${index + 1} / ${data.size} FAILURE -> ${it.javaClass.name} : ${it.message}" }
                    }
                }
            }.sendTo(subject)
            sendMessage("总用时: ${(start - System.currentTimeMillis() / 1000.0).transToNum(2)}")
        }
    }

    data class Cosplay(
        val code: String,
        val `data`: Data,
        val text: String
    )

    data class Data(
        val Title: String,
        val `data`: List<String>
    )
}