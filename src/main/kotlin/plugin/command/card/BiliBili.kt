package org.seiki.plugin.command.card

import com.google.gson.Gson
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.messageChainOf
import org.seiki.SweetBoy
import org.seiki.SweetBoy.matchRegexOrFail
import org.seiki.SweetBoy.transToNumString
import org.seiki.plugin.uploadAsImage

suspend fun Contact.bili(id: String): MessageChain {
    val isBv = if ("""[bB][vV][a-zA-Z0-9]+""".toRegex().matches(id)) true
    else if ("""[aA][vV]\d+""".toRegex().matches(id)) false
    else throw NoSuchElementException("AV/BV号格式错误")
    val id2 = if (isBv)
        id.matchRegexOrFail("""[bB][vV]([a-zA-Z0-9]+)""".toRegex())[1]
    else
        id.matchRegexOrFail("""[aA][vV](\d+)""".toRegex())[1]
    val result1 =
        SweetBoy.get("https://api.bilibili.com/x/web-interface/view?${if (isBv) "bv" else "a"}id=$id2")
    val json = Gson().fromJson(result1.body!!.string(), BiliApi::class.java)
    val result2 = json?.let { bili ->
        return@let if (bili.code == 0) {
            buildMessageChain {
                +this@bili.uploadAsImage(bili.data.pic)
                +PlainText(bili.data.title + "\n")
                +PlainText("https://www.bilibili.com/video/${(if (isBv) "av${bili.data.aid}" else bili.data.bvid)}/\n")
                +PlainText("观看:${bili.data.stat.view.transToNumString(1)} ")
                +PlainText("点赞:${bili.data.stat.like.transToNumString(1)} ")
                +PlainText("弹幕:${bili.data.stat.danmaku.transToNumString(1)} ")
                +PlainText("投币:${bili.data.stat.coin.transToNumString(1)} ")
                +PlainText("收藏:${bili.data.stat.favorite.transToNumString(1)} ")
                +PlainText("分享:${bili.data.stat.share.transToNumString(1)} ")
                +PlainText("评论:${bili.data.stat.reply.transToNumString(1)} ")
                +PlainText((if (bili.data.copyright == 1) "自制" else "转载") + "\n")
                +PlainText("UP主: ${bili.data.owner.name} UID:${bili.data.owner.mid}\n")
                +PlainText(bili.data.desc)
            }
        } else messageChainOf(PlainText(bili.message))
    } ?: messageChainOf(PlainText(""))
    return result2
}

data class BiliLight(
    val app: String,
    val config: Config,
    val desc: String,
    val meta: Meta,
    val prompt: String,
    val ver: String,
    val view: String
)

data class Config(
    val autoSize: Int,
    val ctime: Int,
    val forward: Int,
    val height: Int,
    val token: String,
    val type: String,
    val width: Int
)

data class Meta(
    val detail_1: Detail1
)

data class Detail1(
    val appType: Int,
    val appid: String,
    val desc: String,
    val gamePoints: String,
    val gamePointsUrl: String,
    val host: Host,
    val icon: String,
    val preview: String,
    val qqdocurl: String,
    val scene: Int,
    val shareTemplateData: ShareTemplateData,
    val shareTemplateId: String,
    val showLittleTail: String,
    val title: String,
    val url: String
)

data class Host(
    val nick: String,
    val uin: Long
)

class ShareTemplateData

data class BiliApi(
    val code: Int,
    val `data`: Data,
    val message: String,
    val ttl: Int
)

data class Data(
    val aid: Int,
    val bvid: String,
    val cid: Int,
    val copyright: Int,
    val ctime: Int,
    val desc: String,
    val desc_v2: List<DescV2>,
    val dimension: Dimension,
    val duration: Int,
    val `dynamic`: String,
    val honor_reply: HonorReply,
    val is_season_display: Boolean,
    val no_cache: Boolean,
    val owner: Owner,
    val pages: List<Page>,
    val pic: String,
    val pubdate: Int,
    val rights: Rights,
    val stat: Stat,
    val state: Int,
    val subtitle: Subtitle,
    val tid: Int,
    val title: String,
    val tname: String,
    val user_garb: UserGarb,
    val videos: Int
)

data class DescV2(
    val biz_id: Int,
    val raw_text: String,
    val type: Int
)

data class Dimension(
    val height: Int,
    val rotate: Int,
    val width: Int
)

data class HonorReply(
    val honor: List<Honor>
)

data class Owner(
    val face: String,
    val mid: Int,
    val name: String
)

data class Page(
    val cid: Int,
    val dimension: DimensionX,
    val duration: Int,
    val from: String,
    val page: Int,
    val part: String,
    val vid: String,
    val weblink: String
)

data class Rights(
    val autoplay: Int,
    val bp: Int,
    val clean_mode: Int,
    val download: Int,
    val elec: Int,
    val hd5: Int,
    val is_360: Int,
    val is_cooperation: Int,
    val is_stein_gate: Int,
    val movie: Int,
    val no_background: Int,
    val no_reprint: Int,
    val no_share: Int,
    val pay: Int,
    val ugc_pay: Int,
    val ugc_pay_preview: Int
)

data class Stat(
    val aid: Int,
    val argue_msg: String,
    val coin: Int,
    val danmaku: Int,
    val dislike: Int,
    val evaluation: String,
    val favorite: Int,
    val his_rank: Int,
    val like: Int,
    val now_rank: Int,
    val reply: Int,
    val share: Int,
    val view: Int
)

data class Subtitle(
    val allow_submit: Boolean,
    val list: List<Any>
)

data class UserGarb(
    val url_image_ani_cut: String
)

data class Honor(
    val aid: Int,
    val desc: String,
    val type: Int,
    val weekly_recommend_num: Int
)

data class DimensionX(
    val height: Int,
    val rotate: Int,
    val width: Int
)

data class BiliApp(
    val app: String,
    val config: Config2,
    val desc: String,
    val meta: Meta2,
    val prompt: String,
    val ver: String,
    val view: String
)

data class Config2(
    val ctime: Int,
    val forward: Boolean,
    val token: String,
    val type: String
)

data class Meta2(
    val news: News
)

data class News(
    val action: String,
    val android_pkg_name: String,
    val app_type: Int,
    val appid: Int,
    val ctime: Int,
    val desc: String,
    val jumpUrl: String,
    val preview: String,
    val source_icon: String,
    val source_url: String,
    val tag: String,
    val title: String,
    val uin: Int
)