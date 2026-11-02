# TUBE TV PRO OFFLINE MAX

## بناء APK عبر GitHub Actions
1. ارفع محتويات هذا المشروع إلى مستودع GitHub.
2. تأكد أن الفرع اسمه `main`، أو عدّل اسم الفرع في `.github/workflows/build-apk.yml`.
3. افتح تبويب **Actions**.
4. اختر **Build Android APK** ثم **Run workflow**.
5. بعد نجاح البناء افتح نتيجة التشغيل، ثم حمّل الملف من **Artifacts** باسم `TUBE-TV-PRO-OFFLINE-MAX-debug`.

## إصلاح Gradle
لم يكن المشروع يحتوي على ملفات Gradle Wrapper (`gradlew` وملفات `gradle/wrapper`). لذلك تم تعديل سير العمل ليُثبت Gradle 8.9، وينشئ ملفات الـ Wrapper أثناء التشغيل، ثم ينفذ البناء باستخدام `./gradlew`.

## ملاحظة
هذا المشروع يُنتج APK تجريبياً من نوع Debug. لم يتم تشغيل بناء Android فعلي داخل هذه البيئة؛ يجب التأكد من نجاحه في GitHub Actions.
