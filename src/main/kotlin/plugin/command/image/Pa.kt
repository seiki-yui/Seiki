package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.sendTo
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.uploadAsImage

object Pa : SimpleCommand(
    SeikiMain, "pa", "爬",
    description = "爬"
) {
    @Handler
    suspend fun UserCommandSender.handle(user: User) {
        subject.uploadAsImage("http://ovooa.com/API/pa/?QQ=${user.id}").sendTo(subject)
    }
}