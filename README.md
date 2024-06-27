# Fread

Mastodon android client

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

