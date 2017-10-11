# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Programy\Android\android-studio1\sdk/tools/proguard/proguard-android.txt
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
-keepclassmembers class com.studytor.app.helpers.HtmlJSInterface {
   public *;
}
-keep public class com.studytor.app.helpers.HtmlJSInterface
-keep public class * implements com.studytor.app.helpers.HtmlJSInterface
-keepclassmembers class com.studytor.app.helpers.HtmlJSInterface {
    <methods>;
}
-keepattributes JavascriptInterface
-keep class android.support.v4.** { *; }
#-keep interface android.support.v4.** { *; }
-keep class org.jsoup.** { *; }
#-keep interface org.jsoup.** { *; }
-keep class com.android.volley.** { *; }
#-keep interface com.android.volley.** { *; }
-keep class com.loopj.android.http.** { *; }
#-keep interface com.loopj.android.http.** { *; }
-keep class com.google.** { *; }
#-keep interface com.google.** { *; }

#-keep class com.google.gson.stream.** { *; }
#-keep class sun.misc.Unsafe { *; }

# Add any classes the interact with gson
########################    -keep class com.studytor.app.** { *; }

# Models cannot be change, becouse it has be the same as in message in json
-keep class com.studytor.app.models.** { *; }
#-keep class com.studytor.app.sync.** { *; }
#-keep class com.studytor.app.interfaces.** { *; }
#-keep class com.studytor.app.data.** { *; }

-keepattributes *Annotation*
-keepattributes Signature
#-keepattributes InnerClasses
-keep class org.apache.commons.logging.**
#-libraryjars libs/volley.jar
#-libraryjars libs/jsoup-1.7.3.jar
#-libraryjars libs/android.support.v4.jar
#-libraryjars libs/android-async-http-1.4.9.jar

### ----------------------------------
###   ########## Gson confusion    ##########
### ----------------------------------
#-keepattributes Signature
#-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.examples.android.model.** { *; }
#
## # -------------------------------------------
## #  ############### Volley confusion  ###############
## # -------------------------------------------
#-keep class com.android.volley.** {*;}
#-keep class com.android.volley.toolbox.** {*;}
#-keep class com.android.volley.Response$* { *; }
#-keep class com.android.volley.Request$* { *; }
#-keep class com.android.volley.RequestQueue$* { *; }
#-keep class com.android.volley.toolbox.HurlStack$* { *; }
#-keep class com.android.volley.toolbox.ImageLoader$* { *; }
#
## Needed just to be safe in terms of keeping Google API service model classes
#-keep class com.google.api.services.*.model.*
#-keep class com.google.api.client.**
#-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault