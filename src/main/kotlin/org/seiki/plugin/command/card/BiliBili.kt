package org.seiki.plugin.command.card

import com.google.gson.Gson
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.messageChainOf
import net.mamoe.mirai.utils.info
import org.seiki.SweetBoy
import org.seiki.SweetBoy.matchRegexOrFail
import org.seiki.SweetBoy.transToNum
import org.seiki.SweetBoy.transToTime
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching
import org.seiki.plugin.uploadAsImage
import java.awt.Dimension

suspend fun Contact.biliVideo(id: String): MessageChain? =
    this.runCatching {
        SeikiMain.logger.info { id }
        val isBv = if ("""[bB][vV][a-zA-Z0-9]+""".toRegex().matches(id)) true
        else if ("""[aA][vV]\d+""".toRegex().matches(id)) false
        else throw NoSuchElementException("AV/BV号格式错误")
        val id2 = if (isBv)
            id.matchRegexOrFail("""[bB][vV]([a-zA-Z0-9]+)""".toRegex())[1]
        else
            id.matchRegexOrFail("""[aA][vV](\d+)""".toRegex())[1]
        SeikiMain.logger.info { id2 }
        val rel1 =
            SweetBoy.get("http://api.bilibili.com/x/web-interface/view?${if (isBv) "bv" else "a"}id=$id2").use {
                it.body!!.string()
            }
        val rel2 =
            SweetBoy.get("http://api.bilibili.com/x/web-interface/search/all/v2?keyword=${if (isBv) "BV" else "av"}$id2")
                .use {
                    it.body!!.string()
                }
        val gson = Gson()
        val json1 = gson.fromJson(rel1, BiliVideoApi::class.java)
        val json2 = gson.fromJson(rel2, BiliSearchApi::class.java)
        val data1 = json1.data
        val data2 = json2.data.result.last().data.first()
        return@runCatching (if (json1.code == 0) buildMessageChain {
            +this@biliVideo.uploadAsImage(json1.data.pic)
            +PlainText(json1.data.title + "\n")
            +PlainText("https://www.bilibili.com/video/${json1.data.bvid}/\n")
            +PlainText("${data1.bvid} - av${data1.aid}\n")
            +PlainText("${data2.typename}-${if (data1.copyright == 1) "自制" else "转载"}(${data2.tag})\n")
            +PlainText("📅${(data2.pubdate * 1000L).transToTime()} 🕑${data2.duration}\n")
            +PlainText("▶${data1.stat.view.transToNum(1)} ")
            +PlainText("🈂${data1.stat.danmaku.transToNum(1)}\n")
            +PlainText("👍${data1.stat.like.transToNum(1)} ")
            +PlainText("⭐${data1.stat.favorite.transToNum(1)} ")
            +PlainText("💰${data1.stat.coin.transToNum(1)} ")
            +PlainText("↗${data1.stat.share.transToNum(1)}\n")
            +PlainText("💬${data1.stat.reply.transToNum(1)} ")
            +PlainText("🆙${data1.owner.name} (${json1.data.owner.mid})\n")
            +PlainText(json1.data.desc)
        } else messageChainOf(PlainText(json1.message)))
    }.getOrNull()

suspend fun Contact.biliUser(id: Long): MessageChain? =
    this.runCatching {
        val gson = Gson()
        val rel1 = SweetBoy.get("https://api.bilibili.com/x/space/acc/info?mid=$id").use { it.body!!.string() }
        val rel2 = SweetBoy.get("https://api.bilibili.com/x/relation/stat?vmid=$id").use { it.body!!.string() }
        val rel3 = SweetBoy.get("https://api.bilibili.com/x/space/arc/search?mid=$id").use { it.body!!.string() }
        val json1 = gson.fromJson(rel1, BiliUserApi::class.java)
        val json2 = gson.fromJson(rel2, BiliUserStatApi::class.java)
        val json3 = gson.fromJson(rel3, BiliUserSearchApi::class.java)
        val data1 = json1.data
        val data2 = json2.data
        val data3 = json3.data
        return@runCatching (
                if (json1.code == 0 && json2.code == 0 && json3.code == 0) buildMessageChain {
                    +this@biliUser.uploadAsImage(data1.face)
                    +PlainText("${data1.name} ${data1.sex} LV${data1.level} ")
                    +PlainText("性别:${data1.sex}\n${data1.sign}\n")
                    +PlainText("关注:${data2.following.transToNum()} ")
                    +PlainText("粉丝:${data2.follower.transToNum()} ")
                    +PlainText("总视频数:${data3.list.vlist.size}")
                } else messageChainOf(PlainText("1.${json1.message}\n2.${json2.message}\n3.${json3.message}"))
                )
    }.getOrNull()

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

data class BiliVideoApi(
    val code: Int,
    val data: Data,
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
    val dynamic: String,
    val honor_reply: HonorReply,
    val is_season_display: Boolean,
    val no_cache: Boolean,
    val owner: Owner,
    val pages: List<Page1>,
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
    val mid: Long,
    val name: String
)

data class Page1(
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
    val uin: Long
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
    val mid: Long,
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

data class BiliUserApi(
    val code: Int,
    val `data`: Data3,
    val message: String,
    val ttl: Int
)

data class Data3(
    val birthday: String,
    val coins: Int,
    val face: String,
    val face_nft: Int,
    val fans_badge: Boolean,
    val fans_medal: FansMedal,
    val is_followed: Boolean,
    val is_senior_member: Int,
    val jointime: Int,
    val level: Int,
    val live_room: LiveRoom,
    val mid: Int,
    val moral: Int,
    val name: String,
    val nameplate: Nameplate,
    val official: Official,
    val pendant: Pendant,
    val profession: Profession,
    val rank: Int,
    val school: School,
    val series: Series,
    val sex: String,
    val sign: String,
    val silence: Int,
    val sys_notice: SysNotice,
    val tags: Any,
    val theme: Theme,
    val top_photo: String,
    val user_honour_info: UserHonourInfo,
    val vip: Vip
)

data class FansMedal(
    val medal: Any,
    val show: Boolean,
    val wear: Boolean
)

data class LiveRoom(
    val broadcast_type: Int,
    val cover: String,
    val liveStatus: Int,
    val roomStatus: Int,
    val roomid: Int,
    val roundStatus: Int,
    val title: String,
    val url: String,
    val watched_show: WatchedShow
)

data class Nameplate(
    val condition: String,
    val image: String,
    val image_small: String,
    val level: String,
    val name: String,
    val nid: Int
)

data class Official(
    val desc: String,
    val role: Int,
    val title: String,
    val type: Int
)

data class Pendant(
    val expire: Int,
    val image: String,
    val image_enhance: String,
    val image_enhance_frame: String,
    val name: String,
    val pid: Int
)

data class Profession(
    val department: String,
    val is_show: Int,
    val name: String,
    val title: String
)

data class School(
    val name: String
)

data class Series(
    val show_upgrade_window: Boolean,
    val user_upgrade_status: Int
)

class SysNotice

class Theme

data class UserHonourInfo(
    val colour: Any,
    val mid: Int,
    val tags: List<Any>
)

data class Vip(
    val avatar_subscript: Int,
    val avatar_subscript_url: String,
    val due_date: Long,
    val label: Label,
    val nickname_color: String,
    val role: Int,
    val status: Int,
    val theme_type: Int,
    val type: Int,
    val vip_pay_type: Int
)

data class WatchedShow(
    val icon: String,
    val icon_location: String,
    val icon_web: String,
    val num: Int,
    val switch: Boolean,
    val text_large: String,
    val text_small: String
)

data class Label(
    val bg_color: String,
    val bg_style: Int,
    val border_color: String,
    val label_theme: String,
    val path: String,
    val text: String,
    val text_color: String
)

data class BiliUserStatApi(
    val code: Int,
    val `data`: Data4,
    val message: String,
    val ttl: Int
)

data class Data4(
    val black: Int,
    val follower: Int,
    val following: Int,
    val mid: Int,
    val whisper: Int
)

data class BiliUserSearchApi(
    val code: Int,
    val `data`: Data5,
    val message: String,
    val ttl: Int
)

data class Data5(
    val episodic_button: EpisodicButton,
    val list: List1,
    val page: Page2
)

data class EpisodicButton(
    val text: String,
    val uri: String
)

data class List1(
    val vlist: List<Vlist>
)

data class Page2(
    val count: Int,
    val pn: Int,
    val ps: Int
)

data class Vlist(
    val aid: Int,
    val author: String,
    val bvid: String,
    val comment: Int,
    val copyright: String,
    val created: Int,
    val description: String,
    val hide_click: Boolean,
    val is_live_playback: Int,
    val is_pay: Int,
    val is_steins_gate: Int,
    val is_union_video: Int,
    val length: String,
    val mid: Int,
    val pic: String,
    val play: Int,
    val review: Int,
    val subtitle: String,
    val title: String,
    val typeid: Int,
    val video_review: Int
)