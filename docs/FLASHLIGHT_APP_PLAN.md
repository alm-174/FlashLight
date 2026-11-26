# Kế hoạch app Flashlight – màn hình, kiến trúc, công nghệ

Tài liệu dựng từ file Figma **Love_Tester** (các section: Splash + Language + OBD + Internet + Permission,
Flash Light, ScreenLight, LED, Flash Alert, Edge Lighting, background, Settings, và các component
Rate us, fill name, Icon, Button, Frame 1707478416).
Mục đích: để bạn **tự tay viết** app. Tài liệu nói *làm gì, đặt ở đâu, vì sao*, không viết code hộ.

> Những chỗ ghi **(cần xác nhận)** là điều Figma không thể hiện rõ. Hỏi người hướng dẫn trước khi làm.

---

## 1. App này làm gì

Một app "đèn" đa năng, giao diện tối (dark), màu nhấn xanh lá. Có 5 tính năng chính, đều mở từ màn Home:

| Tính năng | Tóm tắt |
|---|---|
| **Flash** | Bật/tắt đèn flash, chế độ SOS, chế độ DJ (nháy theo nhịp) |
| **Screen Light** | Biến màn hình thành đèn màu, chỉnh độ sáng và màu |
| **LED Banner** | Chữ chạy toàn màn hình (ngang), chỉnh font, tốc độ, màu, hướng, hiệu ứng, ảnh nền |
| **Flash Alert** | Nháy flash khi có cuộc gọi, SMS, thông báo từ app được chọn |
| **Edge Lighting** | Viền sáng chạy quanh mép màn hình, nhiều kiểu viền, màu, notch |

Kèm theo: Camera (soi/kính lúp có đèn), Settings (ngôn ngữ, chia sẻ, đánh giá, chính sách).

---

## 2. Danh sách màn hình đọc từ Figma

### 2.1 Khởi động (section "Splash + Language + OBD + Internet + Permission")

| Frame | Nội dung | Ghi chú |
|---|---|---|
| **Splash** | Nền gradient xanh, logo + chữ "Flashlight" | Dùng Android SplashScreen API |
| **Language** | Tiêu đề "Language", danh sách 11 ngôn ngữ có cờ + nút tròn chọn | Chưa chọn thì **chưa có** nút Save |
| **Language_Select** | Như trên, đã chọn 1 ngôn ngữ → hiện nút **Save** góc phải | Cùng một màn, chỉ khác trạng thái |
| **OBD1–OBD4** | 4 trang giới thiệu: Bright Flashlight → SOS & DJ Flash → Color Screen Light → Custom LED Banner. Có chấm chỉ trang, nút **Next**, trang cuối **Get Started!** | Nửa dưới mỗi trang trống: chỗ dành cho quảng cáo native **(cần xác nhận)** |
| **No internet** (2 frame) | "Oops, No Internet Connection", nút **Go to Settings** / **Cancel**. Một bản toàn màn hình, một bản dạng hộp thoại | Cùng nội dung, 2 cách hiển thị |
| **Permission** (2 frame) | "[App name] is requesting access…", công tắc **Camera**, nút **Continue** (xám khi chưa cấp, xanh khi đã cấp) | 2 trạng thái của cùng một màn |

11 ngôn ngữ: Tiếng Việt, Español, हिन्दी, English, Português, 日本語, Deutsch, 한국어, Français, Italiano, Indonesia.

### 2.2 Home (trong section "Flash Light", frame **Homepage**)

Một màn **cuộn dọc**, **không có thanh tab dưới**:
- Header: "App Name", "Light up anytime, anywhere.", nút **Camera** và nút **Settings**.
- Thẻ lớn **LED** (Hero): dòng chữ chạy tự động (frame *Text Auto*), "Speed: 5s", "RGB Multi-Color", nút **Play Now!**, link **Custom Text →**.
- 2 thẻ nhỏ: **Flash** (nút *Explore Modes*), **Flash Alert** (nút *Set Up Alerts*).
- Thẻ **Screenlight**: 5 màu preset + link **Adjust**, nút →.
- Thẻ **Edge Lighting**: thanh màu "Aura Rainbow FX", nút →.

### 2.3 Flash (section "Flash Light")

| Frame | Nội dung |
|---|---|
| Flash Light_Off / _On | Tiêu đề "Flash", **3 tab trên**: Flash Light · SOS Light · DJ Mode. Giữa màn là nút tròn lớn, chữ OFF/ON, sáng xanh khi bật |
| SOS Light_Off / _On | Tab SOS, icon đèn báo động |
| DJ Mode_Off / _On | Tab DJ, icon quả cầu disco |
| Camera | Xem camera toàn màn hình, thanh zoom (− / +), nút **X**, nút **chụp**, nút **bật đèn** |

### 2.4 Screen Light (section "ScreenLight")

| Frame | Nội dung |
|---|---|
| ScreenLight | Ô xem trước màu (có nút phóng to), thanh **Brightness** (80%), lưới **Color** (nút bảng màu + 6 màu) |
| ScreenLight (hộp thoại) | **Bộ chọn màu**: vùng chọn, thanh Hue, thanh Alpha, ô HEX / R / G / B, nút **Choose** |
| Play | Màn **ngang** toàn màu, chỉ có nút **X** |

### 2.5 LED Banner (section "LED")

| Frame | Nội dung |
|---|---|
| LED | Xem trước (có nút phóng to), ô **Text**, thanh **Font Size** (64px) và **Scroll Speed** (5s), **Color** (như Screen Light), **Direction** (→ ← ↓ ↑), **Visual Effects** (Glow, Blink, Neon, Fade), **Background** (nút + và các ảnh nền) |
| LED (hộp thoại) | Bộ chọn màu (giống Screen Light) |
| Play | Màn **ngang** đen, chữ phát sáng chạy, nút **X** |
| background (section riêng) | 10 ảnh nền có sẵn cho LED (DJ, sân khấu, đàn guitar…) → đưa vào `drawable-nodpi` hoặc `assets` |

### 2.6 Flash Alert (section "Flash Alert")

| Frame | Nội dung |
|---|---|
| Flash Alert | 3 mục: **Incoming Calls**, **SMS**, **Notification** ("Tap for more settings") |
| Incoming Calls | Icon lớn, công tắc **Status**, khối **Flashing Speed** (Flashing ON 0.5s, Flashing OFF 0.5s), nút **TEST** |
| SMS | Giống Incoming Calls, icon tin nhắn |
| Notification | Giống trên, thêm dòng **Select App** (hiện icon các app đã chọn) |
| Select App (2 frame) | Danh sách app trong máy, nút tròn chọn, **Select All**; khi có chọn thì hiện nút **Done** |
| …\| Test (Incoming Calls / SMS 2 / Notification) | Màn giả lập: cuộc gọi đến, khung chat, thông báo trên màn khoá, để **thử** nháy flash |

### 2.7 Edge Lighting (section "Edge Lighting")

| Frame | Nội dung |
|---|---|
| Edge Lighting | Công tắc **Enable Edge Lighting to begin**; **Color** (+ thêm màu, *Edit*); **Border Style** (tab Weather & Nature, Arrow, Commerce… và lưới icon); **Run Style** (None, Rotation, Run, R Run, Crawler, R Crawler, Snake, R Snake); **Border** (Top Radius, Bottom Radius, Width, Color Speed); **Notch Style** (Full, Water, …); nút **Apply**. Khi bật, viền màn hình chạy hiệu ứng |
| Add more color | Hộp thoại **Add Border Color** (bộ chọn màu + Apply/Cancel) |
| Edit | Hộp thoại **Edit Border Color**: các màu đã thêm, mỗi màu có nút xoá, nút **Choose**/Cancel |
| Apply | Hộp thoại **Choose Display Mode**: công tắc **HomeScreen**, **LockScreen**, **Overlay All Apps**, nút **Apply**/Cancel |

### 2.8 Settings (section "Settings")

| Frame | Nội dung |
|---|---|
| Settings | **Language** (hiện ngôn ngữ đang dùng), **Share**, **Rate app**, **Privacy policy**, **Policy setting** |
| Choose Language | Danh sách ngôn ngữ như lần đầu, nhưng **có nút ←** |
| Settings + Rate (6 frame) | Hộp thoại đánh giá đè lên Settings, 0→5 sao, mỗi mức đổi icon bóng đèn và câu chữ, nút **Submit** |

### 2.9 Component dùng chung

| Component | Nội dung | Dùng làm gì |
|---|---|---|
| **Rate us** | 6 biến thể (0–5 sao) của hộp thoại đánh giá | `RateDialog` |
| **Button** | Nút đặc nhỏ, nút đặc to, nút có icon, nút xám "Share" | Bộ style nút (như `Widget.LoveTester.Button.*` bạn đã làm) |
| **Frame 1707478416** | 4 trạng thái ô ngôn ngữ: thường, đã chọn, viền nét đứt, sáng | `item_language.xml` + selector |
| **Icon** | Bộ icon "Regular / Light (Border)" | Xuất SVG → Vector Asset |
| **fill name** | Công tắc "Turn on Floating Ball" | **(cần xác nhận)**: không thấy màn nào dùng |

---

## 3. Màn nào là Activity, màn nào là Fragment

Theo quy tắc công ty: **màn mới hoàn toàn (thường có ←) → Activity; nội dung đổi ngay trong màn đang đứng → Fragment / Dialog**.

### 3.1 Cây màn hình

```
📱 SplashActivity                      (SplashScreen API, điều hướng theo trạng thái onboarding)
📱 LanguageActivity                    (lần đầu: có Save, không ←  |  từ Settings: có ←)
📱 OnboardingActivity
   └── ViewPager2: 🧩 OnboardingPageFragment × 4
📱 PermissionActivity
   💬 NoInternetDialog                 (dạng hộp thoại; bản toàn màn hình → NoInternetActivity nếu cần)
📱 HomeActivity                        (KHÔNG có tab dưới → 1 Activity, không cần Fragment bên trong)
📱 FlashActivity
   └── TabLayout + ViewPager2
       ├── 🧩 FlashLightFragment
       ├── 🧩 SosFragment
       └── 🧩 DjFragment
📱 CameraActivity                      (CameraX, kính lúp + đèn)
📱 ScreenLightActivity
   💬 ColorPickerDialog                (dùng chung)
📱 ScreenLightPlayActivity             (ngang, toàn màn hình)
📱 LedActivity
   💬 ColorPickerDialog
📱 LedPlayActivity                     (ngang, toàn màn hình)
📱 FlashAlertActivity                  (danh sách 3 loại)
📱 FlashAlertDetailActivity            (1 màn dùng cho Calls / SMS / Notification, phân biệt bằng extra)
   💬 FlashAlertTestDialog             (giả lập cuộc gọi / tin nhắn / thông báo, toàn màn hình)
📱 SelectAppActivity
📱 EdgeLightingActivity
   💬 ColorPickerDialog (Add Border Color)
   💬 EditColorsDialog
   💬 DisplayModeDialog
📱 SettingsActivity
   💬 RateDialog

⚙️ Service (chạy nền, không phải màn hình):
   FlashAlertNotificationService      (NotificationListenerService)
   CallStateReceiver                  (nghe cuộc gọi đến)
   EdgeLightingOverlayService         (Foreground Service vẽ viền đè lên app khác)
   EdgeLightingWallpaperService       (Live Wallpaper cho HomeScreen/LockScreen)
```
📱 Activity · 🧩 Fragment · 💬 Dialog/BottomSheet · ⚙️ Service

### 3.2 Lý do cho các chỗ dễ phân vân

| Màn | Chọn | Vì sao |
|---|---|---|
| Language lần đầu vs Choose Language trong Settings | **1 Activity** + extra `EXTRA_FROM_SETTINGS` | Cùng danh sách, chỉ khác nút ← và hành động sau khi lưu. Bạn đã làm đúng mẫu này ở LoveTester |
| Language / Language_Select | **1 màn, 2 trạng thái** | Nút Save chỉ hiện khi đã chọn → `isVisible` theo ViewModel |
| 4 trang OBD | **Fragment** trong ViewPager2 | Lướt ngang trong cùng một màn. Có thể dùng **1** `OnboardingPageFragment` nhận ảnh/chữ qua arguments thay vì 4 class |
| Flash / SOS / DJ | **Fragment** trong `FlashActivity` | 3 tab **trên cùng một màn** "Flash" |
| Home | **1 Activity, không Fragment** | Figma không có thanh tab dưới. Đừng tạo vỏ tab như LoveTester khi không cần |
| Incoming Calls / SMS / Notification | **1 Activity** dùng chung | Bố cục giống nhau, chỉ khác icon, tiêu đề và dòng Select App |
| Select App | **Activity** | Màn mới có ←, có nút Done trả kết quả |
| Play của LED / Screen Light | **Activity riêng** | Phải xoay **ngang**, ẩn thanh hệ thống, giữ màn hình sáng. Tách Activity thì khai báo `screenOrientation` trong manifest rất gọn |
| Bộ chọn màu | **1 DialogFragment dùng chung** | Xuất hiện ở 3 nơi (Screen Light, LED, Edge). Trả màu về bằng **Fragment Result API** |
| Choose Display Mode, Edit Color, Rate | **DialogFragment** | Nổi lên trên màn hiện tại |
| Màn Test của Flash Alert | **DialogFragment toàn màn hình** **(cần xác nhận)** | Chỉ để thử rồi quay lại màn chi tiết; không phải màn điều hướng thật |
| No internet | **DialogFragment** | Bản hộp thoại dùng được ở mọi màn. Chỉ làm bản toàn màn hình nếu người hướng dẫn yêu cầu |

### 3.3 Luồng điều hướng

```
Lần đầu:  Splash → Language → Onboarding (4 trang) → Permission → Home
Lần sau:  Splash → Home
(mỗi bước finish() màn trước; Home bấm back = thoát app)

Home ── Camera ──────────→ CameraActivity
     ── Settings ────────→ SettingsActivity ── Language ──→ LanguageActivity (có ←)
                                            ── Rate ──────→ RateDialog
     ── LED: Play Now ───→ LedPlayActivity (dùng cấu hình đã lưu)
     ── LED: Custom Text ─→ LedActivity ── Play ──→ LedPlayActivity
     ── Flash ───────────→ FlashActivity (tab Flash Light / SOS / DJ)
     ── Flash Alert ─────→ FlashAlertActivity ──→ FlashAlertDetailActivity(type)
                                                     ── Select App ──→ SelectAppActivity
                                                     ── TEST ────────→ FlashAlertTestDialog
     ── Screenlight ─────→ ScreenLightActivity ── phóng to ──→ ScreenLightPlayActivity
     ── Edge Lighting ───→ EdgeLightingActivity ── Apply ──→ DisplayModeDialog
```

---

## 4. Kiến trúc code

### 4.1 Các tầng (giống LoveTester, bạn đã quen)

```
UI (Activity / Fragment / Dialog)   hiển thị, nhận thao tác, chọn câu chữ
        │ quan sát StateFlow, gọi hàm
ViewModel                           giữ trạng thái màn, sống qua xoay màn hình
        │
Repository / Controller (Hilt @Singleton)
  ├── SettingsRepository   → DataStore (cấu hình LED, ScreenLight, FlashAlert, Edge, onboarding)
  ├── TorchController      → CameraManager (đèn flash)
  ├── InstalledAppsRepository → PackageManager (danh sách app cho Select App)
  └── ConnectivityObserver → ConnectivityManager (có mạng hay không)
        │
Service / Receiver                  chạy khi app đóng: Flash Alert, Edge Lighting
```

### 4.2 Điểm thiết kế quan trọng nhất: `TorchController`

Đèn flash được dùng ở **5 chỗ**: Flash Light, SOS, DJ, Flash Alert và nút đèn trong Camera. Nếu mỗi chỗ tự gọi
`CameraManager` thì sẽ xảy ra cảnh SOS đang nháy mà Flash Alert cũng nháy, hai bên tắt bật lẫn nhau.

→ Tạo **một** `TorchController` (`@Singleton`) là nơi **duy nhất** bật/tắt đèn:
- `val state: StateFlow<TorchMode>` với `TorchMode` = `Off`, `On`, `Sos`, `Dj`, `Alert`.
- `fun turnOn()`, `turnOff()`, `startPattern(pattern)`, `stop()`.
- Pattern (SOS, DJ, Alert) chạy bằng coroutine; bắt đầu pattern mới thì **huỷ** pattern cũ.
- Các Fragment chỉ quan sát `state` để vẽ ON/OFF. Chuyển tab không làm đèn tắt bật lung tung.

Giống nguyên tắc "mỗi loại dữ liệu chỉ một Repository được đọc/ghi" bạn đã học.

### 4.3 Cấu trúc thư mục đề xuất

```
com.example.flashlight
├── App.kt                         (@HiltAndroidApp)
├── core/
│   ├── torch/                     TorchController, TorchMode, FlashPattern (SOS, DJ, Alert)
│   ├── ui/                        extension (viewBinding, collectOn, insets, showOnce, toast…),
│   │                              ColorPickerDialog, RateDialog, NoInternetDialog
│   ├── permission/                hàm kiểm tra / xin quyền dùng chung
│   └── network/                   ConnectivityObserver
├── data/
│   ├── settings/                  SettingsRepository + các model cấu hình (LedConfig, ScreenLightConfig,
│   │                              FlashAlertConfig, EdgeConfig, OnboardingStep)
│   └── apps/                      InstalledAppsRepository, AppInfo
├── di/                            Hilt module (DataStore, CameraManager…)
├── service/
│   ├── alert/                     FlashAlertNotificationService, CallStateReceiver
│   └── edge/                      EdgeLightingOverlayService, EdgeLightingWallpaperService, EdgeLightView
└── ui/
    ├── splash/  language/  onboarding/  permission/
    ├── home/
    ├── flash/                     FlashActivity, FlashLightFragment, SosFragment, DjFragment, FlashViewModel
    ├── camera/
    ├── screenlight/               ScreenLightActivity, ScreenLightPlayActivity, ViewModel
    ├── led/                       LedActivity, LedPlayActivity, MarqueeView, ViewModel
    ├── alert/                     FlashAlertActivity, FlashAlertDetailActivity, SelectAppActivity, TestDialog
    ├── edge/                      EdgeLightingActivity, các Dialog, ViewModel
    └── settings/
```

### 4.4 Custom View cần tự vẽ

| View | Dùng ở | Kỹ thuật |
|---|---|---|
| `MarqueeView` | Thẻ LED ở Home, preview LED, LedPlay | `Canvas.drawText` + `ValueAnimator`/`Choreographer` để dịch chữ; hướng → ← ↓ ↑; hiệu ứng: Glow/Neon (`setShadowLayer` hoặc `BlurMaskFilter`), Blink (đổi alpha), Fade |
| `EdgeLightView` | Preview Edge, Overlay Service, Wallpaper | `Path` bo góc theo Top/Bottom Radius + notch; `SweepGradient` nhiều màu; animation chạy theo Run Style; độ dày = Width |
| `ColorPickerView` | ColorPickerDialog | Ô Saturation/Value + thanh Hue + thanh Alpha; hoặc dùng thư viện (xem mục 5) |

Viết **một** View dùng lại cho preview và màn thật, chỉ khác kích thước. Không vẽ 2 lần.

---

## 5. Công nghệ đề xuất

| Việc | Công nghệ | Ghi chú |
|---|---|---|
| Ngôn ngữ | **Kotlin** | |
| Giao diện | **XML + ViewBinding** | Giữ cách bạn đã học ở LoveTester và cách công ty đang dùng. Jetpack Compose cũng làm được nhưng là một thứ mới phải học thêm |
| DI | **Hilt** | `TorchController`, Repository, DataStore là `@Singleton` |
| Bất đồng bộ | **Coroutines + Flow/StateFlow** | Pattern nháy đèn, đọc cấu hình |
| Lưu cấu hình | **DataStore Preferences** | App không có danh sách dữ liệu lớn → **không cần Room** |
| Đa ngôn ngữ | **AppCompatDelegate.setApplicationLocales** + `locales_config.xml` | 11 ngôn ngữ, giống LoveTester |
| Splash | **androidx.core:core-splashscreen** | |
| Onboarding / tab Flash | **ViewPager2 + TabLayout** (`TabLayoutMediator`) | |
| Đèn flash | **CameraManager.setTorchMode** | **Không cần** quyền CAMERA. Android 13+ có thêm `turnOnTorchWithStrengthLevel` để chỉnh độ mạnh (nếu máy hỗ trợ) |
| Camera (kính lúp) | **CameraX** (`camera-camera2`, `camera-lifecycle`, `camera-view`) | `PreviewView`, `setLinearZoom` cho thanh zoom, `enableTorch` cho nút đèn. **Cần** quyền CAMERA |
| Độ sáng Screen Light | `window.attributes.screenBrightness` | Chỉ đổi độ sáng **của màn đó**, không cần quyền WRITE_SETTINGS |
| Màn Play ngang | `android:screenOrientation="sensorLandscape"`, `WindowInsetsControllerCompat` (ẩn thanh hệ thống), `FLAG_KEEP_SCREEN_ON` | |
| Ảnh nền LED | Ảnh có sẵn trong app + **Photo Picker** (`PickVisualMedia`) cho nút + | Đã làm ở LoveTester |
| Hiển thị ảnh | **Coil** | |
| Danh sách app | `PackageManager.queryIntentActivities` với `<queries>` intent LAUNCHER | Tránh quyền `QUERY_ALL_PACKAGES` (Google Play hạn chế) |
| Flash Alert – thông báo | **NotificationListenerService** | Người dùng phải bật "Quyền truy cập thông báo" trong cài đặt hệ thống |
| Flash Alert – cuộc gọi | `READ_PHONE_STATE` + receiver `PHONE_STATE` (hoặc `TelephonyCallback`) | Quyền nguy hiểm, phải xin lúc bật công tắc |
| Flash Alert – SMS | **Bắt thông báo của app tin nhắn** qua NotificationListenerService | **Không** dùng `RECEIVE_SMS`: Google Play chỉ cho app SMS mặc định dùng quyền này |
| Edge – Overlay All Apps | `SYSTEM_ALERT_WINDOW` + **Foreground Service** vẽ `EdgeLightView` bằng `WindowManager` | Android 14+ phải khai báo loại foreground service (`specialUse`); cần `POST_NOTIFICATIONS` (Android 13+) cho thông báo của service |
| Edge – HomeScreen / LockScreen | **Live Wallpaper** (`WallpaperService`) | Người dùng phải đặt app làm hình nền động |
| Đánh giá | **Google Play In-App Review API** | 4–5 sao → mở Review; 1–3 sao → mở email góp ý |
| Chia sẻ | `Intent.ACTION_SEND` | |
| Privacy policy | **Custom Tabs** (`androidx.browser`) | |
| Policy setting | **Google UMP SDK** (form đồng ý GDPR) | Chỉ cần nếu app có quảng cáo **(cần xác nhận)** |
| Kiểm tra mạng | `ConnectivityManager.NetworkCallback` → `StateFlow<Boolean>` | Hiện NoInternetDialog khi cần |

> **Lưu ý chính sách:** các quyền ở Flash Alert và Edge Lighting (thông báo, cuộc gọi, overlay, foreground service)
> là phần Google Play kiểm duyệt kỹ nhất. Trước khi làm 2 tính năng này, hãy đọc lại chính sách quyền hiện hành
> của Google Play và hỏi người hướng dẫn cách công ty vẫn làm.

---

## 6. Dữ liệu cần lưu (DataStore)

| Nhóm | Trường |
|---|---|
| Onboarding | `onboardingStep` (LANGUAGE, INTRO, PERMISSION, DONE) |
| Screen Light | `color`, `brightness` |
| LED | `text`, `fontSize`, `speed`, `color`, `direction`, `effect`, `background` (id ảnh có sẵn hoặc đường dẫn ảnh) |
| Flash Alert (× 3 loại) | `enabled`, `onMillis`, `offMillis`; riêng Notification: `selectedPackages` (Set<String>) |
| Edge Lighting | `enabled`, `colors` (danh sách), `borderStyle`, `runStyle`, `topRadius`, `bottomRadius`, `width`, `colorSpeed`, `notchStyle`, `displayModes` (Home/Lock/Overlay) |

Mỗi nhóm nên là một `data class` và một `Flow<…Config>` trong `SettingsRepository`. ViewModel đọc Flow,
màn chỉ hiển thị. Màn Play đọc cùng Flow nên luôn khớp với màn cấu hình.

---

## 7. Lộ trình tự viết (dễ → khó)

Mỗi bước chạy được rồi mới sang bước sau, **commit sau mỗi bước**.

| # | Việc | Học được gì |
|---|---|---|
| 1 | Tạo dự án: Hilt, ViewBinding, theme tối, màu và style nút từ Figma, `core/ui` extension | Nền móng, chép lại kinh nghiệm từ LoveTester |
| 2 | Splash → Language → Onboarding → Permission → Home (Home chỉ là giao diện tĩnh) | Luồng nhiều Activity, ViewPager2, quyền runtime, đa ngôn ngữ |
| 3 | `TorchController` + `FlashActivity` 3 tab | Singleton dùng chung, coroutine pattern, TabLayout |
| 4 | Screen Light + `ColorPickerDialog` + màn Play ngang | Dialog dùng chung, Fragment Result API, độ sáng cửa sổ, xoay ngang |
| 5 | LED + `MarqueeView` + Play; nối thẻ LED ở Home | **Custom View** và animation |
| 6 | Camera (CameraX) | Thư viện camera, quyền CAMERA |
| 7 | Settings + Rate dialog + Share + Privacy | Dialog nhiều trạng thái, In-App Review |
| 8 | Flash Alert: màn cấu hình + Select App + Test, rồi đến Service/Receiver | **Service chạy nền**, quyền đặc biệt |
| 9 | Edge Lighting: màn cấu hình + `EdgeLightView` preview, rồi Overlay Service, rồi Live Wallpaper | Vẽ Path phức tạp, overlay, wallpaper. Khó nhất |

**Mẹo:** ở bước 8 và 9, làm **màn cấu hình + preview trước** (chỉ lưu DataStore, chưa chạy nền).
Phần Service làm sau cùng, khi giao diện đã ổn.

---

## 8. Câu hỏi cần hỏi người hướng dẫn trước khi code

1. App có **quảng cáo** không? (khoảng trống dưới các trang OBD, mục "Policy setting" gợi ý là có)
2. Màn **Test** của Flash Alert là màn riêng hay hộp thoại toàn màn hình?
3. **No internet**: hiện ở đâu và lúc nào (chỉ ở Splash hay mọi màn)? Dùng bản toàn màn hình hay hộp thoại?
4. Component **fill name** ("Turn on Floating Ball") dùng ở đâu?
5. **DJ Mode** nháy theo nhịp cố định hay theo âm thanh micro? (theo micro thì cần thêm quyền RECORD_AUDIO)
6. **Edge Lighting** sáng liên tục, hay chỉ sáng khi có thông báo?
7. Nút **Play Now!** ở thẻ LED trên Home chạy với chữ mặc định hay chữ người dùng đã lưu?
8. Có cần quyền Camera ngay ở màn Permission không, hay chỉ xin khi mở Camera? (Flash **không** cần quyền Camera)
