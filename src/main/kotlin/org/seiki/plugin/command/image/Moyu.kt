package org.seiki.plugin.command.image

import com.google.gson.Gson
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.sendTo
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching
import org.seiki.plugin.uploadAsImage

object Moyu : SimpleCommand(
    SeikiMain, "moyu", "摸鱼",
    description = "获取今日摸鱼日历"
) {
    @Handler
    suspend fun UserCommandSender.handle() {
        subject.runCatching {
            subject.moyu().sendTo(subject)
        }
    }

    suspend fun Contact.moyu(): Image {
        val rel = SweetBoy.get("https://api.j4u.ink/v1/store/other/proxy/remote/moyu.json").body!!.string()
        val url = Gson().fromJson(rel, Moyu::class.java).data.moyu_url
        return this.uploadAsImage(url)
    }

    data class Moyu(
        val code: Int,
        val `data`: Data,
        val message: String
    )

    data class Data(
        val articles: List<Article>,
        val moyu_url: String
    )

    data class Article(
        val cover: String,
        val digest: String,
        val pub_time: String,
        val sources: Sources,
        val title: String,
        val url: String
    )

    data class Sources(
        val source: Source
    )

    data class Source(
        val name: String
    )
}