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
#-keep class android.support.v4.** { *; }
#-keep interface android.support.v4.** { *; }

### -------------------------------------------------------
###                   Support libraries
### -------------------------------------------------------
### Support v7, Design
# http://stackoverflow.com/questions/29679177/cardview-shadow-not-appearing-in-lollipop-after-obfuscate-with-proguard/29698051
-keep class android.support.v7.widget.RoundRectDrawable { *; }

-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# https://github.com/Gericop/Android-Support-Preference-V7-Fix/blob/master/preference-v7/proguard-rules.pro
-keepclassmembers class android.support.v7.preference.PreferenceGroupAdapter {
    private ** mPreferenceLayouts;
}
-keepclassmembers class android.support.v7.preference.PreferenceGroupAdapter$PreferenceLayout {
    private int resId;
    private int widgetResId;
}

# keep setters in VectorDrawables so that animations can still work.
-keepclassmembers class android.support.graphics.drawable.VectorDrawableCompat$* {
   void set*(***);
   *** get*();
}

# Preference objects are inflated via reflection
-keep public class android.support.v7.preference.Preference {
  public <init>(android.content.Context, android.util.AttributeSet);
}
-keep public class * extends android.support.v7.preference.Preference {
  public <init>(android.content.Context, android.util.AttributeSet);
}
-keep class com.loopj.android.http.** { *; }
#-keep interface com.loopj.android.http.** { *; }
-keep class com.google.** { *; }
#-keep interface com.google.** { *; }

#-keep class com.google.gson.stream.** { *; }
#-keep class sun.misc.Unsafe { *; }

# Add any classes the interact with gson
########################    -keep class com.studytor.app.** { *; }

### -------------------------------------------------------
###                        Studytor
### -------------------------------------------------------
# Models cannot be change, becouse it has be the same as in message in json
-keep class com.studytor.app.models.** { *; }
-keep class com.studytor.app.repositories.models.** { *; }
-keep class com.studytor.app.repositories.webservices.** { *; }
-keep class com.studytor.app.preferences.SettingsActivity { *; }
-keep class com.studytor.app.sync.** { *; }
#-keep class com.studytor.app.interfaces.** { *; }
#-keep class com.studytor.app.data.** { *; }
-keep class com.studytor.app.activities.** { *; }
-keep class com.studytor.app.globals.** { *; }
-keep class com.studytor.app.helpers.** { *; }
-keep class com.studytor.app.views.** { *; }

### -------------------------------------------------------
###                         Other
### -------------------------------------------------------
-keepattributes *Annotation*
-keepattributes Signature
#-keepattributes InnerClasses
-keep class org.apache.commons.logging.**

## Needed just to be safe in terms of keeping Google API service model classes
#-keep class com.google.api.services.*.model.*
#-keep class com.google.api.client.**
#-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

### -------------------------------------------------------
###   Retrofit and Gson and OkHttp (for Picasso as well)
### -------------------------------------------------------
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
# OkHttp
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
# Retrofit uses Okio under the hood, so...
-dontwarn okio.**
# OkHttp3
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

### -------------------------------------------------------
###            Android architecture components
### -------------------------------------------------------
## Android architecture components: Lifecycle
# LifecycleObserver's empty constructor is considered to be unused by proguard
-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}
# ViewModel's empty constructor is considered to be unused by proguard
-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
    <init>(...);
}
# keep Lifecycle State and Event enums values
-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }
-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }
# keep methods annotated with @OnLifecycleEvent even if they seem to be unused
# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
-keepclassmembers class * {
    @android.arch.lifecycle.OnLifecycleEvent *;
}

-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}

-keep class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}
-keepclassmembers class android.arch.** { *; }
-keep class android.arch.** { *; }
-dontwarn android.arch.**
-keep class * implements android.arch.lifecycle.GeneratedAdapter {<init>(...);}

### -------------------------------------------------------
###               Room Persistence Library
### -------------------------------------------------------
-keep class android.arch.persistence.room.paging.LimitOffsetDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource
-keep class android.arch.util.paging.CountedDataSource
-dontwarn android.arch.util.paging.CountedDataSource

### -------------------------------------------------------
###                   Constraint Layout
### -------------------------------------------------------
-dontwarn android.support.constraint.**
-keep class android.support.constraint.** { *; }
-keep interface android.support.constraint.** { *; }
-keep public class android.support.constraint.R$* { *; }

### -------------------------------------------------------
###                 Removing logging code
### -------------------------------------------------------
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
# Logging statements often contain implicit calls that perform string concatenation.
# They no longer serve a purpose after the logging calls have been removed.
# You can let ProGuard clean up such constructs as well by providing additional hints:
-assumenoexternalsideeffects class java.lang.StringBuilder {
    public java.lang.StringBuilder();
    public java.lang.StringBuilder(int);
    public java.lang.StringBuilder(java.lang.String);
    public java.lang.StringBuilder append(java.lang.Object);
    public java.lang.StringBuilder append(java.lang.String);
    public java.lang.StringBuilder append(java.lang.StringBuffer);
    public java.lang.StringBuilder append(char[]);
    public java.lang.StringBuilder append(char[], int, int);
    public java.lang.StringBuilder append(boolean);
    public java.lang.StringBuilder append(char);
    public java.lang.StringBuilder append(int);
    public java.lang.StringBuilder append(long);
    public java.lang.StringBuilder append(float);
    public java.lang.StringBuilder append(double);
    public java.lang.String toString();
}
-assumenoexternalreturnvalues public final class java.lang.StringBuilder {
    public java.lang.StringBuilder append(java.lang.Object);
    public java.lang.StringBuilder append(java.lang.String);
    public java.lang.StringBuilder append(java.lang.StringBuffer);
    public java.lang.StringBuilder append(char[]);
    public java.lang.StringBuilder append(char[], int, int);
    public java.lang.StringBuilder append(boolean);
    public java.lang.StringBuilder append(char);
    public java.lang.StringBuilder append(int);
    public java.lang.StringBuilder append(long);
    public java.lang.StringBuilder append(float);
    public java.lang.StringBuilder append(double);
}
