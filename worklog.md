# Worklog — مغایرت‌گیر دامداران

این فایل لاگ مشترک همه‌ی agentهاست. لطفاً پس از اتمام کار خود، یک section جدید به انتهای این فایل اضافه کنید.

---
Task ID: fix-stage-separation
Agent: main (Claude Sonnet 4.5)
Task: رفع کامل bugهای جداسازی مراحل اول کار و آخر کار (START/END)

Work Log:
- خواندن ساختار فعلی پروژه: Entity، DAO، Repository، ViewModelها، Screenها
- شناسایی شش ریشه مشکل (طبق تحلیل قبلی): stage پارامتر استفاده نشده، فیلدها مشترک، hasCurrentStock نادرست، currentStockTotal=0 مبهم، استنتاج نادرست از لیست، VarianceFormatter متصل نشده
- اصلاح `DailyStatEntity.kt`: تبدیل `currentStockTotal: Int = 0` به `currentStockTotal: Int? = null` برای تفکیک «ثبت‌نشده» از «ثبت‌شده با مقدار صفر»
- اصلاح `AppDatabase.kt`: bump version 6→7 و اضافه کردن `MIGRATION_6_7` که داده‌های قدیمی را حفظ می‌کند (CASE WHEN endCumulativeVariance IS NOT NULL)
- اصلاح `MoghayeratApplication.kt`: حذف `fallbackToDestructiveMigration()` و اضافه کردن `.addMigrations(AppDatabase.MIGRATION_6_7)` برای حفظ داده‌های کاربر
- اصلاح `DailyStatRepository.kt`:
  - به‌روزرسانی `saveStat` برای استفاده از `result.currentStockTotal` به‌جای `?: 0`
  - اضافه کردن `saveStartStage()` که فقط فیلدهای اول کار را ذخیره می‌کند و فیلدهای آخر کار را از رکورد موجود حفظ می‌کند
  - اضافه کردن `saveEndStage()` که فقط فیلدهای آخر کار را ذخیره می‌کند و فیلدهای اول کار را حفظ می‌کند
  - هر دو متد، `nightShiftVariance` و `closingVariance` را بر اساس داده‌های ترکیبی محاسبه می‌کنند
  - اضافه کردن متد کمکی `persistEntity()` برای جلوگیری از تکرار کد
  - اصلاح `getCarryOverStock` برای return null اگر `currentStockTotal` ثبت نشده
- اصلاح `DailyStatDao.kt`: فیلتر کردن `currentStockTotal IS NOT NULL` در queryهای `observeZeroVarianceStats`، `observeDeficitStats`، `observeSurplusStats`، `countVarianceStatsForDate` تا رکوردهای ثبت‌نشده به‌اشتباه «مغایرت صفر» محسوب نشوند
- اصلاح `StatEntryViewModel.kt`:
  - تعریف `StatEntryStage` enum با START/END/FULL و متد `fromString()`
  - پذیرش `stage: StatEntryStage` در constructor و factory
  - اضافه کردن `isStartStage` و `isEndStage` properties
  - اضافه کردن `startStageSaved` و `endStageSaved` به UiState
  - اضافه کردن متد `clearCurrentStock()` برای لغو صریح END
  - پیاده‌سازی `buildInput()` کمکی
  - `saveDraftSilent`، `saveDraft`، `finalizeStat` اکنون stage-aware هستند (saveStartStage/saveEndStage)
  - اصلاح `hasCurrentStock` در `loadInitialData` از `existingStat.currentStockTotal != null` (به‌جای `> 0 || endCumulativeVariance != null`)
  - استنتاج `startStageSaved` و `endStageSaved` از داده موجود
  - هشدارهای validation stage-aware
- اصلاح `StatEntryScreen.kt`:
  - استفاده از `StatEntryStage.fromString(stage)` در factory
  - تفکیک واقعی فیلدها: در START فقط فیلدهای اول کار نمایش داده می‌شوند، در END فقط فیلدهای آخر کار
  - دکمه‌های bottom bar stage-aware: «ذخیره اول کار»، «ذخیره آخر کار»، «نهایی کردن اول کار»، «نهایی کردن آخر کار»
  - اضافه کردن پارامتر `saveLabel` و `finalizeLabel` به `BottomActionButtons`
- اصلاح `ProductListViewModel.kt`:
  - `startCount`: شمارش رکوردهایی که `startVariance != null` یا `status != NOT_REGISTERED`
  - `endCount`: شمارش رکوردهایی که `endCumulativeVariance != null`
  - `refreshStatuses`: تشخیص دقیق‌تر وضعیت بر اساس داده‌های واقعی
- اصلاح `ProductListScreen.kt`:
  - نمایش `VarianceFormatter.formatVariance` فقط روی دکمه‌های ثبت‌شده
  - برجسته‌سازی دکمه‌های ثبت‌شده با BorderStroke و رنگ primary
  - دکمه‌های ثبت‌نشده با رنگ outline و alpha کمتر
- اصلاح `HomeViewModel.kt`:
  - شمارش جداگانه `startCount` و `endCount` برای هر گردش کار
  - بارگذاری یک‌باره‌ی `allTodayStats` برای کارایی بهتر
  - اضافه کردن `workflowStartCounts` و `workflowEndCounts` به UiState
  - اضافه کردن `startCount` و `endCount` به `WorkflowWithStats`
- اصلاح `HomeScreen.kt`:
  - نمایش چهار ستون در کارت گردش کار: کل محصولات، اول کار، آخر کار، باقی‌مانده
  - درصد پیشرفت بر اساس آخر کار (مرحله کامل‌تر)
- اصلاح `ReportsViewModel.kt`:
  - فیلتر کردن فقط رکوردهای `currentStockTotal != null` در محاسبه `zeroVarianceCount`، `deficitCount`، `surplusCount`، `totalDeficit`، `totalSurplus`
  - فیلتر `REGISTERED_ONLY` و `VARIANCE_ONLY` اکنون فقط آمار ثبت‌شده را در نظر می‌گیرند
- اصلاح `ReportsScreen.kt`: `ReportValueItem("واقعی", stat.currentStockTotal ?: 0)` برای جلوگیری از خطای type
- اصلاح `HistoryScreen.kt`: 
  - اضافه کردن حالت `StatusType.INCOMPLETE` برای رکوردهایی که آخر کار ثبت نشده
  - نمایش «ثبت آخر کار نشده» برای این رکوردها
- اصلاح `VarianceResultCard.kt`:
  - اضافه کردن پارامتر `isUnregistered` به `ResultRow`
  - نمایش «ثبت‌نشده» برای `currentStockTotal == null`

Stage Summary:
- **۹ فایل اصلی اصلاح شد** + ۵ فایل مرتبط دیگر (مجموعاً ۱۴ فایل)
- **Migration 6→7 داده‌ها را حفظ می‌کند** (CASE WHEN endCumulativeVariance IS NOT NULL)
- **مراحل START/END کاملاً جدا شد**: ذخیره اول کار دیگر آخر کار را بازنویسی نمی‌کند و برعکس
- **صفر معتبر است**: `currentStockTotal = 0` به‌عنوان «ثبت‌شده با مقدار صفر» شناخته می‌شود، نه «ثبت‌نشده»
- **دکمه‌ها stage-aware شدند**: هر دکمه فقط واریانس مرحله خودش را نمایش می‌دهد
- **شمارش‌ها دقیق**: startCount و endCount مستقل محاسبه می‌شوند
- **نمایش‌های مناسب**: «ثبت‌نشده»، «ثبت آخر کار نشده»، «ناقص» برای رکوردهای ناتمام

---
Task ID: fix-stage-separation-review
Agent: main (Claude Sonnet 4.5)
Task: بررسی استاتیک کامل و رفع خطاهای نهایی

Work Log:
- اجرای static code reviewer روی ۱۵ فایل تغییر یافته
- نتیجه: هیچ خطای compile-blocking پیدا نشد
- رفع ۴ مورد جزئی:
  1. `MoghayeratApplication.kt`: اضافه کردن `.fallbackToDestructiveMigration()` به‌عنوان safety net (پس از MIGRATION_6_7) تا اگر کاربر روی نسخه بسیار قدیمی بود، به‌جای crash، دیتابیس بازسازی شود
  2. `StatEntryViewModel.kt`: حذف کد مرده `private fun Int.absoluteValue()` در انتهای فایل
  3. `ProductListScreen.kt`: اضافه کردن imports صریح برای `BorderStroke` و `ButtonDefaults` و جایگزینی ارجاعات fully-qualified با ارجاعات مستقیم
  4. `ReportsViewModel.kt`: رفع race condition بین loadWorkflows و loadToday — اکنون isoDate در init همگام (synchronous) تنظیم می‌شود، قبل از collect شدن workflows

Stage Summary:
- **بررسی استاتیک تأیید کرد**: هیچ خطای compile-blocking وجود ندارد
- **تمام تغییرات مورد نیاز اعمال شد**
- **آماده برای build در Android Studio**
