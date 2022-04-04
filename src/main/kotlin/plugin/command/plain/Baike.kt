package org.seiki.plugin.command.plain

import com.google.gson.Gson
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain

object Baike : SimpleCommand(
    SeikiMain, "baike", "baidu",
    description = "百度百科"
) {
    @Handler
    suspend fun CommandSender.handle(msg: String) {
        val rel = SweetBoy.get("http://ovooa.com/API/bdbk/?Msg=$msg").body!!.string()
        val json = Gson().fromJson(rel, Baike::class.java)
        sendMessage(
            if (json.code == 1) {
                buildMessageChain {
                    +PlainText(json.data.Msg + "\n")
                    +PlainText(json.data.info + "\n")
                }
            } else PlainText(json.text)
        )
    }

    data class Baike(
        val code: Int,
        val `data`: Data,
        val text: String
    )

    data class Data(
        val Msg: String,
        val image: String,
        val info: String,
        val url: String
    )
}