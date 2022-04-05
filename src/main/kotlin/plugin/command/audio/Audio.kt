package org.seiki.plugin.command.audio

import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.sendTo
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.SeikiMain.audioFolder
import org.seiki.plugin.uploadAsAudio
import java.io.File

object Audio : CompositeCommand(
    SeikiMain, "audio",
    description = "音频"
) {
    @SubCommand
    @Description("发送文件")
    suspend fun UserCommandSender.send(path: String) {
        subject.uploadAsAudio(File("$audioFolder/$path")).sendTo(subject)
    }
}