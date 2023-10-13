# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep the classes of Kerdy class itself and its fields from obfuscation
-keep class com.emmsale.data.apiModel.response.UpdatedMemberInterestEventTagResponse { *; }
-keep class com.emmsale.data.common.callAdapter.* { *; }

# Keep the classes of Kerdy class itself and its fields from obfuscation
-keep class com.emmsale.data.repository.concretes.DefaultPostRepository { *; }
-keep class com.emmsale.data.service.** {*;}

# Keep the classes of Kerdy class itself and its fields from obfuscation
-keep class com.emmsale.data.repository.concretes.DefaultLoginRepository { *; }
-keep class com.emmsale.presentation.ui.login.** {*;}

# Keep the classes of Kerdy class itself and its fields from obfuscation
-keep class com.emmsale.data.** {*;}

