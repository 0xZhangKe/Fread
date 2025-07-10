# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

-dontobfuscate
-keepattributes *
-dontwarn androidx.test.platform.app.InstrumentationRegistry

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-dontwarn androidx.lifecycle.LiveData
-keep @androidx.room.Entity class * {
    *;
}

# Ktor
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn io.ktor.client.network.sockets.TimeoutExceptionsCommonKt
-dontwarn io.ktor.client.plugins.HttpTimeout$HttpTimeoutCapabilityConfiguration
-dontwarn io.ktor.client.plugins.HttpTimeout$Plugin
-dontwarn io.ktor.client.plugins.HttpTimeout
-dontwarn io.ktor.util.InternalAPI
-dontwarn io.ktor.utils.io.ByteReadChannelJVMKt
-dontwarn io.ktor.utils.io.CoroutinesKt
-dontwarn io.ktor.utils.io.ReadSessionKt
-dontwarn io.ktor.utils.io.core.Buffer$Companion
-dontwarn io.ktor.utils.io.core.Buffer
-dontwarn io.ktor.utils.io.core.ByteBuffersKt
-dontwarn io.ktor.utils.io.core.BytePacketBuilder
-dontwarn io.ktor.utils.io.core.ByteReadPacket$Companion
-dontwarn io.ktor.utils.io.core.ByteReadPacket
-dontwarn io.ktor.utils.io.core.CloseableJVMKt
-dontwarn io.ktor.utils.io.core.Input
-dontwarn io.ktor.utils.io.core.InputArraysKt
-dontwarn io.ktor.utils.io.core.InputPrimitivesKt
-dontwarn io.ktor.utils.io.core.Output
-dontwarn io.ktor.utils.io.core.OutputPrimitivesKt
-dontwarn io.ktor.utils.io.core.PreviewKt
-dontwarn io.ktor.client.network.sockets.SocketTimeoutException

# Parcelable
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class com.zhangke.activitypub.entities.** { *; }

-keepnames class <1>$$serializer { # -keepnames suffices; class is kept when serializer() is kept.
    static <1>$$serializer INSTANCE;
}
