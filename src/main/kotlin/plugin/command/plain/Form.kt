package org.seiki.plugin.command.plain

import com.google.gson.Gson
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain

object Form : SimpleCommand(
    SeikiMain, "form",
    description = "查查你的成分构成……"
) {
    @Handler
    suspend fun UserCommandSender.handle(name: String) {
        val rel = SweetBoy.get("http://ovooa.com/API/name/?msg=$name").body!!.string()
        val json = Gson().fromJson(rel, Form::class.java)
        sendMessage(json.text)
    }

    data class Form(
        val code: Int,
        val text: String
    )
}