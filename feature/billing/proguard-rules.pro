# Billing module ProGuard rules
# Keep Play Billing Library classes
-keep class com.android.billingclient.api.** { *; }

# Keep product IDs
-keepclassmembers class com.epaperlauncher.feature.billing.domain.model.ProProduct {
    public static ** *;
}
