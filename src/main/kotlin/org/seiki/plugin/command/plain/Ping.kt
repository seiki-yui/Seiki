package org.seiki.plugin.command.plain

import com.google.gson.Gson
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.sendTo
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching

object Ping : SimpleCommand(
    SeikiMain, "ping",
    description = "Ping!"
) {
    @Handler
    suspend fun UserCommandSender.handle(url: String = "seiki.fun", num: Int = 2) {
        subject.runCatching {
            val str = "http://ovooa.com/API/ping/?url=$url&num=$num"
            val rel = SweetBoy.get(str).body()!!.string()
            val json = Gson().fromJson(rel, Ping::class.java)
            (if (json.code == 1) {
                buildMessageChain {
                    +PlainText("Ping! ${json.data.IP} ")
                    +PlainText("用时 ${json.data.Times}ms! ")
                    +PlainText("延迟 ${json.data.average}ms!")
                }
            } else PlainText(json.text)).sendTo(subject)
        }
    }

    data class Ping(
        val code: Int,
        val `data`: Data,
        val text: String
    )

    data class Data(
        val IP: String,
        val Times: String,
        val abandon: String,
        val address: String,
        val average: String,
        val max: String,
        val num: String,
        val receive: String,
        val small: String,
        val url: String
    )
}