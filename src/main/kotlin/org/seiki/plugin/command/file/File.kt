package org.seiki.plugin.command.file

import cn.hutool.core.util.URLUtil
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.MemberCommandSender
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.sendTo
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.convert
import org.seiki.plugin.runCatching
import java.io.File

object File: CompositeCommand(
    SeikiMain, "file",
    description = "操作Seiki资源站文件."
) {
    @SubCommand
    suspend fun UserCommandSender.ls(path: String) {
        subject.runCatching {
            var str = ""
            var i = 0
            File(path).walk().maxDepth(1).forEach {
                i += 1
                str += it.name + (if (it.isDirectory) "/" else "") + (if (i == 0) "" else "\n")
            }
            buildForwardMessage (subject) {
                str.convert().forEach {
                    add(bot, PlainText(it))
                }
            }.sendTo(subject)
        }
    }
    @SubCommand
    suspend fun MemberCommandSender.upload(name: String) {
        subject.runCatching {
            val path = if (System.getProperties().getProperty("os.name").startsWith("Windows"))
                "E:\$name" else "/www/wwwroot/8008/files/$name"
            runCatching {
                val url = subject.files.root.files().filter { it.isFile && (it.name == name) }.first().getUrl()!!
                SweetBoy.getFile(url, path)
            }.onFailure {
                when (it) {
                    is java.lang.NullPointerException -> subject.sendMessage("名为\"${name}\"的文件不存在!")
                    else -> throw it
                }
            }.onSuccess {
                subject.sendMessage("已上传至 http://res.seiki.fun/files/${URLUtil.encode(name)}")
            }
        }
    }

    @SubCommand
    suspend fun MemberCommandSender.upload2(index: Int) {
        subject.runCatching {
            val name = subject.files.root.files().toList()[index].name
            val path = if (System.getProperties().getProperty("os.name").startsWith("Windows"))
                "D:\\$name" else "/www/wwwroot/8008/files/$name"
            runCatching {
                val url = subject.files.root.files().toList()[index].getUrl()!!
                SweetBoy.getFile(url, path)
            }.onFailure {
                when (it) {
                    is java.lang.NullPointerException -> subject.sendMessage("名为\"${name}\"的文件不存在!")
                    else -> throw it
                }
            }.onSuccess {
                subject.sendMessage("已上传至 http://res.seiki.fun/files/${URLUtil.encode(name)}")
            }
        }
    }

}