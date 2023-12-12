# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# ApiResponse 클래스 축소 및 난독화 해제하여 CallAdapter에서 retrofit2.Call<ApiResponse>를 반환하는 CallAdapter 만들 수 있도록 변경
-keepnames class com.emmsale.data.common.retrofit.callAdapter.ApiResponse
