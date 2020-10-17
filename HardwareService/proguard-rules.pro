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

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#实际混淆指令,都是默认设置基本不用修改

# 设置混淆的压缩比率 0 ~ 7
-optimizationpasses 5
# 混淆后类名都为小写   Aa aA
-dontusemixedcaseclassnames
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
#不做预校验的操作
-dontpreverify
# 混淆时不记录日志
-verbose

# ----不做混淆优化----
-dontoptimize

#-keepattributes MethodParameters
# ----优化时允许访问并修改有修饰符的类和类的成员----
-allowaccessmodification

# 混淆采用的算法.
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保留代码行号，方便异常信息的追踪
-keepattributes SourceFile,LineNumberTable
#dump文件列出apk包内所有class的内部结构
-dump class_files.txt
#seeds.txt文件列出未混淆的类和成员
-printseeds seeds.txt
#usage.txt文件列出从apk中删除的代码
-printusage unused.txt
#mapping文件列出混淆前后的映射
-printmapping mapping.txt


#-------------------
# 打包时忽略以下类的警告
#-------------------
-dontwarn java.awt.**
-dontwarn android.test.**
-dontwarn android.support.v4.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }
-dontwarn android.support.v7.**
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }
-keep class android.support.v7.** { *; }
-keep class com.google.android.material.** {*;}

-keep class androidx.** {*;}

-keep public class * extends androidx.**

-keep interface androidx.** {*;}

-dontwarn com.google.android.material.**

-dontnote com.google.android.material.**

-dontwarn androidx.**

-dontwarn androidex.**

-dontwarn android_serialport_api.**

-keep class android.**{*;}

-keep class java.** {*;}

-keep public class * extends android.app.Activity # 保持哪些类不被混淆

-keep public class * extends android.app.Application # 保持哪些类不被混淆

-keep public class * extends android.app.Service # 保持哪些类不被混淆

-keep public class * extends android.content.BroadcastReceiver # 保持哪些类不被混淆

-keep public class * extends android.content.ContentProvider # 保持哪些类不被混淆

-keep public class * extends android.app.backup.BackupAgentHelper # 保持哪些类不被混淆

-keep public class * extends android.preference.Preference # 保持哪些类不被混淆

-keep public class com.android.vending.licensing.ILicensingService # 保持哪些类不被混淆



-keep class * implements android.os.Parcelable {#保持Parcelable不被混淆
   public static final android.os.Parcelable$Creator *;
}

# Java
-keep class * implements java.io.Serializable{*;}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    **[] $VALUES;
}

-keepattributes Exceptions
-keepattributes EnclosingMethod
-keepattributes InnerClasses


#避免混淆自定义控件类的get/set方法和构造函数
-keep public class * extends android.view.View{
        *** get*();
        void set*(***);
        public <init>(android.content.Context);
        public <init>(android.content.Context,android.util.AttributeSet);
        public <init>(android.content.Context,android.util.AttributeSet,int);
}

#避免混淆枚举类
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#避免混淆序列化类
-keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
}

#不混淆Serializable和它的实现子类、其成员变量
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

 #使用GSON、fastjson等框架时，所写的JSON对象类不混淆，否则无法将JSON解析成对应的对象
-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}

# linkkit API
-keep class com.aliyun.**{*;}

-keep class com.aliyuncs.**{*;}

-keep class com.alibaba.**{*;}

-keep class com.alipay.**{*;}

-keep class com.ut.**{*;}


-dontwarn com.aliyun.**

-dontwarn com.aliyuncs.**

-dontwarn com.alibaba.**

-dontwarn com.alipay.**

-dontwarn com.ut.**

# keep native method
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep netty
-keepattributes Signature,InnerClasses
-keepclasseswithmembers class io.netty.** {
    *;
}
-dontwarn io.netty.**
-dontwarn sun.**

# keep mqtt
-keep public class org.eclipse.paho.**{*;}

# keep fastjson
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*;}

# keep gson
-keep class com.google.gson.** { *;}

# keep network core
-keep class com.http.**{*;}

# keep okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keep class okio.**{*;}
-keep class okhttp3.**{*;}
-keep class org.apache.commons.codec.**{*;}

-keep class FileProvider{*;}
-keep class android.support.**{*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}




#天步sdk
-keep class com.tbtech.** {*;}
-dontwarn com.tbtech.**
-dontwarn com.csht.**
-dontwarn com.tbtech.**
-dontwarn com.mwp.**

-libraryjars libs/aliyun-java-sdk-core-4.5.0.jar
-libraryjars libs/hardware_can_V1.1.2.jar
-libraryjars libs/hardware_standard-V4.1.3.jar

#配置SDK混淆文件
-dontwarn com.auv.**

-keep class com.auv.standard.hardware.utils{
  *;
 }


-keep class  com.auv.model.** {*;}

-keep class  com.auv.annotation.** {*;}

-keep class  com.auv.annotation.Constant$** {*;}

-keep class  com.auv.annotation.Constant$**$** {*;}

-keep class  com.auv.alink.** {
     public <fields>;
     public <methods>;
  }
-keep class  com.auv.holder.** {
  public <fields>;
    public <methods>;
}
-keep class  com.auv.utils.**
-keep class  com.auv.utils.NetworkUtils {*;}
-keep class  com.auv.hardware.**

-keep class  com.auv.hardware.AUVIotConnectService {
         public <fields>;
         public <methods>;
 }

-keep class  com.auv.hardware.AUVIotConnectService$** {
        public <fields>;
        public <methods>;
}
-keep class  com.auv.hardware.HardwareService {
      public <fields>;
      public <methods>;
  }
-keep class  com.auv.hardware.HardwareService$** {
        public <fields>;
        public <methods>;
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**Event);
    void *(**Listener);
}

-keepclassmembers  class com.auv.** {
   public <init>();
   public static  <methods>;
  }


