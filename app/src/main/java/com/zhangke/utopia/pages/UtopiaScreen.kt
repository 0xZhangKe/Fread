package com.zhangke.utopia.pages

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.utopia.rss.internal.rss.RssParser
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

class UtopiaScreen : Screen {

    private val rssText = """
        <?xml version="1.0" encoding="UTF-8"?>
        <rss version="2.0" xmlns:webfeeds="http://webfeeds.org/rss/1.0" xmlns:media="http://search.yahoo.com/mrss/">
          <channel>
            <title>nova</title>
            <description>@nova@m.cmx.im 的公开嘟文</description>
            <link>https://m.cmx.im/@nova</link>
            <image>
              <url>https://media.cmx.edu.kg/accounts/avatars/000/079/074/original/c4beaaa4e61512f0.png</url>
              <title>nova</title>
              <link>https://m.cmx.im/@nova</link>
            </image>
            <lastBuildDate>Sat, 17 Feb 2024 23:51:02 +0000</lastBuildDate>
            <webfeeds:icon>https://media.cmx.edu.kg/accounts/avatars/000/079/074/original/c4beaaa4e61512f0.png</webfeeds:icon>
            <generator>Mastodon v4.2.7</generator>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111949503705606861</guid>
              <link>https://m.cmx.im/@nova/111949503705606861</link>
              <pubDate>Sat, 17 Feb 2024 23:51:02 +0000</pubDate>
              <description>&lt;p&gt;早上发现自己一直在梦里哭，哭到连眼罩都湿了。&lt;/p&gt;&lt;p&gt;梦里一直在重现上海封城的片段，各种离奇的片段组合到一起。然后我就在那扎扎实实地哭了。&lt;/p&gt;&lt;p&gt;尽管我不曾亲身经历上海封城，这些居然在近两年后以噩梦形式浮现在我的脑海里。很难想象那两千多万亲历者经历了什么。&lt;/p&gt;</description>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111944986859313818</guid>
              <link>https://m.cmx.im/@nova/111944986859313818</link>
              <pubDate>Sat, 17 Feb 2024 04:42:21 +0000</pubDate>
              <description>&lt;p&gt;&lt;span class="h-card" translate="no"&gt;&lt;a href="https://ovo.st/club/board" class="u-url mention"&gt;@&lt;span&gt;board&lt;/span&gt;&lt;/a&gt;&lt;/span&gt; &lt;/p&gt;&lt;p&gt;DPS 周刊 136 - 爱的教育与恨的教育&lt;/p&gt;&lt;p&gt;本周翻完了两本书，一本是冯克写的《毛泽东的大饥荒》，一本是 Erich Fromm 的 The Art of Loving。两本形成鲜明的对比：前者是恨的教育，后者是爱的教育。&lt;/p&gt;&lt;p&gt;其实去年就把《毛泽东的大饥荒》翻了一大半，看得实在难过就丢在一边了。这回下了一大把决心才把剩余部分读完。&lt;/p&gt;&lt;p&gt;冯克花了很大力气整理了各种公开档案，才写成这本书。这些档案恐怕只是冰山一角，真实的情况恐怕比这些档案更加恐怖。&lt;/p&gt;&lt;p&gt;反观 The Art of Loving，完全就是一本爱的教育。在这本书里，爱是广义上的爱，并不局限在亲密关系中的爱。&lt;/p&gt;&lt;p&gt;&lt;a href="https://letters.acacess.com/weekly-136/" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class="ellipsis"&gt;letters.acacess.com/weekly-136&lt;/span&gt;&lt;span class="invisible"&gt;/&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111938891125364172</guid>
              <link>https://m.cmx.im/@nova/111938891125364172</link>
              <pubDate>Fri, 16 Feb 2024 02:52:07 +0000</pubDate>
              <description>&lt;p&gt;&lt;span class="h-card" translate="no"&gt;&lt;a href="https://ovo.st/club/board" class="u-url mention"&gt;@&lt;span&gt;board&lt;/span&gt;&lt;/a&gt;&lt;/span&gt; &lt;/p&gt;&lt;p&gt;昨天了解了一下新加坡公司的税务知识，简单分享一下：&lt;/p&gt;&lt;p&gt;在新加坡注册的公司只要缴两种税：一种是 GST，税率为9%，一种是企业所得税 Corporate Income Tax Rate，税率为17%。&lt;/p&gt;&lt;p&gt;前者在发生任何购买行为时都会产生。比如一家新加坡注册公司从中国进口了货物，就需要缴纳 GST。当它在新加坡出售了这些货物之后，替新加坡政府从消费者代征 GST。可以用来相抵。举个例子，比如进口了价值一万新币的货物，清关时就缴纳了900新币的 GST。当这些货物以30000新币出售给消费者，那么需要向消费者代征2700新币的 GST。这时只需向政府缴纳1800的GST，而不是2700。&lt;/p&gt;&lt;p&gt;全文参见：&lt;/p&gt;&lt;p&gt;&lt;a href="https://t.me/pillarsalt/531" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class=""&gt;t.me/pillarsalt/531&lt;/span&gt;&lt;span class="invisible"&gt;&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111938270628753868</guid>
              <link>https://m.cmx.im/@nova/111938270628753868</link>
              <pubDate>Fri, 16 Feb 2024 00:14:19 +0000</pubDate>
              <description>&lt;p&gt;Daily Productive Sharing 915 - Letter of 2022&lt;/p&gt;&lt;p&gt;这是 Dan Wang 写的2022年度之信。尽管这荒谬的三年已经过去了许久，但是读到这些还是难免让人难过：&lt;/p&gt;&lt;p&gt;	1	他在上海宣布即将封城后，立马买了机票逃到了云南。然后在云南逍遥了两个多月；&lt;/p&gt;&lt;p&gt;	2	大理比西双版纳凉快，又比香格里拉多点阳光；所以被人戏称为“大理福尼亚”，因为和 Califonia 只差一个字母；&lt;/p&gt;&lt;p&gt;	3	高山让云南难以发展工业，但业因此让它成为了避世之地；&lt;/p&gt;&lt;p&gt;	4	上海的老人戏言道，封城还不是最惨的，毕竟都经历过十年文革。封城让大家提前体验了“共同富裕“；&lt;/p&gt;&lt;p&gt;	5	上海封城带来最大的挑战是心理上的，因为谁都不知道晒时候会结束；&lt;/p&gt;&lt;p&gt;	6	上海民众经历了两次短缺：春季的食物短缺和冬季的退烧药短缺；而退烧药的短缺非常荒谬 -- 一个主要症状是发烧的大流行病的前提下，退烧药居然被限制购买，所以药房没有备货，药厂不再生产；&lt;/p&gt;&lt;p&gt;	7	12月7号前后，所有的喉舌都是两面派，唯独一个人从来不发声，明明都是他的责任，他却什么都不用负责；&lt;/p&gt;&lt;p&gt;	8	在过去的七十多年里，每一次折腾都是人祸。&lt;/p&gt;&lt;p&gt;&lt;a href="https://letters.acacess.com/daily-productive-sharing-915/" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class="ellipsis"&gt;letters.acacess.com/daily-prod&lt;/span&gt;&lt;span class="invisible"&gt;uctive-sharing-915/&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/938/270/488/646/976/original/f3d7c5cb67b67ea3.jpeg" type="image/jpeg" fileSize="458863" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111935539271388343</guid>
              <link>https://m.cmx.im/@nova/111935539271388343</link>
              <pubDate>Thu, 15 Feb 2024 12:39:42 +0000</pubDate>
              <description>&lt;p&gt;&lt;span class="h-card" translate="no"&gt;&lt;a href="https://ovo.st/club/runrunrun" class="u-url mention"&gt;@&lt;span&gt;runrunrun&lt;/span&gt;&lt;/a&gt;&lt;/span&gt; &lt;span class="h-card" translate="no"&gt;&lt;a href="https://ovo.st/club/board" class="u-url mention"&gt;@&lt;span&gt;board&lt;/span&gt;&lt;/a&gt;&lt;/span&gt;&lt;/p&gt;&lt;p&gt;Exodus 3.31 - 以创业的心态来准备移民&lt;/p&gt;&lt;p&gt;最近和朋友聊起去年转行做品牌出海的经历。我在这份工作中的心态和之前工作中的完全不一样。简单而言，就是需要以更开放的心态来应对更多的未知挑战。同理，这样的心态也是移民准备中需要的，所以我们不妨展开来谈谈。&lt;/p&gt;&lt;p&gt;如果我们的目标是技术移民，并且目标是在新国家找到工作，那么以这种心态可以让我们在准备过程中保持主动和创新。&lt;/p&gt;&lt;p&gt;1. 心态调整&lt;/p&gt;&lt;p&gt;具体而言，我们的心态调整有三块：&lt;br /&gt;	•	主动性：积极寻找机会&lt;br /&gt;	•	适应性：快速适应新挑战&lt;br /&gt;	•	持续学习：不断更新和提升技能&lt;/p&gt;&lt;p&gt;&lt;a href="https://exodus.awesomevisa.com/exodus-3-31/" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class="ellipsis"&gt;exodus.awesomevisa.com/exodus-&lt;/span&gt;&lt;span class="invisible"&gt;3-31/&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111933488468622757</guid>
              <link>https://m.cmx.im/@nova/111933488468622757</link>
              <pubDate>Thu, 15 Feb 2024 03:58:09 +0000</pubDate>
              <description>&lt;p&gt;帮朋友拍肖像，摁了七百多次快门，粗选出来一百张。&lt;/p&gt;&lt;p&gt;最后她挑了其中的六张，其中两张也是我觉得拍得还不错的。&lt;/p&gt;</description>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/933/478/076/845/177/original/489fd8855743e5ec.jpeg" type="image/jpeg" fileSize="284268" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/933/478/087/105/393/original/d12f7ae69c34dee7.jpeg" type="image/jpeg" fileSize="189926" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111932711324392032</guid>
              <link>https://m.cmx.im/@nova/111932711324392032</link>
              <pubDate>Thu, 15 Feb 2024 00:40:31 +0000</pubDate>
              <description>&lt;p&gt;Daily Productive Sharing 914 - Margin of Safety&lt;/p&gt;&lt;p&gt;Jack Raines 指出 Amazon 之所以能在互联网泡沫中存活下来，是因为他们在爆发前一个月卖出了6.72亿美金的债券：&lt;/p&gt;&lt;p&gt;	1	这些现金为 Amazon 的存活提供了保障；&lt;/p&gt;&lt;p&gt;	2	SpaceX 之所以能活下来，是因为 Elon Musk 用之前两家公司 Zip2 和 Paypal 的钱来养活它；&lt;/p&gt;&lt;p&gt;	3	很多企业的成功，只是比竞争对手多活了一段时间；&lt;/p&gt;&lt;p&gt;	4	如果你想独自创业，请确保有足够的现金流。&lt;/p&gt;&lt;p&gt;&lt;a href="https://letters.acacess.com/daily-productive-sharing-914/" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class="ellipsis"&gt;letters.acacess.com/daily-prod&lt;/span&gt;&lt;span class="invisible"&gt;uctive-sharing-914/&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/932/710/909/182/063/original/7fafcd09857e0983.jpeg" type="image/jpeg" fileSize="387421" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111929816379546412</guid>
              <link>https://m.cmx.im/@nova/111929816379546412</link>
              <pubDate>Wed, 14 Feb 2024 12:24:17 +0000</pubDate>
              <description>&lt;p&gt;最近收到朋友的推荐 — The Art of Loving by Erich Fromm，读了非常有收获。&lt;/p&gt;&lt;p&gt;正好应景推荐给大家，祝大家情人节快乐，没有情人也要好好爱自己！&lt;/p&gt;&lt;p&gt;&lt;a href="https://t.me/tms_ur_way/3177" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class=""&gt;t.me/tms_ur_way/3177&lt;/span&gt;&lt;span class="invisible"&gt;&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111929658795027900</guid>
              <link>https://m.cmx.im/@nova/111929658795027900</link>
              <pubDate>Wed, 14 Feb 2024 11:44:13 +0000</pubDate>
              <description>&lt;p&gt;‪牛津大学出版社最近改了 logo，一言难尽‬&lt;/p&gt;&lt;p&gt;&lt;a href="https://t.me/tms_ur_way/3176" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class=""&gt;t.me/tms_ur_way/3176&lt;/span&gt;&lt;span class="invisible"&gt;&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/929/657/831/008/987/original/83efee6c68103f1b.jpeg" type="image/jpeg" fileSize="76024" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111922645294719772</guid>
              <link>https://m.cmx.im/@nova/111922645294719772</link>
              <pubDate>Tue, 13 Feb 2024 06:00:35 +0000</pubDate>
              <description>&lt;p&gt;重新捡起相机，好好拍肖像&lt;/p&gt;</description>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/922/642/703/064/352/original/cf3c6d1a54caf160.jpeg" type="image/jpeg" fileSize="678895" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/922/643/949/670/263/original/202123d1902c7165.jpeg" type="image/jpeg" fileSize="246247" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111921313419739442</guid>
              <link>https://m.cmx.im/@nova/111921313419739442</link>
              <pubDate>Tue, 13 Feb 2024 00:21:53 +0000</pubDate>
              <description>&lt;p&gt;Daily Productive Sharing 912 - Meditations on Riding&lt;/p&gt;&lt;p&gt;Herman Martinus 花了四天时间从开普敦骑摩托到约翰内斯堡，在他看来这像是在沙漠里冥想：&lt;/p&gt;&lt;p&gt;	1	骑摩托意味着你必须活在当下，不能分心；&lt;/p&gt;&lt;p&gt;	2	骑摩托不断地能收到地面的反馈，让他觉得自己和大地连接在一起；&lt;/p&gt;&lt;p&gt;	3	每天的劳累让他在晚上获得很好的休息；&lt;/p&gt;&lt;p&gt;	4	一路上他不断获得他人的帮助，也对他人施以援手。&lt;/p&gt;&lt;p&gt;阅读全文请点击 ⬇️&lt;/p&gt;&lt;p&gt;&lt;a href="https://letters.acacess.com/daily-productive-sharing-912/" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class="ellipsis"&gt;letters.acacess.com/daily-prod&lt;/span&gt;&lt;span class="invisible"&gt;uctive-sharing-912/&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/921/312/933/625/548/original/69b848156c227a74.jpeg" type="image/jpeg" fileSize="352118" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111916445899787201</guid>
              <link>https://m.cmx.im/@nova/111916445899787201</link>
              <pubDate>Mon, 12 Feb 2024 03:44:00 +0000</pubDate>
              <description>&lt;p&gt;&lt;span class="h-card" translate="no"&gt;&lt;a href="https://ovo.st/club/runrunrun" class="u-url mention"&gt;@&lt;span&gt;runrunrun&lt;/span&gt;&lt;/a&gt;&lt;/span&gt; &lt;/p&gt;&lt;p&gt;其实走线在中国的历史里不断重演，不过是以不同的方式走向不同的国家，唯一相同的就是原因 — 因为在中国活不下去了。&lt;/p&gt;&lt;p&gt;— 《毛澤東的大饑荒》by Frank Dikötter&lt;/p&gt;&lt;p&gt;&lt;a href="https://t.me/pillarsalt/527" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class=""&gt;t.me/pillarsalt/527&lt;/span&gt;&lt;span class="invisible"&gt;&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/916/443/008/431/684/original/64da352a289f9c27.jpeg" type="image/jpeg" fileSize="516310" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/916/443/013/487/339/original/06624f6786f03aff.jpeg" type="image/jpeg" fileSize="584502" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/916/443/029/954/178/original/a5ee58fa52a4a9cb.jpeg" type="image/jpeg" fileSize="505331" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111916023598766797</guid>
              <link>https://m.cmx.im/@nova/111916023598766797</link>
              <pubDate>Mon, 12 Feb 2024 01:56:36 +0000</pubDate>
              <description>&lt;p&gt;Daily Productive Sharing 911 - Three lessons from Peter Yang&lt;/p&gt;&lt;p&gt;Peter Yang 在一份全职工作的同时，还经营着一份电子报：&lt;br /&gt;	1	他对很多事物说不，因为他认为保证电子报的质量是最重要的事；&lt;br /&gt;	2	他每天五点半起床，六点到七点之间做自己的事，如果完成了的话，他就认为这一天是成功的一天；&lt;br /&gt;	3	他都是异步工作，包括和他的团队做决策。&lt;/p&gt;&lt;p&gt;&lt;a href="https://letters.acacess.com/daily-productive-sharing-911/" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class="ellipsis"&gt;letters.acacess.com/daily-prod&lt;/span&gt;&lt;span class="invisible"&gt;uctive-sharing-911/&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111899207600747476</guid>
              <link>https://m.cmx.im/@nova/111899207600747476</link>
              <pubDate>Fri, 09 Feb 2024 02:40:05 +0000</pubDate>
              <description>&lt;p&gt;Daily Productive Sharing 910 - Letter 2023&lt;/p&gt;&lt;p&gt;去年12月，Dan Wang 在清迈度过了一个月，他参加了 Kevin Kelly 和 Craig Mod 组织的 Walk and Talk 活动 -- 七天走了100公里。这次步行激发他继续写年度之信，这里分享的就是他写的 2023 年度之信，主要分为两部分，第一部分谈中国，第二部分谈美国：&lt;/p&gt;&lt;p&gt;	1	泰北和中国西南部又非常多的山峦，所以很多人选择这些地方避世；&lt;/p&gt;&lt;p&gt;	2	2023年有更多的人“润”了出来，首选的是亚洲三国 -- 像创业的跑到了新加坡，已经有条件的跑到了日本，想避世的跑到了泰国；&lt;/p&gt;&lt;p&gt;	3	很多跑到泰国的中国人才20出头，他们想夺回失去的三年。尽管如此，其中不少不敢告诉父母，自己跑到了泰国；&lt;/p&gt;&lt;p&gt;	4	跑到泰国的这些人也不确定自己是否会一直呆在泰国，当然更不确定的是家在哪里；&lt;/p&gt;&lt;p&gt;	5	回顾过去，东南亚一直是避世的首选之地，从太平天国开始；&lt;/p&gt;&lt;p&gt;	6	20世纪初，曼谷有一半以上的人口是华人，即使到现在，10-15%的泰国人口是华裔；&lt;/p&gt;&lt;p&gt;	7	中国的汽车出口总量于2023年超越日本，成为世界第一。主要由电动车构成，而这一成功得益于十多年前的电池产业布局；&lt;/p&gt;&lt;p&gt;	8	尽管有不少制造业外迁，但中国仍将在未来几年占据制作业的主导位置；&lt;/p&gt;&lt;p&gt;	9	2022年，中国的造船量占据全球的一半，而美国只有0.2%；&lt;/p&gt;&lt;p&gt;	10	尽管美国政界商界都明白，吸引高技术人才是保持竞争力的关键，但是高技术人才的签证政策并没有太多变化；&lt;/p&gt;&lt;p&gt;	11	中国已经开放了40年，再闭关锁国是不可能的。&lt;/p&gt;&lt;p&gt;&lt;a href="https://letters.acacess.com/daily-productive-sharing-910/" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class="ellipsis"&gt;letters.acacess.com/daily-prod&lt;/span&gt;&lt;span class="invisible"&gt;uctive-sharing-910/&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/899/207/334/396/269/original/ee44ee493f006f56.jpeg" type="image/jpeg" fileSize="423194" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111893953780247795</guid>
              <link>https://m.cmx.im/@nova/111893953780247795</link>
              <pubDate>Thu, 08 Feb 2024 04:23:58 +0000</pubDate>
              <description>&lt;p&gt;&lt;span class="h-card" translate="no"&gt;&lt;a href="https://ovo.st/club/runrunrun" class="u-url mention"&gt;@&lt;span&gt;runrunrun&lt;/span&gt;&lt;/a&gt;&lt;/span&gt; &lt;/p&gt;&lt;p&gt;Exodus 3.30 有那么多牌，你怕什么？&lt;/p&gt;&lt;p&gt;最近的一个案例也蛮典型的，所以我们不妨一起来看一下：&lt;/p&gt;&lt;p&gt;我现在对于这个迷茫的点在于，我想的太久太远了，我竟然在纠结于自己的养老和对父母的赡养。看着绿卡的移民监自己在纠结年纪大了究竟是回国还是在那边一个人了此残生。&lt;/p&gt;&lt;p&gt;其实这是典型的了解不多又想得太多——对自己的了解不够，对于外面的了解也不够，然后想法太多，做不了决策。&lt;/p&gt;&lt;p&gt;还是回到牌桌上，那么我们要清楚这几点：&lt;/p&gt;&lt;p&gt;	1	看牌 - 自己手上有什么牌，可以打出什么样的组合；&lt;br /&gt;	2	算牌 - 对方手上可能有什么牌，已经出了那些牌，可能会出那些牌；&lt;br /&gt;	3	出牌 - 每一次出手，都要根据已经出的牌进行调整。&lt;/p&gt;&lt;p&gt;&lt;a href="https://exodus.awesomevisa.com/exodus-3-30/" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class="ellipsis"&gt;exodus.awesomevisa.com/exodus-&lt;/span&gt;&lt;span class="invisible"&gt;3-30/&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111893001633228488</guid>
              <link>https://m.cmx.im/@nova/111893001633228488</link>
              <pubDate>Thu, 08 Feb 2024 00:21:49 +0000</pubDate>
              <description>&lt;p&gt;Daily Productive Sharing 909 - On Spending Money&lt;/p&gt;&lt;p&gt;Morgan Housel 谈了一些关于财富的见解：&lt;/p&gt;&lt;p&gt;	1	使用金钱有两种方式。一种是作为工具，让生活更美好。另一种是作为衡量自己与他人地位的尺度。&lt;/p&gt;&lt;p&gt;	2	你可以使用金钱；但如果不小心，它会反过来使用你。&lt;/p&gt;&lt;p&gt;	3	每个人都可以以一种让自己更快乐的方式花钱，但没有通用的公式告诉你如何做到这一点。&lt;/p&gt;&lt;p&gt;	4	人们花钱不仅仅是因为他们觉得某样东西有趣或有用。他们的决定往往反映了他们生活经验中的心理创伤。&lt;/p&gt;&lt;p&gt;	5	花钱可以买到幸福，但这往往是间接的。&lt;/p&gt;&lt;p&gt;	6	每一分的储蓄都买下了对未来的一份可能性。&lt;/p&gt;&lt;p&gt;	7	没有什么比你想要却得不到的东西更令人渴望。&lt;/p&gt;&lt;p&gt;	8	客观的财富水平并不存在。&lt;/p&gt;&lt;p&gt;&lt;a href="https://letters.acacess.com/daily-productive-sharing-909/" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class="ellipsis"&gt;letters.acacess.com/daily-prod&lt;/span&gt;&lt;span class="invisible"&gt;uctive-sharing-909/&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/893/001/017/295/016/original/f36296bae3c2dcdc.jpeg" type="image/jpeg" fileSize="263265" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111890012547937578</guid>
              <link>https://m.cmx.im/@nova/111890012547937578</link>
              <pubDate>Wed, 07 Feb 2024 11:41:39 +0000</pubDate>
              <description>&lt;p&gt;每天喝一升这样的豆浆，感觉可以把蛋白粉的钱都省了。&lt;/p&gt;&lt;p&gt;一盒豆浆大约十二块钱，可以获得40g蛋白质，&lt;/p&gt;&lt;p&gt;我喝的蛋白粉500g一罐，差不多两百一罐。每次一勺可以获得20g蛋白质，要两勺才能达到上面一盒豆浆的蛋白质，也就是二十块的成本。&lt;/p&gt;&lt;p&gt;热量也是豆浆低，含糖量差不多。唯一的优势就是这种蛋白粉加了 BCAA。&lt;/p&gt;</description>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/889/984/304/988/185/original/8e4794cdb0834af8.jpeg" type="image/jpeg" fileSize="1642623" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111887372933722549</guid>
              <link>https://m.cmx.im/@nova/111887372933722549</link>
              <pubDate>Wed, 07 Feb 2024 00:30:22 +0000</pubDate>
              <description>&lt;p&gt;Daily Productive Sharing 908 - Tips for a Better Life&lt;/p&gt;&lt;p&gt;Conor Barnes 总结了100条人生感悟，其中这些非常有启发：&lt;br /&gt;	1	你在生活中频繁使用的东西（床：1/3，办公椅：1/4）值得投资。&lt;br /&gt;	2	一旦建立了明确的规则，垃圾问题可能就不再是问题。&lt;br /&gt;	3	购买东西时，时间和金钱会相互抵消。&lt;br /&gt;	4	保持你的桌子和工作区域干净整洁。&lt;br /&gt;	5	最好的建议是个人化的，它们来自于了解你的人。&lt;br /&gt;	6	尽可能简化完成任务的过程。&lt;br /&gt;	7	注意到别人的偏见很容易，注意到自己的偏见很难。然而，这有更高的回报。&lt;br /&gt;	8	在描述问题的过程中，解决方案往往会自呈现。&lt;br /&gt;	9	不是你的错的事情仍然可以是你的责任。&lt;br /&gt;	10	以自己的苦难来定义自己是永远苦难的有效方法（例如：无缘社群，创伤）。&lt;br /&gt;	11	记住你正在死去。&lt;br /&gt;	12	当你问人们“你最喜欢的书/电影/乐队是什么？”而他们犹豫不决时，改问他们目前最喜欢的书/电影/乐队是什么。&lt;br /&gt;	13	在关系中寻找一个你喜欢仅仅是靠近挂出的人。长期关系大多数时间都是在放松中度过的。&lt;br /&gt;	14	如果你在约会时交谈有困难，试着说出任何进入你头脑的事情。最糟糕的情况是你会毁了一些约会（反正它们也不顺利），最好的情况是你会有一些很棒的对话。酒精可以帮助。&lt;br /&gt;	15	人们可能不适合你，但并不意味着他们是坏人。&lt;br /&gt;	16	当你想到父母时给他们打电话，当你爱你的朋友时告诉他们。&lt;br /&gt;	17	多夸奖人。许多人很难把自己看作聪明、漂亮或善良，除非有人这么告诉他们。你可以帮助他们。&lt;br /&gt;	18	不要因为人们承认自己错了而惩罚他们，这会让他们更难以改进。&lt;br /&gt;	19	坏事往往戏剧化地发生（一场大流行）。好事往往逐年逐步发生（疟疾死亡率逐年下降）并不感觉像是“新闻”。&lt;/p&gt;&lt;p&gt;&lt;a href="https://letters.acacess.com/daily-productive-sharing-908/" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class="ellipsis"&gt;letters.acacess.com/daily-prod&lt;/span&gt;&lt;span class="invisible"&gt;uctive-sharing-908/&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
              <media:content url="https://media.cmx.edu.kg/media_attachments/files/111/887/372/479/628/418/original/096214505acc4071.jpeg" type="image/jpeg" fileSize="300182" medium="image">
                <media:rating scheme="urn:simple">nonadult</media:rating>
              </media:content>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111881721850727344</guid>
              <link>https://m.cmx.im/@nova/111881721850727344</link>
              <pubDate>Tue, 06 Feb 2024 00:33:13 +0000</pubDate>
              <description>&lt;p&gt;Daily Productive Sharing 907 - The World Can Be Much Better&lt;/p&gt;&lt;p&gt;Max Roser 利用婴幼儿死亡率的下降，来说明世界很糟糕，世界已经变好了很多，世界还能变得更好这三者同时存在：&lt;/p&gt;&lt;p&gt;	1	基于2020年的数据，全球总体的婴幼儿死亡率在4.3%；&lt;/p&gt;&lt;p&gt;	2	而在十九世纪前，全球只有一半的婴幼儿能活到成年；&lt;/p&gt;&lt;p&gt;	3	欧盟各国的婴幼儿死亡率已经降至0.45%；&lt;/p&gt;&lt;p&gt;	4	结合以上三个数据，我们可以看到世界已经变好了很多，世界还可以变得更好，而死亡本身的存在也说明世界还是很糟糕。&lt;/p&gt;&lt;p&gt;&lt;a href="https://letters.acacess.com/daily-productive-sharing-907/" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class="ellipsis"&gt;letters.acacess.com/daily-prod&lt;/span&gt;&lt;span class="invisible"&gt;uctive-sharing-907/&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
            </item>
            <item>
              <guid isPermaLink="true">https://m.cmx.im/@nova/111881077169478666</guid>
              <link>https://m.cmx.im/@nova/111881077169478666</link>
              <pubDate>Mon, 05 Feb 2024 21:49:16 +0000</pubDate>
              <description>&lt;p&gt;&lt;span class="h-card" translate="no"&gt;&lt;a href="https://ovo.st/club/runrunrun" class="u-url mention"&gt;@&lt;span&gt;runrunrun&lt;/span&gt;&lt;/a&gt;&lt;/span&gt; &lt;span class="h-card" translate="no"&gt;&lt;a href="https://ovo.st/club/board" class="u-url mention"&gt;@&lt;span&gt;board&lt;/span&gt;&lt;/a&gt;&lt;/span&gt;  &lt;/p&gt;&lt;p&gt;CBS 介绍走线进入美国的中国人：&lt;/p&gt;&lt;p&gt;1. 这两年通过走线进入美国的中国人暴涨，增长速度是所有国别中最快的；&lt;/p&gt;&lt;p&gt;2. 美国边境管理无权阻止这种方式进入美国，只能在入境后进行逮捕；&lt;/p&gt;&lt;p&gt;3. 以这种身份进入美国只能申请政治庇护，也就意味着今后无法回国。当然不是所有人都能获得政治庇护，所以会被美国遣返，但是中国拒收；&lt;/p&gt;&lt;p&gt;4. 以这种方式进入美国的，有各种各样的人，有刚毕业的学生，有白发苍苍的老人。大家都很绝望，看了抖音相关视频就决定搏一把。&lt;/p&gt;&lt;p&gt;&lt;a href="https://t.me/pillarsalt/522" target="_blank" rel="nofollow noopener noreferrer" translate="no"&gt;&lt;span class="invisible"&gt;https://&lt;/span&gt;&lt;span class=""&gt;t.me/pillarsalt/522&lt;/span&gt;&lt;span class="invisible"&gt;&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;</description>
            </item>
          </channel>
        </rss>
    """.trimIndent()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
//        MainPage()

        val coroutineScope = rememberCoroutineScope()
        Box(modifier = Modifier.fillMaxSize()) {
            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    coroutineScope.launch {
                        RssParser.parse(rssText)
                    }
                }
            ) {
                Text(text = "Parse rss")
            }
        }

//            Navigator(TabTestScreen())

//        val tabs: List<PagerTab> = remember {
//            listOf(
//                FirstTab(0),
//                FirstTab(1),
//                SecondTab(2),
//                ThirdTab(3),
//            )
//        }
//        val pagerState = rememberPagerState {
//            tabs.size
//        }
//            Column(modifier = Modifier.fillMaxSize()) {
//                Row(modifier = Modifier.fillMaxWidth()) {
//                    Button(onClick = {
//                        tabNavigator.current = tabs.first()
//                    }) {
//                        Text(text = "First")
//                    }
//                    Button(onClick = {
//                        tabNavigator.current = tabs[1]
//                    }) {
//                        Text(text = "Second")
//                    }
//                    Button(onClick = {
//                        tabNavigator.current = tabs[2]
//                    }) {
//                        Text(text = "Third")
//                    }
//                }
//                Box(modifier = Modifier.fillMaxSize()) {
//                    CurrentTab()
//                }
//            }
//        HorizontalPager(
//            modifier = Modifier.fillMaxSize(),
//            state = pagerState,
//        ) {
//            Log.d("U_TEST", "current page index is $it")
//            with(tabs[it]) {
//                TabContent()
//            }
//        }
    }
}

interface PagerTab {

    val title: String
        @Composable get

    @Composable
    fun Screen.TabContent()
}

class FirstTab(private val pageIndex: Int) : PagerTab {

    override val title: String
        @Composable get() = "$pageIndex"

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Screen.TabContent() {
        val viewModel = getViewModel<FirstViewModel, FirstViewModel.Factory> {
            it.create(pageIndex)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = title,
            )
        }
    }
}

@HiltViewModel(assistedFactory = FirstViewModel.Factory::class)
class FirstViewModel @AssistedInject constructor(
    @Assisted val pageIndex: Int,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(pageIndex: Int): FirstViewModel
    }

    init {
        Log.d("U_TEST", "FirstViewModel@${hashCode()} init, index is $pageIndex")
    }
}

class SecondTab(private val pageIndex: Int) : PagerTab {

    override val title: String
        @Composable get() = "$pageIndex"

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Screen.TabContent() {
        val viewModel: SecondViewModel = getViewModel<SecondViewModel, SecondViewModel.Factory> {
            it.create(pageIndex)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Second",
            )
        }
    }
}

@HiltViewModel(assistedFactory = SecondViewModel.Factory::class)
class SecondViewModel @AssistedInject constructor(
    @Assisted val pageIndex: Int,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(pageIndex: Int): SecondViewModel
    }

    init {
        Log.d("U_TEST", "SecondViewModel@${hashCode()} init, index is $pageIndex")
    }
}

class ThirdTab(private val pageIndex: Int) : PagerTab {

    override val title: String
        @Composable get() = "$pageIndex"

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Screen.TabContent() {
        val viewModel: ThirdViewModel = getViewModel<ThirdViewModel, ThirdViewModel.Factory> {
            it.create(pageIndex)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Third",
            )
        }
    }
}

@HiltViewModel(assistedFactory = ThirdViewModel.Factory::class)
class ThirdViewModel @AssistedInject constructor(
    @Assisted val pageIndex: Int,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(pageIndex: Int): ThirdViewModel
    }

    init {
        Log.d("U_TEST", "ThirdViewModel@${hashCode()} init, index is $pageIndex")
    }
}

//@Module
//@InstallIn(ActivityComponent::class)
//abstract class HiltModule {
//
//    @Binds
//    @IntoMap
//    @ScreenModelFactoryKey(FirstViewModel.Factory::class)
//    abstract fun bindFirstScreenModelFactory(
//        hiltDetailsScreenModelFactory: FirstViewModel.Factory
//    ): ScreenModelFactory
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(FirstViewModel::class)
//    abstract fun bindFirstScreenModel(testScreenModel: FirstViewModel): ScreenModel
//
//    @Binds
//    @IntoMap
//    @ScreenModelFactoryKey(SecondViewModel.Factory::class)
//    abstract fun bindSecondScreenModelFactory(
//        hiltDetailsScreenModelFactory: SecondViewModel.Factory
//    ): ScreenModelFactory
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(SecondViewModel::class)
//    abstract fun bindSecondScreenModel(testScreenModel: SecondViewModel): ScreenModel
//
//    @Binds
//    @IntoMap
//    @ScreenModelFactoryKey(ThirdViewModel.Factory::class)
//    abstract fun bindThirdScreenModelFactory(
//        hiltDetailsScreenModelFactory: ThirdViewModel.Factory
//    ): ScreenModelFactory
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(ThirdViewModel::class)
//    abstract fun bindThirdScreenModel(testScreenModel: ThirdViewModel): ScreenModel
//}
