# Preserve generic type information used by Gson
-keepattributes Signature
-keepattributes *Annotation*

# Keep fields annotated with Gson annotations
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Preserve Retrofit service interfaces
-keep interface retrofit2.** { *; }
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

# Preserve model classes used by Gson
-keep class com.google.gson.** { *; }