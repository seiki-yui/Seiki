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
import org.seiki.SweetBoy.transToTime
import org.seiki.plugin.uploadAsImage
import java.awt.Dimension

suspend fun Contact.bili(id: String): MessageChain {
    val isBv = if ("""[bB][vV][a-zA-Z0-9]+""".toRegex().matches(id)) true
    else if ("""[aA][vV]\d+""".toRegex().matches(id)) false
    else throw NoSuchElementException("AV/BV号格式错误")
    val id2 = if (isBv)
        id.matchRegexOrFail("""[bB][vV]([a-zA-Z0-9]+)""".toRegex())[1]
    else
        id.matchRegexOrFail("""[aA][vV](\d+)""".toRegex())[1]
    val rel1 =
        SweetBoy.get("http://api.bilibili.com/x/web-interface/view?${if (isBv) "bv" else "a"}id=$id2")
    val rel2 =
        SweetBoy.get("http://api.bilibili.com/x/web-interface/search/all/v2?keyword=${if (isBv) "BV" else "av"}$id2")
    val json1 = Gson().fromJson(rel1.body!!.string(), BiliApi::class.java)
    val json2 = Gson().fromJson(rel2.body!!.string(), BiliSearchApi::class.java)
    val data1 = json1.data
    val data2 = json2.data.result[json2.data.result.size - 1].data[1]
    return (
            if (json1.code == 0) {
                buildMessageChain {
                    +this@bili.uploadAsImage(json1.data.pic)
                    +PlainText(json1.data.title + "\n")
                    +PlainText("https://www.bilibili.com/video/${json1.data.bvid}/\n")
                    +PlainText("${data1.bvid} - av${data1.aid}\n")
                    +PlainText("${data2.typename}-${if (data1.copyright == 1) "自制" else "转载"}(${data2.tag})\n\r")
                    +PlainText("📅${(data2.pubdate * 1000L).transToTime()} 🕑${data2.duration}\n\r")
                    +PlainText("▶${data1.stat.view.transToNumString(1)} ")
                    +PlainText("🈂${data1.stat.danmaku.transToNumString(1)}\n\r")
                    +PlainText("👍${data1.stat.like.transToNumString(1)} ")
                    +PlainText("⭐${data1.stat.favorite.transToNumString(1)} ")
                    +PlainText("💰${data1.stat.coin.transToNumString(1)} ")
                    +PlainText("↗${data1.stat.share.transToNumString(1)}\n\r")
                    +PlainText("💬${data1.stat.reply.transToNumString(1)} ")
                    +PlainText("🆙${data1.owner.name} (${json1.data.owner.mid})\n\r")
                    +PlainText(json1.data.desc)
                }
            } else messageChainOf(PlainText(json1.message)))
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

data class BiliSearchApi(
    val code: Int,
    val `data`: Data2,
    val message: String,
    val ttl: Int
)

data class Data2(
    val result: List<Result>
)

data class Result(
    val `data`: List<DataX>,
    val result_type: String
)

data class DataX(
    val aid: Int,
    val arcrank: String,
    val arcurl: String,
    val author: String,
    val badgepay: Boolean,
    val bvid: String,
    val corner: String,
    val cover: String,
    val desc: String,
    val description: String,
    val duration: String,
    val favorites: Int,
    val hit_columns: List<Any>,
    val id: Int,
    val is_pay: Int,
    val is_union_video: Int,
    val like: Int,
    val mid: Int,
    val new_rec_tags: List<Any>,
    val pic: String,
    val play: Int,
    val pubdate: Int,
    val rank_score: Int,
    val rec_reason: String,
    val rec_tags: Any,
    val review: Int,
    val senddate: Int,
    val tag: String,
    val title: String,
    val type: String,
    val typeid: String,
    val typename: String,
    val upic: String,
    val url: String,
    val video_review: Int,
    val view_type: String
)