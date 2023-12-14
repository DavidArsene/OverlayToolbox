# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontobfuscate

-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite { <fields>; }
-assumevalues class com.google.protobuf.Android { static boolean ASSUME_ANDROID return true; }

-assumevalues class androidx.recyclerview.widget.RecyclerView {
    static boolean sDebugAssertionsEnabled return false;
    static boolean sVerboseLoggingEnabled return false;
}

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
	static void checkNotNull(...);
	static void checkExpressionValueIsNotNull(...);
	static void checkNotNullExpressionValue(...);
	static void checkParameterIsNotNull(...);
	static void checkNotNullParameter(...);
	static void checkReturnedValueIsNotNull(...);
	static void checkFieldIsNotNull(...);
	static void throwUninitializedPropertyAccessException(...);
	static void throwNpe(...);
	static void throwJavaNpe(...);
	static void throwAssert(...);
	static void throwIllegalArgument(...);
	static void throwIllegalState(...);
}

-assumenosideeffects class android.util.Log {
    static *** d(...);
    static *** v(...);
    static *** i(...);
    static *** w(...);
    static *** e(...);
    static *** wtf(...);
    static *** isLoggable(...) return false;
}

-assumenosideeffects class android.view.View { boolean isInEditMode() return false; }

# Eliminate null checks in view binding classes
-assumevalues class androidx.viewbinding.ViewBindings {
    static *** findChildViewById(...) return 1..2147483647;
}

#-assumenosideeffects class java.lang.Object {
#    *** getClass() return null;
#}
#
#-assumenosideeffects class ** extends java.lang.Object {
#    *** getClass() return null;
#}

# For androidx.recyclerview:recyclerview
-keep public class androidx.recyclerview.widget.LinearLayoutManager {
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
    public <init>();
}

-keep public class com.google.android.material.appbar.AppBarLayout$ScrollingViewBehavior {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>();
}

