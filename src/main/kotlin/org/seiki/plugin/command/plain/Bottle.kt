package org.seiki.plugin.command.plain

import com.google.gson.Gson
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.UserCommandSender
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching

object Bottle : CompositeCommand(
    SeikiMain, "bottle",
    description = "漂流瓶"
) {
    @SubCommand
    @Description("捡漂流瓶")
    suspend fun UserCommandSender.get() {
        subject.runCatching {
            val json = Gson().fromJson(SweetBoy.get("http://ovooa.com/API/Piao/").body!!.string(), Bottle::class.java)
            sendMessage("${json.data[0].title}\n${json.data[0].text}\n${json.data[0].time}")
        }
    }

    @SubCommand
    @Description("扔漂流瓶")
    suspend fun UserCommandSender.`throw`(title: String, text: String) {
        val json = Gson().fromJson(
            SweetBoy.get("http://ovooa.com/API/Piao/?Select=1&title=${title}&msg=${text}&QQ=${subject.id}").body!!.string(),
            BottleThrow::class.java
        )
        sendMessage(json.text)
    }

    data class Bottle(
        val Tips: String,
        val code: String,
        val `data`: List<Data>,
        val text: String
    )

    data class Data(
        val id: String,
        val text: String,
        val time: String,
        val title: String
    )

    data class BottleThrow(
        val code: Int,
        val text: String
    )
}