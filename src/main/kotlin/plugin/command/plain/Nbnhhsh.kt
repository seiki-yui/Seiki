package org.seiki.plugin.command.plain

import com.google.gson.Gson
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain

object Nbnhhsh : SimpleCommand(
    SeikiMain, "nbnhhsh", "hhsh",
    description = "能不能好好说话？"
) {
    @Handler
    suspend fun CommandSender.handle(text: String) {
        runCatching {
            val hashMap = HashMap<String, String>()
            hashMap["text"] = text
            val rel = SweetBoy.post("http://lab.magiconch.com/api/nbnhhsh/guess", hashMap).body!!.string()
            val json = Gson().fromJson(rel, Nbnhhsh::class.java)
            val list = json[0].trans
            var str = list[1]
            for (i in 2 until list.size - 1) {
                str += ",${list[i]}"
            }
            sendMessage("'$text'可能是:\n$str")
        }.onFailure {
            sendMessage("发生了未知的错误...")
        }
    }

    class Nbnhhsh : ArrayList<NbnhhshItem>()

    data class NbnhhshItem(
        val name: String,
        val trans: List<String>
    )
}