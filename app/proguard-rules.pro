
-ignorewarnings
-keep class * {
    public private *;
}
# Strip `Log.v`, `Log.d`, and `Log.i` statements, leave `Log.w` and `Log.e` intact.
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

-keep class .R
-keep class **.R$* { <fields>; }
-keepclasseswithmembers class **.R$* { public static final int define_*; }

-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

-dontnote com.mikepenz.fastadapter.items.**

-dontwarn okio.**

# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn com.squareup.okhttp.**

-keepattributes Signature

-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource

-dontwarn android.arch.**

-dontwarn org.xmlpull.v1.**

# Crashlytics
-keepattributes *Annotation*
-keep @**annotation** class * {*;}
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
