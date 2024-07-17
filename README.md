# Fread

Complete decentralized microblogging client supporting Mastodon and RSS feeds.

这是一个完备的 Microblog 客户端，目前已经支持了 Mastodon and RSS，未来会继续支持更多的协议。
在互联网的新世界，我们不仅需要去中心化，也需要足够好的用户体验，我们希望让新世界中的软件有更好的体验和更便捷的操作。
现在，Fread 已经支持了 Mastodon 几乎所有的功能，已经是一个完备的 Mastodon 客户端了，同时还支持 RSS 协议，你可以通过 RSS 协议订阅你喜欢的博客。
此外，Fread 还支持混合信息流，你完全可以创建一个同时包含了 Mastodon 内容和 RSS 内容的混合信息流。
Fread 对多个账号和多个服务器也做了很好的支持，你不必再通过复杂的方式来切换不同的账号和服务器，并且也不必先注册账号之后才能浏览某些其他服务器的内容。

# Module structure

```
           framework/libs
                |
           biz framework
                |
            common biz
               /  \
              /    \
         feature   plugins
           /          |
         app < - - - /
```

# Build
release-key: save in onedrive
password and alias in 1password
name is fread/zhangke-keystore.jks
same with NotionLight

# StatusProviderUri

The Uri associated with status-provider is denoted by `StatusProviderUri`.

- scheme: freadapp
- host: protocol identification host, no need for ten actual hosts, e.g:
  activitypub.com/rss.com/atom.com
- path: function identification
- query: function params

## ActivityPub uri

scheme always is freadapp

host always is activitypub.com

### user path

presenter a user, must container a `WebFinger` query and a `UserId` query.

### timeline path

Presenter a timeline, must container a `host` and `type` query.

