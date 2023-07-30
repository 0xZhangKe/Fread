# Utopia

Mastodon android client

# StatusProviderUri
The Uri associated with status-provider is denoted by `StatusProviderUri`.

- scheme: utopiaapp
- host: protocol identification host, no need for ten actual hosts, e.g:
  activitypub.com/rss.com/atom.com
- path: function identification
- query: function params

## ActivityPub uri
scheme always is utopiaapp

host always is activitypub.com

### user path
presenter a user, must container a `WebFinger` query and a `UserId` query.

### timeline path
Presenter a timeline, must container a `host` and `type` query.

