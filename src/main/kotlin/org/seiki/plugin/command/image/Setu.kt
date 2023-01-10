package org.seiki.plugin.command.image

import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.utils.debug
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.warning
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.uploadAsImage

object Setu : SimpleCommand(
    SeikiMain, "setu", "涩图",
    description = "获取几张涩图~"
) {
    @Handler
    suspend fun UserCommandSender.handle(number: Int = 1, line: String = "r18=0") {
        if (number in 1..10) {
            SeikiMain.logger.info { "Setu - 开始缓存" }
            repeat(number) { now ->
                kotlin.runCatching {
                    val rel =
                        SweetBoy.get("https://api.lolicon.app/setu/v2?size=regular&$line").use { it.body()!!.string() }
                    SeikiMain.logger.info { rel }
                    val json = Gson().fromJson(rel, LoliconApi::class.java)
                    val url = json.data.first().urls.regular
                    SeikiMain.logger.debug { url }
                    val msg = subject.uploadAsImage(url).sendTo(subject)
                    launch {
                        delay((20..40).random() * 1000L)
                        SeikiMain.logger.info { "Setu - ${now + 1} / $number RECALL" }
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

    data class LoliconApi(
        val `data`: List<Data>,
        val error: String
    )

    data class Data(
        val author: String,
        val ext: String,
        val height: Int,
        val p: Int,
        val pid: Int,
        val r18: Boolean,
        val tags: List<String>,
        val title: String,
        val uid: Int,
        val uploadDate: Long,
        val urls: Urls,
        val width: Int
    )

    data class Urls(
        val regular: String
    )
}
