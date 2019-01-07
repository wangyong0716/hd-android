# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/tinx/develop/android/android-sdk-linux/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


-keep class com.jakewharton.** {*;}
-keep class com.media.** {*;}
-keep class fm.dian.hddata_android.** {*;}
-keep class fm.dian.hdui.wxapi.WXEntryActivity{*;}

#-keep class fm.dian.hdui.view.** {*;}
-keep class hd.hdmedia.** {*;}

#三方包混淆
-keep class fm.dian.service.** {*;}
-keep class fm.dian.protocol.** {*;}
-keep class android.support.**{*;}
-keep class fm.dian.hddata.**{*;}
-keep class fm.dian.hdservice.**{*;}
-keep class com.tencent.** {*;}
-keep class com.sina.** {*;}
-keep class com.upyun.** {*;}
-keep class fm.dian.hddata.** {*;}
-keep class fm.dian.jnihdagent.** {*;}
-keep class com.umeng.** {*;}
-keep class com.google.** {*;}
-keep class com.android.** {*;}
-keep class com.nostra13.universalimageloader.** {*;}
-keep class com.loopj.android.http.** {*;}
-keep class org.apache.commons.codec.** {*;}
-keep class u.aly.** {*;}


-dontwarn sun.misc.Unsafe
-dontwarn javax.annotation.**
-dontwarn fm.dian.hddata.**

## ----------------------------------
##   ########## support.v4混淆    ##########
## ----------------------------------
-dontwarn android.support.v4.**
-dontwarn **CompatHoneycomb
-dontwarn **CompatHoneycombMR2
-dontwarn **CompatCreatorHoneycombMR2
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

## ----------------------------------
##   ########## Gson混淆    ##########
## ----------------------------------
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }


 # # -------------------------------------------
 # #  ############### volley混淆  ###############
 # # -------------------------------------------
 -keep class com.android.volley.** {*;}
 -keep class com.android.volley.toolbox.** {*;}
 -keep class com.android.volley.Response$* { *; }
 -keep class com.android.volley.Request$* { *; }
 -keep class com.android.volley.RequestQueue$* { *; }
 -keep class com.android.volley.toolbox.HurlStack$* { *; }
 -keep class com.android.volley.toolbox.ImageLoader$* { *; }

 # # -------------------------------------------
 # #  ############### okhttp混淆  ###############
 # # -------------------------------------------
 -dontwarn com.squareup.okhttp.**