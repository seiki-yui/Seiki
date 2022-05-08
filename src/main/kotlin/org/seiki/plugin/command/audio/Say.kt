package org.seiki.plugin.command.audio

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.sendTo
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching
import org.seiki.plugin.uploadAsAudio

object Say : SimpleCommand(
    SeikiMain, "say",
    description = "以语音读出文本"
) {
    @Handler
    suspend fun UserCommandSender.handle(text: String) {
        subject.runCatching {
            val str = SweetBoy.get("http://ovooa.com/API/yuyin/api.php?msg=$text&type=text").body!!.string()
            subject.uploadAsAudio(str).sendTo(subject)
        }
    }
}