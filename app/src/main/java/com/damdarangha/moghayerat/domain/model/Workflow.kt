package com.damdarangha.moghayerat.domain.model

/**
 * مدل گردش کار (Workflow)
 *
 * هر گردش کار یک مجموعه مستقل از محصولات است که آمارگیری آن‌ها
 * به‌صورت جداگانه انجام می‌شود. مثلاً «استریل» و «دبه و خرما».
 */
data class Workflow(
    val id: Long = 0L,
    val name: String,
    val displayOrder: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * وضعیت ثبت آمار یک محصول در روز خاص
 */
enum class StatStatus(val displayName: String) {
    NOT_REGISTERED("ثبت‌نشده"),
    DRAFT("پیش‌نویس"),
    REGISTERED("ثبت‌شده"),
    HAS_VARIANCE("دارای مغایرت"),
    FINALIZED("نهایی‌شده")
}
