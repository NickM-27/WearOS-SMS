# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\Android-Development\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

-keep class com.nick.mowen.wearossms.WearOSSMS { *; }
-keep class android.net.NetworkUtilsHelper { *; }
-keep class com.bumptech.glide.** { *; }
-keep class com.klinker.android.send_message.MmsFileProvider { *; }
-keep class androidx.core.content.FileProvider { *; }
-keep class androidx.appcompat.app.AppCompatViewInflater { *; }
-keep class androidx.appcompat.widget.SearchView { *; }

-dontwarn com.nick.mowen.wearossms.receiver.*
-dontwarn android.net.LinkProperties
-dontwarn ezvcard.io.json.**
-dontwarn freemarker.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn android.net.ConnectivityManager
-dontwarn org.codehaus.**

-keepattributes EnclosingMethod

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#}