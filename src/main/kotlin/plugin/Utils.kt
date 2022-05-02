@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.seiki.plugin

import com.huaban.analysis.jieba.JiebaSegmenter
import com.huaban.analysis.jieba.WordDictionary
import kotlinx.coroutines.TimeoutCancellationException
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.AudioSupported
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.AbstractEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.nextMessage
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.seiki.SweetBoy
import java.io.File

class TimeTickEvent(
    var bot: Bot,
    var timestamp: Long,
) : AbstractEvent()

val ownerList = arrayListOf(2630557998L, 1812691029L)

val biliUrlRegex =
    """[\s\S]*(?:(?:https?://)?(?:www\.)?bilibili\.com/video/)?([aA][vV]\d+|[bB][vV][a-zA-Z0-9]+)[\s\S]*""".toRegex()
val bili23tvRegex = """[\s\S]*((?:https?://)?(?:www\.)?b23\.tv/[a-zA-Z0-9]+)[\s\S]*""".toRegex()
val biliUserRegex = """[\s\S]*(?:https?://)?(?:space\.bilibili\.com|bilibili\.com/space)/(\d+)[\s\S]*""".toRegex()

fun MemberPermission.getName(): String = when (this) {
    MemberPermission.MEMBER -> "群员"
    MemberPermission.ADMINISTRATOR -> "管理员"
    MemberPermission.OWNER -> "群主"
}

suspend fun Contact.uploadAsImage(url: String) =
    SweetBoy.getStream(url).use { it.uploadAsImage(this@uploadAsImage) }

suspend fun Contact.uploadAsImage(file: File) =
    file.uploadAsImage(this@uploadAsImage)

suspend fun Contact.uploadAsAudio(url: String) =
    SweetBoy.getStream(url).toExternalResource().use { (this@uploadAsAudio as AudioSupported).uploadAudio(it) }

suspend fun Contact.uploadAsAudio(file: File) =
    file.toExternalResource().use { (this@uploadAsAudio as AudioSupported).uploadAudio(it) }

suspend fun <T : Contact, R> T.runCatching(block: suspend T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        buildMessageChain {
            +PlainText("Warning! ${e.javaClass.name}: ${e.message}\n")
            if (e.cause != null) +PlainText("Caused by: ${e.cause!!.javaClass.name}: ${e.cause!!.message}")
            +Image("{D3A4F304-847D-BB7B-1534-8ABFDC7575B4}.png")
        }.sendTo(this)
        Result.failure(e)
    }
}

/**
 * @author LaoLittle鸽鸽♡
 */
suspend fun MessageEvent.getOrWaitImage(): Image? =
    (message.takeIf { m -> m.contains(Image) } ?: runCatching {
        subject.sendMessage("请在30秒内发送图片...")
        nextMessage(30_000) { event -> event.message.contains(Image) }
    }.getOrElse { e ->
        when (e) {
            is TimeoutCancellationException -> {
                messageChainOf(PlainText("超时未发送!"), message.quote()).sendTo(subject)
                return null
            }
            else -> throw e
        }
    }).firstIsInstanceOrNull<Image>()

suspend fun MessageEvent.getOrWait(): MessageChain? =
    runCatching {
        this@getOrWait.nextMessage(30_000)
    }.getOrElse {
        when (it) {
            is TimeoutCancellationException -> {
                messageChainOf(PlainText("超时未发送!"), message.quote()).sendTo(subject)
                return null
            }
            else -> throw it
        }
    }

val String.consolas: String
    get() {
        val hash: HashMap<String, String> = hashMapOf(
            Pair("0", "𝟶"),
            Pair("1", "𝟷"),
            Pair("2", "𝟸"),
            Pair("3", "𝟹"),
            Pair("4", "𝟺"),
            Pair("5", "𝟻"),
            Pair("6", "𝟼"),
            Pair("7", "𝟽"),
            Pair("8", "𝟾"),
            Pair("9", "𝟿"),
            Pair("a", "𝚊"),
            Pair("b", "𝚋"),
            Pair("c", "𝚌"),
            Pair("d", "𝚍"),
            Pair("e", "𝚎"),
            Pair("f", "𝚏"),
            Pair("g", "𝚐"),
            Pair("h", "𝚑"),
            Pair("i", "𝚒"),
            Pair("j", "𝚓"),
            Pair("k", "𝚔"),
            Pair("l", "𝚕"),
            Pair("m", "𝚖"),
            Pair("n", "𝚗"),
            Pair("o", "𝚘"),
            Pair("p", "𝚙"),
            Pair("q", "𝚚"),
            Pair("r", "𝚛"),
            Pair("s", "𝚜"),
            Pair("t", "𝚝"),
            Pair("u", "𝚞"),
            Pair("v", "𝚟"),
            Pair("w", "𝚠"),
            Pair("x", "𝚡"),
            Pair("y", "𝚢"),
            Pair("z", "𝚣"),
            Pair("A", "𝙰"),
            Pair("B", "𝙱"),
            Pair("C", "𝙲"),
            Pair("D", "𝙳"),
            Pair("E", "𝙴"),
            Pair("F", "𝙵"),
            Pair("G", "𝙶"),
            Pair("H", "𝙷"),
            Pair("I", "𝙸"),
            Pair("J", "𝙹"),
            Pair("K", "𝙺"),
            Pair("L", "𝙻"),
            Pair("M", "𝙼"),
            Pair("N", "𝙽"),
            Pair("O", "𝙾"),
            Pair("P", "𝙿"),
            Pair("Q", "𝚀"),
            Pair("R", "𝚁"),
            Pair("S", "𝚂"),
            Pair("T", "𝚃"),
            Pair("U", "𝚄"),
            Pair("V", "𝚅"),
            Pair("W", "𝚆"),
            Pair("X", "𝚇"),
            Pair("Y", "𝚈"),
            Pair("Z", "𝚉")
        )
        var str = ""
        this.forEach {
            str += if (it.toString() in hash.keys) hash[it.toString()] else it.toString()
        }
        return str
    }

/**
 * @author LaoLittle鸽鸽♡
 */
object YinglishUtil {
    val String.yinglish get() = this.chs2yin(50)
    fun String.chs2yin(yingLevel: Int): String {
        val b = JiebaSegmenter().process(this, JiebaSegmenter.SegMode.SEARCH)
        var yinglish = ""
        b.forEach { keyWord ->
            val part = WordDictionary.getInstance().parts[keyWord.word]
            val chars = keyWord.word.toCharArray()
            yinglish += getYinglishNode(chars, part, yingLevel)
        }
        return yinglish
    }

    private fun getYinglishNode(chars: CharArray, part: String?, yingLevel: Int): String {
        val randomOneTen = { (1..100).random() }
        var pon = ""
        if (randomOneTen() > yingLevel)
            return String(chars)
        when (chars[0].toString()) {
            in arrayOf("!", "！", "？", "?", "—") -> return "❤"
            in arrayOf(",", "，", "。") -> return "…"
        }
        if (chars.size > 1 && randomOneTen() > 50)
            return "${chars[0]}…${String(chars)}"
        else if (part == "n" && randomOneTen() > 50) {
            repeat(chars.count()) { pon += "〇" }
            return pon
        }
        return "…${String(chars)}"
    }
}
