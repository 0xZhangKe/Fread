package com.zhangke.framework.opml

import kotlin.test.Test
import kotlin.test.assertEquals

class OpmlParserTest {
    @Test
    fun testParse() {
        val xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <opml version="1.0">
            <head><title>中文独立博客列表</title></head>
            <body>
                <outline text="透明创业实验" title="透明创业实验" type="rss" xmlUrl="https://blog.t9t.io/atom.xml" htmlUrl="https://blog.t9t.io"/>
                <outline text="阮一峰的网络日志" title="阮一峰的网络日志" type="rss" xmlUrl="http://feeds.feedburner.com/ruanyifeng" htmlUrl="https://www.ruanyifeng.com/blog/"/>
                <outline text="酷 壳 – CoolShell" title="酷 壳 – CoolShell" type="rss" xmlUrl="http://coolshell.cn/feed" htmlUrl="https://coolshell.cn"/>
                <outline text="张鑫旭-鑫空间-鑫生活" title="张鑫旭-鑫空间-鑫生活" type="rss" xmlUrl="http://www.zhangxinxu.com/wordpress/?feed=rss2" htmlUrl="https://www.zhangxinxu.com/"/>
                <outline text="Alili丶前端大爆炸" title="Alili丶前端大爆炸" type="rss" xmlUrl="https://alili.tech/index.xml" htmlUrl="https://alili.tech"/>
                <outline text="蚊子前端博客" title="蚊子前端博客" type="rss" xmlUrl="https://www.xiabingbao.com/atom.xml" htmlUrl="https://www.xiabingbao.com"/>
                <outline text="DIYGod - 写代码是热爱，写到世界充满爱!" title="DIYGod - 写代码是热爱，写到世界充满爱!" type="rss" xmlUrl="https://diygod.me/atom.xml" htmlUrl="https://diygod.me"/>
                <outline text="MacTalk-池建强的随想录" title="MacTalk-池建强的随想录" type="rss" xmlUrl="http://macshuo.com/?feed=rss2" htmlUrl="http://macshuo.com"/>
                <outline text="ShrekShao" title="ShrekShao" type="rss" xmlUrl="http://shrekshao.github.io/feed.xml" htmlUrl="https://shrekshao.github.io/"/>
                <outline text="云风的 BLOG" title="云风的 BLOG" type="rss" xmlUrl="http://blog.codingnow.com/atom.xml" htmlUrl="https://blog.codingnow.com"/>
                <outline text="Reorx’s Forge" title="Reorx’s Forge" type="rss" xmlUrl="https://reorx.com/feed.xml" htmlUrl="https://reorx.com/"/>
                <outline text="ZDDHUB 的博客" title="ZDDHUB 的博客" type="rss" xmlUrl="https://zddhub.com/feed" htmlUrl="https://zddhub.com/"/>
                <outline text="全栈应用开发:精益实践" title="全栈应用开发:精益实践" type="rss" xmlUrl="https://www.phodal.com/blog/feeds/rss/" htmlUrl="https://www.phodal.com">
                    <outline text="前端之巅" title="前端之巅" type="rss" xmlUrl="https://www.phodal.com/blog/feeds/rss/" htmlUrl="https://www.phodal.com"/>
                    <outline text="云风的 BLOG" title="云风的 BLOG" type="rss" xmlUrl="http://blog.codingnow.com/atom.xml" htmlUrl="https://blog.codingnow.com"/>
                </outline>
            </body>
            </opml>
        """.trimIndent()
        val result = OpmlParser.parse(xml)
        assertEquals(13, result.size)
        assertEquals("透明创业实验", result[0].title)
        assertEquals("全栈应用开发:精益实践", result[12].title)
        assertEquals("前端之巅", result[12].children[0].title)
    }
}