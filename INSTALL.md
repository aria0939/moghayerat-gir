# راهنمای نصب فونت و ساخت APK

<div dir="rtl">

## مرحله ۱: نصب فونت Vazirmatn (توصیه می‌شود)

برای نمایش صحیح فونت فارسی، فایل‌های فونت Vazirmatn را دانلود و در پروژه قرار دهید:

### روش ۱: دانلود از گیت‌هاب

1. به آدرس https://github.com/rastikerdar/vazirmatn بروید
2. در بخش Releases، آخرین نسخه را دانلود کنید
3. فایل ZIP را استخراج کنید
4. به پوشه `fonts/ttf/` بروید
5. این سه فایل را در مسیر زیر کپی کنید:
   ```
   app/src/main/res/font/
   ├── vazirmatn_regular.ttf
   ├── vazirmatn_medium.ttf
   └── vazirmatn_bold.ttf
   ```

### روش ۲: دانلود مستقیم

می‌توانید فایل‌ها را مستقیماً از این آدرس‌ها دانلود کنید:

- https://github.com/rastikerdar/vazirmatn/raw/master/fonts/ttf/Vazirmatn-Regular.ttf
- https://github.com/rastikerdar/vazirmatn/raw/master/fonts/ttf/Vazirmatn-Medium.ttf
- https://github.com/rastikerdar/vazirmatn/raw/master/fonts/ttf/Vazirmatn-Bold.ttf

سپس نام فایل‌ها را به حروف کوچک و با خط زیر تبدیل کنید:
- `Vazirmatn-Regular.ttf` → `vazirmatn_regular.ttf`
- `Vazirmatn-Medium.ttf` → `vazirmatn_medium.ttf`
- `Vazirmatn-Bold.ttf` → `vazirmatn_bold.ttf`

**نکته**: اگر این فایل‌ها را اضافه نکنید، برنامه از فونت پیش‌فرض سیستم استفاده می‌کند که ممکن است زیبا نباشد، اما عملکرد برنامه تحت تأثیر قرار نمی‌گیرد.

## مرحله ۲: باز کردن پروژه در Android Studio

1. Android Studio را باز کنید (نسخه Hedgehog 2023.1.1 یا بالاتر)
2. از منوی File → Open را انتخاب کنید
3. به پوشه `moghayerat-gir` بروید و OK را بزنید
4. چند دقیقه صبر کنید تا Gradle همگام‌سازی شود
5. در صورت درخواست دانند SDK، تأیید کنید

## مرحله ۳: بررسی Gradle Sync

پس از باز کردن پروژه:

- در صورت بروز خطای همگام‌سازی، روی `Try Again` کلیک کنید
- در صورت درخواست دانلود Android SDK 34، آن را تأیید کنید
- در صورت بروز خطای JDK، مطمئن شوید JDK 17 نصب است

## مرحله ۴: ساخت APK

### APK دیباگ (سریع، برای تست)

1. از منوی Build → Build Bundle(s) / APK(s) → Build APK(s) را انتخاب کنید
2. چند دقیقه صبر کنید
3. پس از اتمام، روی notification کلیک کنید
4. یا فایل را در مسیر زیر پیدا کنید:
   ```
   app/build/outputs/apk/debug/moghayerat-gir-debug-1.0.0.apk
   ```

### APK نهایی (Release، برای انتشار)

1. از منوی Build → Generate Signed Bundle / APK را انتخاب کنید
2. APK را انتخاب کنید و Next را بزنید
3. اگر keystore ندارید، روی Create new کلیک کنید و یک keystore بسازید
4. اطلاعات را پر کنید و Next را بزنید
5. variant را release انتخاب کنید
6. Finish را بزنید
7. APK نهایی در مسیر زیر ساخته می‌شود:
   ```
   app/build/outputs/apk/release/moghayerat-gir-release-1.0.0.apk
   ```

## مرحله ۵: نصب روی گوشی

### روش ۱: از طریق Android Studio

1. گوشی را با کابل USB به کامپیوتر وصل کنید
2. حالت USB Debugging را فعال کنید
3. در Android Studio، روی دکمه Run (سبز) کلیک کنید
4. گوشی خود را انتخاب کنید

### روش ۲: نصب دستی APK

1. فایل APK را به گوشی منتقل کنید
2. در تنظیمات گوشی، "نصب از منابع ناشناخته" را فعال کنید
3. فایل APK را با File Manager باز کنید
4. روی Install کلیک کنید

## اجرای تست‌ها

برای اطمینان از صحت محاسبات، تست‌های واحد را اجرا کنید:

### از طریق Android Studio

1. در پنجره Project، به مسیر `app/src/test/java` بروید
2. روی پوشه `com.damdarangha.moghayerat` راست‌کلیک کنید
3. گزینه Run Tests را انتخاب کنید

### از طریق خط فرمان

```bash
cd moghayerat-gir
./gradlew test
```

## رفع مشکلات رایج

### مشکل: خطای "Cannot resolve dependency"

**راه‌حل**:
1. فایل `build.gradle.kts` (ریشه) را باز کنید
2. مطمئن شوید مخازن google() و mavenCentral() وجود دارند
3. Gradle Sync را مجدداً اجرا کنید

### مشکل: خطای "SDK location not found"

**راه‌حل**:
1. فایل `local.properties` در ریشه پروژه ایجاد کنید (اگر وجود ندارد)
2. خط زیر را اضافه کنید (با مسیر واقعی SDK):
   ```
   sdk.dir=C:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
   ```
   یا در لینوکس/مک:
   ```
   sdk.dir=/home/yourname/Android/Sdk
   ```

### مشکل: خطای "JDK 17 not found"

**راه‌حل**:
1. JDK 17 را از [سایت اوراکل](https://www.oracle.com/java/technologies/downloads/) دانلود کنید
2. در Android Studio: File → Project Structure → SDK Location
3. مسیر JDK 17 را تنظیم کنید

### مشکل: خطای "Failed to sync Gradle"

**راه‌حل**:
1. فایل `gradle/wrapper/gradle-wrapper.properties` را باز کنید
2. مطمئن شوید URL صحیح است
3. در صورت نیاز، آینه‌ای (mirror) دیگر استفاده کنید

### مشکل: برنامه کرش می‌کند

**راه‌حل**:
1. در Android Studio، Logcat را باز کنید
2. برنامه را اجرا کنید
3. خطا را در Logcat ببینید
4. در صورت نیاز، باگ را گزارش دهید

## ساختار فایل‌های مهم

| فایل | توضیح |
|------|-------|
| `app/build.gradle.kts` | وابستگی‌ها و تنظیمات ساخت |
| `app/src/main/AndroidManifest.xml` | تنظیمات برنامه |
| `app/src/main/java/com/damdarangha/moghayerat/` | سورس کد Kotlin |
| `app/src/main/res/` | منابع (رنگ، فونت، آیکون) |
| `app/src/test/` | تست‌های واحد |
| `gradle/wrapper/` | تنظیمات Gradle Wrapper |

## پشتیبانی

در صورت بروز هرگونه مشکل:

1. ابتدا فایل README.md را مطالعه کنید
2. در صورت نیاز، یک فایل پشتیبان از داده‌ها بسازید
3. خطا را با جزئیات (شامل Logcat) گزارش دهید

## مجوز

این سورس کد برای استفاده داخلی دامداران و تیم فنی آن‌ها ارائه شده است. لطفاً بدون اجازه، آن را به اشتراک نگذارید.

</div>
