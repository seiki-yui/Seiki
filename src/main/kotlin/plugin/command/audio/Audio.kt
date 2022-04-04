package org.seiki.plugin.command.audio

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.contact.AudioSupported
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.SeikiMain.audioFolder
import java.io.File

object Audio : CompositeCommand(
    SeikiMain, "audio",
    description = "音频"
) {
    @SubCommand
    @Description("发送文件")
    suspend fun CommandSender.send(path: String) {
        (subject as AudioSupported).uploadAudio(File("$audioFolder/$path").toExternalResource()).sendTo(subject!!)
    }
}