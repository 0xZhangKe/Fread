package com.halilibo.richtext.ui.util

import platform.Foundation.NSUUID

internal actual fun randomUUID(): String {
  return NSUUID().UUIDString()
}
