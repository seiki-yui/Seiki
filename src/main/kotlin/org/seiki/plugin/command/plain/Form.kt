package org.seiki.plugin.command.plain

import com.google.gson.Gson
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching

object Form : SimpleCommand(
    SeikiMain, "form", "组成",
    description = "查查你的成分构成……"
) {
    @Handler
    suspend fun UserCommandSender.handle(name: String) {
        subject.runCatching {
            val rel = SweetBoy.get("http://ovooa.com/API/name/?msg=$name").body()!!.string()
            val json = Gson().fromJson(rel, Form::class.java)
            sendMessage(json.text)
        }
    }

    data class Form(
        val code: Int,
        val text: String
    )
}