package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.MemberCommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.User
import org.seiki.plugin.PatPatTool
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.SeikiMain.tempFolder
import org.seiki.plugin.runCatching

object PatPat : SimpleCommand(
    SeikiMain, "pat", "patpat","摸",
    description = "摸一摸"
) {
    @Handler
    suspend fun MemberCommandSenderOnMessage.handle(user: User) {
        subject.runCatching {
            PatPatTool.getPat(user, 80)
            subject.sendImage(tempFolder.resolve("${user.id}_pat.gif"))
        }
    }
}