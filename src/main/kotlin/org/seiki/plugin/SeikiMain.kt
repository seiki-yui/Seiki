package org.seiki.plugin

import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.Command
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CommandSender.Companion.asCommandSender
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.LightApp
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.Fonts
import org.seiki.SweetBoy
import org.seiki.SweetBoy.matchRegexOrFail
import org.seiki.SweetBoy.transToTime
import org.seiki.plugin.command.audio.Audio
import org.seiki.plugin.command.audio.Say
import org.seiki.plugin.command.card.*
import org.seiki.plugin.command.image.*
import org.seiki.plugin.command.plain.*

object SeikiMain : KotlinPlugin(
    JvmPluginDescription(
        id = "org.seiki.main",
        version = "1.0-SNAPSHOT",
        name = "Seiki Main"
    ) {
        author("xiao-zheng233")
    }
) {

    val audioFolder = dataFolder.resolve("audio")
    val tempFolder = dataFolder.resolve("temp")

    private const val useTimeTickEvent = true

    private lateinit var jobTimeTick: Job

    @OptIn(ExperimentalCommandDescriptors::class, ConsoleExperimentalApi::class)
    override fun onEnable() {
        logger.info { "Seiki Main loaded" }
        logger.info { Fonts["Consolas"].toString() }
        if (!System.getProperties().getProperty("os.name").startsWith("Windows")) {
            System.setProperty("java.awt.headless", "true")
        }
        val commandList: List<Command> = listOf(
            Ping,
            Gpbt,
            Form,
            Two,
            Dazs,
            Kfc,
            Diana,
            Yiyan,
            Nbnhhsh,
            Baike,
            Bottle,
            Fencing,
            Moyu,
            Yinglish,
            Diu,
            Tian,
            Bishi,
            Pa,
            Zan,
            Love,
            Qian,
            Draw,
            Osu,
            PatPat,
            PronHub,
            FiveK,
            BlackWhite,
            Zero,
            Setu,
            Aotu,
            Cosplay,
            Audio,
            Say,
            BuildForward
        )
        commandList.forEach {
            CommandManager.registerCommand(it)
        }
        val eventChannel = globalEventChannel()
        eventChannel.subscribeAlways<MessageEvent> {
            message.forEach {
                if (it is LightApp) {
                    val gson = Gson()
                    // 1105517988 哔哩哔哩HD 类XML的JSON卡片 MiraiApp
                    // 100951776 哔哩哔哩 类XML的JSON卡片 MiraiApp
                    // 1109937557 哔哩哔哩小程序 JSON卡片 MiraiLightApp
                    runCatching {
                        val json = gson.fromJson(it.content, BiliLight::class.java)
                        if (json.meta.detail_1.appid == "1109937557") {
                            val url = SweetBoy.get(
                                json.meta.detail_1.qqdocurl.substringBefore('?')
                            ).use { r -> r.request.url.toString() }
                            val bv = url.matchRegexOrFail(biliUrlRegex)[1]
                            subject.biliVideo(bv)?.sendTo(subject)
                        } else logger.info { "BiliLight出了问题" }
                    }.onFailure { logger.info { "不是B站小程序" } }
                    runCatching {
                        val json = gson.fromJson(it.content, BiliApp::class.java)
                        if (json.meta.news.appid == 1105517988 || json.meta.news.appid == 100951776) {
                            val url = SweetBoy.get(json.meta.news.jumpUrl).use { r -> r.request.url.toString() }
                            logger.info { url }
                            val bv = url.matchRegexOrFail(biliUrlRegex)[1]
                            logger.info { bv }
                            subject.biliVideo(bv)?.sendTo(subject)
                        } else logger.info { "BiliApp AppID非1105517988 或 100951776" }
                    }.onFailure { logger.info { "不是B站类XML的JSON卡片" } }
                }
            }
        }
        eventChannel.subscribeAlways<TimeTickEvent> {
            if (this.timestamp.transToTime("HH:mm:ss") == "11:45:13") {
                bot.groups.forEach {
                    it.sendMessage("${"Seiki".consolas}报时!\n现在是11:45:14")
                }
            }
        }
        eventChannel.subscribeMessages {
            biliUrlRegex findingReply {
                subject.biliVideo(it.groupValues[1])
            }
            bili23tvRegex findingReply {
                subject.biliVideo(
                    SweetBoy.get(it.groupValues[1]).request.url.toString().matchRegexOrFail(biliUrlRegex)[1]
                )
            }
            biliUserRegex findingReply {
                subject.biliUser(it.groupValues[1].toLong())
            }
            """#5k<([\s\S]*)><([\s\S]*)>""".toRegex() finding {
                val (top, bottom) = it.destructured
                with(FiveK) {
                    sender.asCommandSender(isTemp = false).handle(top, bottom)
                }
            }
            """#osu<([\s\S]+)>""".toRegex() finding {
                val (text) = it.destructured
                with(Osu) {
                    sender.asCommandSender(isTemp = false).handle(text)
                }
            }
            """#consolas ([\s\S]+)""".toRegex() findingReply { it.groupValues[1].consolas }

            """#?(help|帮助|菜单)""".toRegex() findingReply { "http://139.224.249.110/wiki/seiki-bot/index.html#%E4%BD%BF%E7%94%A8" }

            """#throw""".toRegex() finding {
                subject.runCatching { throw Throwable("www") }
            }
            """#exception""".toRegex() finding {
                subject.runCatching { throw Exception("www") }
            }
            """#error""".toRegex() finding {
                subject.runCatching { throw Error("www") }
            }
            """#发图 ([\s\S]+)""".toRegex() findingReply {
                subject.runCatching { Image(it.groupValues[1]) }
            }
            """#get ([\s\S]+)""".toRegex() findingReply {
                subject.runCatching {
                    SweetBoy.get(it.groupValues[1]).use { r -> r.body!!.string() }
                }.getOrNull()
            }
            """#post ([\s\S]+)""".toRegex() findingReply {
                subject.runCatching {
                    SweetBoy.post(it.groupValues[1]).use { r -> r.body!!.string() }
                }.getOrNull()
            }
        }
        eventChannel.subscribeAlways<BotOnlineEvent> {
            jobTimeTick = if (useTimeTickEvent) launch {
                while (true) {
                    TimeTickEvent(bot, System.currentTimeMillis()).broadcast()
                    delay(999L)
                }
            } else launch {}
        } // bot上线
        eventChannel.subscribeAlways<BotInvitedJoinGroupRequestEvent> {
            if (invitorId in ownerList) launch {
                delay(1000L)
                accept()
            }
        } // bot拉群
        eventChannel.subscribeAlways<BotJoinGroupEvent> {
            launch {
                delay(100L)
                group.sendMessage("这里是${"Seiki".consolas}.发送\"# help\"来康教程.")
            }
        } // bot进群
        eventChannel.subscribeAlways<NudgeEvent> {
            if (target.id == bot.id && from.id != bot.id) launch {
                delay(100L)
                from.nudge().sendTo(subject)
            }
        } // 戳一戳
        eventChannel.subscribeAlways<MemberMuteEvent> {
            launch {
                delay(100L)
                group.sendMessage(
                    if (operator == null) "已口球${member.nameCardOrNick}(${member.id})"
                    else "${member.nameCardOrNick}(${member.id})被${operatorOrBot.nameCardOrNick}(${operatorOrBot.id})塞了口球"
                )
            }
        } // 禁言
        eventChannel.subscribeAlways<MemberUnmuteEvent> {
            launch {
                delay(100L)
                group.sendMessage(
                    if (operator == null) "已解禁${member.nameCardOrNick}(${member.id})"
                    else "${member.nameCardOrNick}(${member.id})的口球被${operatorOrBot.nameCardOrNick}(${operatorOrBot.id})取了下来"
                )
            }
        } // 解禁
        eventChannel.subscribeAlways<MemberJoinEvent.Active> {
            launch {
                delay(100L)
                group.sendMessage("欢迎${member.nameCardOrNick}(${member.id})的加入")
            }
        } // 进群
        eventChannel.subscribeAlways<MemberJoinEvent.Invite> {
            launch {
                delay(100L)
                group.sendMessage("${invitor.nameCardOrNick}(${invitor.id})邀请${member.nameCardOrNick}(${member.id})加入了本群")
            }
        } // 拉群
        eventChannel.subscribeAlways<MemberLeaveEvent.Quit> {
            launch {
                delay(100L)
                group.sendMessage("${member.nameCardOrNick}(${member.id})逃跑了")
            }
        } // 退群
        eventChannel.subscribeAlways<MemberLeaveEvent.Kick> {
            launch {
                delay(100L)
                group.sendMessage(
                    if (operator == null) "已飞机${member.nameCardOrNick}(${member.id})"
                    else "${operatorOrBot.nameCardOrNick}(${operatorOrBot.id})飞机了${member.nameCardOrNick}(${member.id})"
                )
            }
        } // 踢群
        eventChannel.subscribeAlways<MemberPermissionChangeEvent> {
            launch {
                delay(100L)
                group.sendMessage("${member.nameCardOrNick}(${member.id})从${origin.getName()}便乘了${new.getName()}")
            }
        } // 管理权限改变
    }

    override fun onDisable() {
        jobTimeTick.cancel()
        tempFolder.walk().filter { it.path.toString().endsWith("_pat.gif") }.forEach { it.delete() }
        super.onDisable()
    }
}