package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.MemberCommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.PermissionDeniedException
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching

object Fencing : SimpleCommand(
    SeikiMain, "fencing", "击剑", "\uD83E\uDD3A",
    description = "击剑:at一个人，随机禁言其中一个"
) {
    @Handler
    suspend fun MemberCommandSender.handle(member: Member) {
        subject.runCatching {
            arrayOf(this@handle.user, member).random().mute((1..3).random() * 60)
        }.onFailure {
            if (it is PermissionDeniedException) subject.sendMessage("Bot没有管理员权限,没法让你们基建咯") else throw it
        }
    }
}