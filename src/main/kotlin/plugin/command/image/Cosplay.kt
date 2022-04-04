package org.seiki.plugin.command.image

import com.google.gson.Gson
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.utils.info
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.uploadImageFormUrl

object Cosplay : SimpleCommand(
    SeikiMain, "cos",
    description = "Cosplay图片，打包为聊天记录"
) {
    @Handler
    suspend fun UserCommandSender.handle() {
        val json =
            Gson().fromJson(SweetBoy.get("http://ovooa.com/API/cosplay/").body!!.string(), Cosplay::class.java)
        if (json.code == "1") {
            val data = json.data.data
            sendMessage(if (data.size > 100) "oh shit 图片超过100张！" else "有${data.size}张图片待发送，请稍等……")
            buildForwardMessage (subject) {
                add(bot,PlainText(json.data.Title))
                data.forEach {
                    add(bot,subject.uploadImageFormUrl(it))
                    SeikiMain.logger.info { it }
                }
            }.sendTo(subject)
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