package com.zhangke.fread.status.richtext.translate

import androidx.compose.ui.text.LinkAnnotation
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.Facet
import com.zhangke.fread.status.model.FacetFeatureUnion
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.createActivityPubProtocol
import com.zhangke.fread.status.richtext.model.RichLinkTarget
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RichTextTranslatorParserTest {

    private val translator = RichTextTranslatorParser

    @Test
    fun parseFacetSplitsPlainTextAndLinksAfterMergingOverlaps() {
        val text = "prefix @alice middle #tag suffix"

        assertEquals(
            listOf(
                TranslatorBlock.PlainTextBlock("prefix "),
                TranslatorBlock.LinkBlock(
                    target = RichLinkTarget.MentionDidTarget("did:plc:alice"),
                    content = "@alice",
                ),
                TranslatorBlock.PlainTextBlock(" middle "),
                TranslatorBlock.LinkBlock(
                    target = RichLinkTarget.MaybeHashtagTarget("tag"),
                    content = "#tag",
                ),
                TranslatorBlock.PlainTextBlock(" suffix"),
            ),
            translator.parseFacet(
                text = text,
                facets = listOf(
                    facet(
                        start = 21,
                        end = 25,
                        feature = FacetFeatureUnion.Tag("tag"),
                    ),
                    facet(
                        start = 8,
                        end = 12,
                        feature = FacetFeatureUnion.Link("https://overlap.example"),
                    ),
                    facet(
                        start = 7,
                        end = 13,
                        feature = FacetFeatureUnion.Mention("did:plc:alice"),
                    ),
                ),
            ),
        )
    }

    @Test
    fun parseFacetUsesUtf8ByteOffsets() {
        val url = "https://example.com"

        assertEquals(
            listOf(
                TranslatorBlock.PlainTextBlock("你好 "),
                TranslatorBlock.LinkBlock(
                    target = RichLinkTarget.UrlTarget(url),
                    content = url,
                ),
                TranslatorBlock.PlainTextBlock(" 世界"),
            ),
            translator.parseFacet(
                text = "你好 $url 世界",
                facets = listOf(
                    facet(
                        start = 7,
                        end = 26,
                        feature = FacetFeatureUnion.Link(url),
                    ),
                ),
            ),
        )
    }

    @Test
    fun mergeOverlapFacetKeepsLongestFacetInEachOverlapGroupAndSortsResult() {
        val result = with(translator) {
            listOf(
                facet(49, 60),
                facet(12, 18),
                facet(30, 35),
                facet(44, 50),
                facet(20, 25),
                facet(10, 15),
                facet(40, 45),
                facet(8, 20),
                facet(72, 77),
                facet(70, 75),
            ).mergeOverlapFacet()
        }

        assertEquals(
            listOf(
                facet(8, 20),
                facet(20, 25),
                facet(30, 35),
                facet(49, 60),
                facet(70, 75),
            ),
            result,
        )
    }

    @Test
    fun rebuildAnnotationStringPreservesContentAndAnnotationRanges() {
        val fixture = createMastodonStatusFixture()
        val blocks = translator.parseHtml(
            html = fixture.content,
            emojis = fixture.emojis,
            mentions = listOf(fixture.mention),
            hashTags = listOf(fixture.hashtag),
        )
        val clickedTargets = mutableListOf<RichLinkTarget>()

        val result = translator.rebuildAnnotationString(
            blockList = blocks,
            onLinkTargetClick = clickedTargets::add,
        )

        assertEquals(
            "哈哈哈 :awesome_rotate: :awesome_rotate: :eoc_20: \n\n" +
                "@fread Hi,\n:eoc_05: \nthreads.com/\n\nnihao #Ni",
            result.text,
        )
        assertEquals(
            listOf(
                Triple("emoji", 4, 20),
                Triple("emoji", 21, 37),
                Triple("emoji", 38, 46),
                Triple("emoji", 60, 68),
            ),
            result.getStringAnnotations(
                tag = "androidx.compose.foundation.text.inlineContent",
                start = 0,
                end = result.length,
            ).map { Triple(it.item, it.start, it.end) },
        )

        val linkRanges = result.getLinkAnnotations(0, result.length)
        assertEquals(
            listOf(
                Triple("113634847439413091", 49, 55),
                Triple("https://www.threads.com/", 70, 82),
                Triple("ni", 90, 93),
            ),
            linkRanges.map {
                val annotation = assertIs<LinkAnnotation.Clickable>(it.item)
                Triple(annotation.tag, it.start, it.end)
            },
        )

        linkRanges.forEach {
            val annotation = assertIs<LinkAnnotation.Clickable>(it.item)
            annotation.linkInteractionListener?.onClick(annotation)
        }
        assertEquals(
            listOf(
                RichLinkTarget.MentionTarget(fixture.mention),
                RichLinkTarget.UrlTarget("https://www.threads.com/"),
                RichLinkTarget.HashtagTarget(fixture.hashtag),
            ),
            clickedTargets,
        )
    }

    @Test
    fun parseHtmlFromMastodonStatusResponse() {
        val fixture = createMastodonStatusFixture()
        val awesomeRotate = fixture.emojis[0]
        val eoc20 = fixture.emojis[1]
        val eoc05 = fixture.emojis[2]

        assertEquals(
            listOf(
                TranslatorBlock.PlainTextBlock("哈哈哈 "),
                TranslatorBlock.EmojiBlock(awesomeRotate),
                TranslatorBlock.PlainTextBlock(" "),
                TranslatorBlock.EmojiBlock(awesomeRotate),
                TranslatorBlock.PlainTextBlock(" "),
                TranslatorBlock.EmojiBlock(eoc20),
                TranslatorBlock.PlainTextBlock(" "),
                TranslatorBlock.NewLineBlock,
                TranslatorBlock.NewLineBlock,
                TranslatorBlock.LinkBlock(
                    target = RichLinkTarget.MentionTarget(fixture.mention),
                    content = "@fread",
                ),
                TranslatorBlock.PlainTextBlock(" Hi,"),
                TranslatorBlock.NewLineBlock,
                TranslatorBlock.EmojiBlock(eoc05),
                TranslatorBlock.PlainTextBlock(" "),
                TranslatorBlock.NewLineBlock,
                TranslatorBlock.LinkBlock(
                    target = RichLinkTarget.UrlTarget("https://www.threads.com/"),
                    content = "threads.com/",
                ),
                TranslatorBlock.NewLineBlock,
                TranslatorBlock.NewLineBlock,
                TranslatorBlock.PlainTextBlock("nihao "),
                TranslatorBlock.LinkBlock(
                    target = RichLinkTarget.HashtagTarget(fixture.hashtag),
                    content = "#Ni",
                ),
            ),
            translator.parseHtml(
                html = fixture.content,
                emojis = fixture.emojis,
                mentions = listOf(fixture.mention),
                hashTags = listOf(fixture.hashtag),
            ),
        )
    }

    @Test
    fun parseHtmlPreservesRichTextStructure() {
        val emoji = Emoji(
            shortcode = "wave",
            url = "https://example.com/wave.png",
            staticUrl = "https://example.com/wave-static.png",
        )

        assertEquals(
            listOf(
                TranslatorBlock.PlainTextBlock("Hello "),
                TranslatorBlock.EmojiBlock(emoji),
                TranslatorBlock.PlainTextBlock(" "),
                TranslatorBlock.LinkBlock(
                    target = RichLinkTarget.UrlTarget("https://example.com"),
                    content = "world",
                ),
                TranslatorBlock.NewLineBlock,
                TranslatorBlock.NewLineBlock,
                TranslatorBlock.PlainTextBlock("Next"),
            ),
            translator.parseHtml(
                html = "<p>Hello :wave: <a href=\"https://example.com\">world" +
                        "<span class=\"invisible\">hidden</span></a></p>" +
                        "<p class=\"quote-inline\">quoted content</p><p>Next</p>",
                emojis = listOf(emoji),
            ),
        )
    }

    @Test
    fun parseHtmlBuildsHashtagLinkAndKeepsAnchorWithoutTargetAsText() {
        val hashtag = HashtagInStatus(
            name = "Kotlin",
            url = "https://example.com/tags/kotlin",
            protocol = createActivityPubProtocol(),
        )

        assertEquals(
            listOf(
                TranslatorBlock.LinkBlock(
                    target = RichLinkTarget.HashtagTarget(hashtag.copy(name = "kotlin")),
                    content = "#KOTLIN",
                ),
                TranslatorBlock.PlainTextBlock(" and "),
                TranslatorBlock.PlainTextBlock("plain"),
            ),
            translator.parseHtml(
                html = "<a class=\"hashtag\">#KOTLIN</a> and <a>plain</a>",
                hashTags = listOf(hashtag),
            ),
        )
    }

    private fun createMastodonStatusFixture(): MastodonStatusFixture {
        val emojis = listOf(
            Emoji(
                shortcode = "awesome_rotate",
                url = "https://media.cmx.edu.kg/custom_emojis/images/000/067/591/original/a5b37107a75ab054.png",
                staticUrl = "https://media.cmx.edu.kg/custom_emojis/images/000/067/591/static/a5b37107a75ab054.png",
            ),
            Emoji(
                shortcode = "eoc_20",
                url = "https://media.cmx.edu.kg/custom_emojis/images/000/432/962/original/fe2dc420e023dbc1.png",
                staticUrl = "https://media.cmx.edu.kg/custom_emojis/images/000/432/962/static/fe2dc420e023dbc1.png",
            ),
            Emoji(
                shortcode = "eoc_05",
                url = "https://media.cmx.edu.kg/custom_emojis/images/000/432/926/original/95e3e2cbf81eded6.png",
                staticUrl = "https://media.cmx.edu.kg/custom_emojis/images/000/432/926/static/95e3e2cbf81eded6.png",
            ),
        )
        val mention = Mention(
            id = "113634847439413091",
            username = "fread",
            url = "https://mastodon.social/@fread",
            webFinger = WebFinger.build("fread", "mastodon.social"),
            protocol = createActivityPubProtocol(),
        )
        val hashtag = HashtagInStatus(
            name = "ni",
            url = "https://m.cmx.im/tags/ni",
            protocol = createActivityPubProtocol(),
        )
        val content = "<p>哈哈哈 :awesome_rotate: :awesome_rotate: :eoc_20: </p>" +
            "<p><span class=\"h-card\" translate=\"no\"><a href=\"https://mastodon.social/@fread\" " +
            "class=\"u-url mention\">@<span>fread</span></a></span> Hi,<br />:eoc_05: <br />" +
            "<a href=\"https://www.threads.com/\" target=\"_blank\" rel=\"nofollow noopener\" " +
            "translate=\"no\"><span class=\"invisible\">https://www.</span>" +
            "<span class=\"\">threads.com/</span><span class=\"invisible\"></span></a></p>" +
            "<p>nihao <a href=\"https://m.cmx.im/tags/Ni\" class=\"mention hashtag\" " +
            "rel=\"tag\">#<span>Ni</span></a></p>"
        return MastodonStatusFixture(
            content = content,
            emojis = emojis,
            mention = mention,
            hashtag = hashtag,
        )
    }

    private data class MastodonStatusFixture(
        val content: String,
        val emojis: List<Emoji>,
        val mention: Mention,
        val hashtag: HashtagInStatus,
    )

    private fun facet(
        start: Long,
        end: Long,
        feature: FacetFeatureUnion? = null,
    ): Facet {
        return Facet(
            byteStart = start,
            byteEnd = end,
            features = listOfNotNull(feature),
        )
    }
}
