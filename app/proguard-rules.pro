# ProGuard rules for WelshDAG Scanner

# Keep the application class
-keep class com.welshdag.scanner.WelshDagApp
-keep class com.welshdag.scanner.MainActivity

# Keep all Composables
-keep class com.welshdag.scanner.ui.screens.** { *; }
-keep class com.welshdag.scanner.ui.theme.** { *; }

# Keep ViewModels
-keep class com.welshdag.scanner.ui.viewmodel.** { *; }

# Keep data classes and models
-keep class com.welshdag.scanner.network.** { *; }
-keepclassmembers class com.welshdag.scanner.network.** { *; }

# Keep repository
-keep class com.welshdag.scanner.data.** { *; }

# Keep security and storage
-keep class com.welshdag.scanner.security.** { *; }

# Keep Hilt-generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.** { *; }
-keepclassmembers class * {
    @dagger.hilt.** <fields>;
    @dagger.hilt.** <methods>;
}

# Keep Retrofit interfaces
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keep interface com.welshdag.scanner.network.** { *; }

# Keep Gson models (used by Retrofit)
-keepclassmembers class ** {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.google.gson.** { *; }

# Keep OkHttp
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Keep datastore
-keep class androidx.datastore.** { *; }

# Keep Android lifecycle components
-keep class androidx.lifecycle.** { *; }

# Keep Web3j

# Keep coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep BuildConfig
-keep class com.welshdag.scanner.BuildConfig { *; }

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
