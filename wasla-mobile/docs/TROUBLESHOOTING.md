# 🔧 حل المشاكل - Troubleshooting

## ⚠️ تحذيرات Assets في VS Code

### المشكلة
```
The asset directory 'assets/images/' doesn't exist.
The asset directory 'assets/icons/' doesn't exist.
```

### السبب
VS Code لم يكتشف المجلدات الجديدة بعد (cache issue).

### الحل

#### الطريقة 1: إعادة تحميل النافذة (موصى بها) ⭐
```
1. اضغط Ctrl+Shift+P (أو Cmd+Shift+P على Mac)
2. اكتب "Reload Window"
3. اختر "Developer: Reload Window"
```

#### الطريقة 2: إعادة فتح المشروع
```
1. أغلق VS Code بالكامل
2. افتح المشروع مرة أخرى
```

#### الطريقة 3: تشغيل Flutter
```bash
flutter pub get
flutter clean
flutter pub get
```

#### الطريقة 4: تجاهل التحذير
```
⚠️ التحذير لا يؤثر على عمل التطبيق
✅ المجلدات موجودة فعلاً
✅ التطبيق سيعمل بشكل طبيعي
```

### التحقق من وجود المجلدات
```bash
# Windows PowerShell
Test-Path "assets/images"  # يجب أن يعطي True
Test-Path "assets/icons"   # يجب أن يعطي True

# Linux/Mac
ls -la assets/
```

### النتيجة المتوقعة
```
assets/
├── images/
│   └── .gitkeep
├── icons/
│   └── .gitkeep
└── README.md
```

---

## 🐛 مشاكل شائعة أخرى

### 1. أخطاء في الكود

#### المشكلة
```
The getter 'textMain' isn't defined for the type 'AppColors'
```

#### الحل
✅ تم إصلاحه! استخدم:
- `AppColors.textPrimary` بدلاً من `AppColors.textMain`
- `AppColors.textSecondary` بدلاً من `AppColors.textGrey`

---

### 2. مكتبات مفقودة

#### المشكلة
```
Target of URI doesn't exist: 'package:image_picker/image_picker.dart'
```

#### الحل
✅ تم إصلاحه! المكتبة تم إزالتها لأن التطبيق 100% نصي.

---

### 3. Backend غير متصل

#### المشكلة
```
DioException: Connection refused
```

#### الحل
تأكد من تشغيل Spring Boot backend:
```bash
# تحقق من أن Backend يعمل على:
http://localhost:8080
```

---

## 📝 ملاحظات مهمة

### التطبيق 100% نصي
```
🚫 لا يوجد رفع صور
🚫 لا يوجد image_picker
🚫 لا يوجد cached_network_image
✅ نصي فقط
```

### مجلد Assets
```
📁 assets/images/  - للصور الثابتة فقط (شعار، أيقونات)
📁 assets/icons/   - للأيقونات المخصصة
⚠️ ليس لصور المستخدمين
```

---

## 🆘 المساعدة

إذا واجهت مشكلة أخرى:

1. راجع `docs/README.md`
2. راجع `docs/DEVELOPER_GUIDE.md`
3. راجع `docs/FINAL_SESSION_REPORT.md`

---

**آخر تحديث:** 2026-01-15
