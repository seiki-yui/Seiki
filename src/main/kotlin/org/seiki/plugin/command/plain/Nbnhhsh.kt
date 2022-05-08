package org.seiki.plugin.command.plain

import com.google.gson.Gson
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.utils.warning
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching

object Nbnhhsh : SimpleCommand(
    SeikiMain, "nbnhhsh", "hhsh",
    description = "能不能好好说话？"
) {
    @Handler
    suspend fun UserCommandSender.handle(text: String) {
        subject.runCatching {
            val hashMap = HashMap<String, String>()
            hashMap["text"] = text
            val rel = SweetBoy.post("https://lab.magiconch.com/api/nbnhhsh/guess", hashMap).body!!.string()
            val json = Gson().fromJson(rel, Nbnhhsh::class.java)
            val list = json[0].trans
            var str = list[1]
            for (i in 2 until list.size - 1) {
                str += ",${list[i]}"
            }
            sendMessage("'$text'可能是:\n$str")
            SeikiMain.logger.warning { "Nbnhhsh - SUCCESS" }
        }.onFailure {
            SeikiMain.logger.warning { "Nbnhhsh - FAILURE" }
        }
    }

    class Nbnhhsh : ArrayList<NbnhhshItem>()

    data class NbnhhshItem(
        val name: String,
        val trans: List<String>
    )
}