# Luồng làm việc từ đầu đến cuối – app Flashlight

Bộ tài liệu gồm 3 file, dùng cùng nhau:

| File | Trả lời câu hỏi |
|---|---|
| `FLASHLIGHT_APP_PLAN.md` | App có **những màn nào**, Activity hay Fragment, kiến trúc, công nghệ |
| `UI_IMPLEMENTATION_GUIDE.md` | **Dựng giao diện** từng màn và từng component thế nào |
| `WORKFLOW_GUIDE.md` (file này) | **Làm theo thứ tự nào**, mỗi giai đoạn làm gì, xong thì kiểm tra gì |

UI_IMPLEMENTATION_GUIDE.md đã được cập nhật theo số đo đọc trực tiếp từ Figma – luôn ưu tiên số trong file đó.

Ký hiệu tham chiếu: **[Plan 4.2]** = mục 4.2 trong `FLASHLIGHT_APP_PLAN.md`; **[UI B5]** = mục B5 trong `UI_IMPLEMENTATION_GUIDE.md`.

---

## Phần 1. Cách làm việc

### 1.1 Vòng lặp cho MỖI màn hình

Mỗi màn đều đi qua đúng 10 bước này. In ra dán cạnh màn hình cũng được.

1. **Đọc Figma**: mở frame, liệt kê các khối, đo số liệu chưa có trong token [UI A1].
2. **Đọc tài liệu**: phần tương ứng trong [Plan] và [UI].
3. **Layout XML**: ghép component có sẵn theo cây View trong [UI C…]. Dùng `tools:` để xem trước.
4. **Khung Activity/Fragment**: binding, insets, nút back. Khai báo manifest nếu là Activity.
5. **State**: viết `data class XxxUiState` chứa mọi thứ màn cần hiển thị.
6. **ViewModel**: `StateFlow<XxxUiState>` + các hàm cho thao tác người dùng.
7. **Nối dữ liệu**: ViewModel gọi Repository/Controller; màn `collectOn` state và vẽ.
8. **Xử lý biên**: xoay màn hình, bấm nhanh 2 lần, back, mất quyền, chuỗi dài (tiếng Đức).
9. **So và thử**: chụp máy ảo đặt cạnh Figma; chạy checklist của giai đoạn.
10. **Commit**, rồi paste code cho Claude review trước khi sang màn sau.

### 1.2 Định nghĩa "xong" (áp dụng cho mọi màn)

- [ ] Build không lỗi, không warning mới trong lint
- [ ] Giống Figma: màu, cỡ chữ, khoảng cách, bo góc
- [ ] Không có chuỗi, màu, số dp viết cứng trong layout/code
- [ ] Xoay màn hình không mất dữ liệu, không crash
- [ ] Back (nút ← và back hệ thống) đi đúng chỗ
- [ ] Bấm nhanh 2 lần không mở 2 màn / 2 dialog
- [ ] Đổi sang tiếng Đức: chữ không tràn, không bị cắt
- [ ] Đã commit

### 1.3 Git

- Nhánh chính `main` luôn chạy được.
- Mỗi **giai đoạn** một nhánh: `feature/onboarding`, `feature/flash`, … Xong giai đoạn thì merge vào `main`.
- Commit sau mỗi **bước nhỏ** chạy được. Message: `Flash: TorchController bật/tắt`, `UI: component nút`.
- `git push` cuối mỗi buổi để đổi máy không mất code.

### 1.4 Khi nào tự làm, khi nào nhờ agent

| Tự làm (để học) | Nhờ agent (việc lặp lại, tốn thời gian) |
|---|---|
| Layout, Activity, Fragment, ViewModel | Dịch chuỗi sang 10 ngôn ngữ |
| TorchController, MarqueeView, EdgeLightView | Xuất/chuyển hàng loạt icon thành Vector Asset |
| Service, xin quyền | Kiểm tra lint, tìm chuỗi/màu viết cứng |
| Mọi thứ lần đầu gặp | Viết tài liệu, báo cáo luồng code |

Prompt cho agent: **ngắn, 1–3 việc, phiên mới mỗi việc, model rẻ** (Sonnet / Gemini Flash), cuối prompt luôn có
"build + commit + báo cáo ngắn".

---

## Phần 2. Các giai đoạn

Tổng quan:

| GĐ | Nội dung | Kiến thức mới chính |
|---|---|---|
| 0 | Setup dự án | (đã xong) |
| 1 | Nền móng code | Extension, Hilt module |
| 2 | Component giao diện | Style, custom attribute, selector |
| 3 | Luồng mở đầu | Điều hướng nhiều Activity, ViewPager2, quyền runtime |
| 4 | Settings | In-App Review, Custom Tabs, Intent chia sẻ |
| 5 | Flash | Điều khiển phần cứng, coroutine pattern, TabLayout |
| 6 | Screen Light | Dialog dùng chung, độ sáng cửa sổ, màn ngang |
| 7 | LED | Custom View + animation |
| 8 | Camera | CameraX |
| 9 | Flash Alert | NotificationListenerService, BroadcastReceiver |
| 10 | Edge Lighting | Vẽ Path, overlay service, live wallpaper |
| 11 | Hoàn thiện & phát hành | Release build, kiểm thử nhiều máy |

---

### GĐ 0. Setup (đã làm)

Kiểm tra lại trước khi đi tiếp:
- [ ] App chạy, nền `#111111`, font Pathway Extreme
- [ ] `colors.xml`, `dimens.xml`, `styles.xml`, `strings.xml` (11 ngôn ngữ) đã có, **đã xử lý hết TODO màu**
- [ ] Thư mục `core/`, `data/`, `ui/`, `service/`, `di/` đã có
- [ ] 3 file tài liệu nằm trong `docs/`

---

### GĐ 1. Nền móng code
**Nhánh:** `feature/foundation` · **Đọc:** [Plan 4.1, 4.3]

1. **Extension dùng chung** → `core/ui/`: chép từ LoveTester `ViewBindingDelegate`, `FlowExtensions` (`collectOn`, `toast`),
   `InsetsExtensions`, `FragmentExtensions` (`showOnce`), `KeyboardExtensions`. Sửa `package`, đọc hiểu từng hàm.
2. **Hilt module** → `di/AppModule.kt`: cung cấp `DataStore<Preferences>` (một instance cho cả app) và `CameraManager`.
3. **Thêm extension mới** cho app này:
   - `Activity.hideSystemBars()` / `showSystemBars()` (cho màn Play)
   - `Activity.keepScreenOn(on: Boolean)`
   - `Context.dp(value)` nếu cần đổi dp → px trong custom View

**Kiểm tra:** build được; chưa có màn nào dùng cũng không sao.
**Commit:** `Foundation: extension dùng chung, Hilt module`

---

### GĐ 2. Component giao diện
**Nhánh:** `feature/components` · **Đọc:** [UI Phần B]

Tạo `ui/dev/ComponentsPreviewActivity` (chỉ để xem, xoá trước khi phát hành). Mỗi component làm xong thì
đặt lên màn này và so với Figma.

| Thứ tự | Component | File tạo ra | Đọc |
|---|---|---|---|
| 1 | Nút Filled / Outlined / Gradient / disabled | `styles.xml`, `bg_button_gradient.xml` | [UI B8] |
| 2 | Thẻ | style `Widget.App.Card` | [UI B2] |
| 3 | Thanh trên cùng | `view_top_bar.xml` | [UI B1] |
| 4 | Hàng chọn | `NavRowView.kt` + `view_nav_row.xml` + `attrs.xml` | [UI B3] |
| 5 | Hàng công tắc | `view_switch_row.xml` + style Switch (component 'fill name' trong Figma) | [UI B4] |
| 6 | Hàng thanh trượt | `view_slider_row.xml` + style Slider | [UI B5] |
| 7 | Ô danh sách có lựa chọn | `item_selectable.xml` + selector | [UI B7] |
| 8 | Lưới màu | `item_color_swatch.xml` + `ColorSwatchAdapter` | [UI B6] |
| 9 | Chip | style `Widget.App.Chip` | [UI B9] |
| 10 | Nút tròn ON/OFF | `view_power_button.xml` | [UI B12] |

Hộp thoại chọn màu (B10) và đánh giá (B11) để tới giai đoạn dùng chúng.

**Kiến thức mới:** custom attribute (`attrs.xml` + `obtainStyledAttributes`) ở bước 4. Đây là bước đáng hỏi Claude nhất.
**Kiểm tra:** màn preview đặt cạnh Figma, giống từng chi tiết; xoay màn hình không vỡ.
**Commit:** mỗi component một commit.

---

### GĐ 3. Luồng mở đầu
**Nhánh:** `feature/onboarding` · **Đọc:** [Plan 2.1, 3.3] · [UI C1–C6]

**3.1 Dữ liệu**
- `data/settings/OnboardingStep.kt` (enum: LANGUAGE, INTRO, PERMISSION, DONE)
- `data/settings/SettingsRepository.kt`: `onboardingStep: Flow<OnboardingStep>`, `suspend fun setOnboardingStep(...)`

**3.2 Các màn** (theo vòng lặp 1.1 cho từng màn)

| Màn | Điểm chính |
|---|---|
| Splash | Đọc `onboardingStep`, mở đúng màn tiếp theo, `finish()`. Hỏi người hướng dẫn về splash gradient [UI C1]. Vị trí logo: tạm để ở tâm, chờ quyết định (xem Phần 3) |
| Language | `LanguageViewModel` giữ ngôn ngữ đang chọn (sống qua xoay). Save → `setApplicationLocales` → bước tiếp. Hỗ trợ `EXTRA_FROM_SETTINGS` ngay từ đầu |
| Onboarding | ViewPager2 + **1** `OnboardingPageFragment` nhận arguments; chấm chỉ trang; Next / Get Started |
| Permission | Công tắc Camera → xin quyền → bật công tắc + mở khoá Continue khi được cấp |
| Home | **Chỉ giao diện tĩnh**: các thẻ, nút Camera/Settings chưa làm gì. Chữ chạy để `TextView` tạm |

Mỗi màn khi đi tiếp: ghi `onboardingStep` rồi `finish()` màn hiện tại.

**Kiểm tra:**
- [ ] Cài mới: Splash → Language → Onboarding → Permission → Home
- [ ] Mở lại app: Splash → Home thẳng
- [ ] Ở Home bấm back: thoát app, không quay lại onboarding
- [ ] Tắt app giữa chừng ở Onboarding, mở lại: vào đúng bước đang dở
- [ ] Đổi ngôn ngữ ở màn Language: chữ các màn sau đổi theo
- [ ] Từ chối quyền Camera: Continue vẫn xám; vào cài đặt cấp quyền rồi quay lại thì mở khoá

**Commit:** mỗi màn một commit · **Merge** vào `main` khi xong cả luồng.

---

### GĐ 4. Settings
**Nhánh:** `feature/settings` · **Đọc:** [Plan 2.8] · [UI C13, B11]

1. Màn Settings bằng 5 `NavRowView`; mục Language hiện tên ngôn ngữ đang dùng.
2. Language → `LanguageActivity` với `EXTRA_FROM_SETTINGS = true` (có ←, lưu xong `finish()`).
3. Share → `Intent.ACTION_SEND` kèm link Play Store.
4. Privacy policy → Custom Tabs.
5. `RateDialog` 6 trạng thái [UI B11]: 4–5 sao → In-App Review; 1–3 sao → mở email góp ý.
6. Policy setting → để trống hoặc hỏi người hướng dẫn (UMP nếu có quảng cáo).
7. Home: nút Settings mở màn này.

**Kiểm tra:** đổi ngôn ngữ từ Settings quay lại Settings với chữ mới, không bị đưa về onboarding; xoay khi đang mở
RateDialog giữ đúng số sao.

---

### GĐ 5. Flash
**Nhánh:** `feature/flash` · **Đọc:** [Plan 2.3, 4.2, 5 (Đèn flash)] · [UI C7, B12]

**5.1 `TorchController`** (`core/torch/`, `@Singleton`) – **làm và thử riêng trước khi có giao diện**
1. `TorchMode` (Off, On, Sos, Dj, Alert) và `val state: StateFlow<TorchMode>`.
2. `turnOn()` / `turnOff()` bằng `CameraManager.setTorchMode(cameraId, …)`. Tìm `cameraId` có flash.
3. `startPattern(mode, pattern)`: coroutine bật/tắt theo danh sách thời gian; bắt đầu pattern mới thì huỷ job cũ.
   - SOS: ··· — — — ··· (ngắn ~200ms, dài ~600ms, nghỉ giữa các chữ) lặp lại
   - DJ: nhịp nhanh ngẫu nhiên / cố định (theo câu trả lời của người hướng dẫn)
4. Xử lý máy **không có flash** và lỗi khi camera đang bị app khác dùng.
5. Thử: tạm thời gọi từ một nút trên `ComponentsPreviewActivity`.

**5.2 Màn Flash**
1. `FlashActivity` + TabLayout + ViewPager2 (`isUserInputEnabled = false`).
2. **1** `FlashModeFragment` nhận `mode`; nút tròn đọc `TorchController.state`.
3. Chuyển tab: tắt chế độ của tab cũ (hoặc giữ – hỏi người hướng dẫn).
4. Rời màn Flash (back): tắt đèn **(cần xác nhận hành vi mong muốn)**.
5. Home: thẻ Flash → `FlashActivity`.

**Kiểm tra:**
- [ ] Bật đèn, xoay màn hình: đèn vẫn sáng, nút vẫn ON
- [ ] Đang SOS chuyển sang tab Flash Light: SOS dừng
- [ ] Bấm liên tục nút ON/OFF thật nhanh: không crash, trạng thái cuối khớp với đèn
- [ ] Máy ảo không có flash: hiện thông báo, không crash (máy thật thì thử đủ cả 3 chế độ)

---

### GĐ 6. Screen Light
**Nhánh:** `feature/screenlight` · **Đọc:** [Plan 2.4] · [UI C9, B10]

1. `ScreenLightConfig(color, brightness)` trong `SettingsRepository`.
2. `ColorPickerDialog` [UI B10]: dùng chung cho cả app; trả kết quả bằng Fragment Result API. Làm kỹ vì LED và Edge dùng lại.
3. `ScreenLightActivity`: preview, slider độ sáng (đổi độ sáng cửa sổ ngay khi kéo), lưới màu.
4. `ScreenLightPlayActivity`: ngang, ẩn thanh hệ thống, giữ màn hình sáng, nút X.
5. Home: thẻ Screenlight → màn này; chấm preset bấm vào thì lưu màu rồi mở Play.

**Kiểm tra:** độ sáng trả về như cũ khi rời màn; màu chọn trong dialog còn sau khi xoay; Play giữ màn hình không tắt.

---

### GĐ 7. LED
**Nhánh:** `feature/led` · **Đọc:** [Plan 2.5, 4.4] · [UI C10]

1. `LedConfig` trong `SettingsRepository` (text, fontSize, speed, color, direction, effect, background).
2. **`MarqueeView`** (`ui/led/`) – làm **riêng trước**, thử trên `ComponentsPreviewActivity`:
   chữ chạy → đổi tốc độ → đổi hướng → hiệu ứng Glow/Neon/Blink/Fade → ảnh nền.
3. `LedActivity`: các khối cấu hình, preview cập nhật ngay.
4. Ảnh nền: ảnh có sẵn + Photo Picker (lưu bản sao vào bộ nhớ app như LoveTester).
5. `LedPlayActivity`: ngang, toàn màn hình.
6. Home: thẻ LED dùng `MarqueeView` thật; Play Now → `LedPlayActivity`; Custom Text → `LedActivity`.

**Kiến thức mới:** `onDraw`, `ValueAnimator`, `Paint` (shadow, alpha), dừng animation khi View bị gỡ (`onDetachedFromWindow`).
**Kiểm tra:** chữ chạy mượt (không giật), đổi cấu hình thấy ngay ở preview, Play và Home hiển thị đúng cấu hình đã lưu,
rời màn thì animation dừng (không tốn pin).

---

### GĐ 8. Camera
**Nhánh:** `feature/camera` · **Đọc:** [Plan 5 (Camera)] · [UI C8]

1. Xin quyền CAMERA khi mở màn (nếu chưa có từ màn Permission).
2. CameraX: `PreviewView`, slider zoom → `setLinearZoom`.
3. Nút đèn trong Camera dùng `cameraControl.enableTorch()`. Khi camera đang mở, `TorchController` không được tự bật đèn
   (hai bên tranh nhau camera) → báo cho `TorchController` biết camera đang được dùng.
4. Nút chụp: lưu ảnh vào thư viện bằng `ImageCapture` + MediaStore **(cần xác nhận có cần chụp không)**.

---

### GĐ 9. Flash Alert
**Nhánh:** `feature/alert` · **Đọc:** [Plan 2.6, 5 (Flash Alert) + lưu ý chính sách] · [UI C11]

**9.1 Giao diện + cấu hình (chưa chạy nền)**
1. `AlertType` enum (icon, màu, tiêu đề, có Select App).
2. `FlashAlertConfig` × 3 loại trong `SettingsRepository`.
3. `FlashAlertActivity` → `FlashAlertDetailActivity(type)` → `SelectAppActivity`.
4. `InstalledAppsRepository` (luồng nền, `<queries>` LAUNCHER).
5. `FlashAlertTestDialog`: hiện hình giả lập + `TorchController` nháy theo tốc độ đang chỉnh.

**9.2 Chạy nền**
1. Bật công tắc → kiểm tra quyền cần thiết → nếu thiếu thì dẫn người dùng tới màn cài đặt hệ thống tương ứng.
2. `FlashAlertNotificationService` (NotificationListenerService): thông báo từ app trong danh sách đã chọn → nháy;
   thông báo từ app tin nhắn mặc định → nháy theo cấu hình SMS.
3. Cuộc gọi đến: receiver `PHONE_STATE` (quyền `READ_PHONE_STATE`) → nháy cho tới khi hết đổ chuông.
4. Mọi lần nháy đều qua `TorchController.startPattern(Alert, …)`.

**Kiểm tra:** tắt app hẳn rồi gọi điện / gửi tin / nhận thông báo từ app đã chọn → đèn nháy; tắt công tắc → không nháy;
khởi động lại máy → vẫn hoạt động (nếu yêu cầu).

---

### GĐ 10. Edge Lighting
**Nhánh:** `feature/edge` · **Đọc:** [Plan 2.7, 4.4, 5 (Edge)] · [UI C12]

**10.1 `EdgeLightView` (làm riêng trước)**
1. Vẽ viền bo góc theo Top/Bottom Radius, độ dày Width.
2. Gradient nhiều màu chạy quanh viền theo Color Speed.
3. Run Style (None, Rotation, Run, Crawler, Snake…) – làm 2–3 kiểu trước, phần còn lại sau.
4. Notch Style, Border Style (icon chạy theo viền) – làm sau cùng.

**10.2 Màn cấu hình**
`EdgeConfig` trong `SettingsRepository`; `EdgeLightingActivity` với preview phủ toàn màn; 3 dialog (Add/Edit color, Display Mode).

**10.3 Chạy nền**
1. Overlay All Apps: xin `SYSTEM_ALERT_WINDOW` → Foreground Service thêm `EdgeLightView` vào `WindowManager`.
2. HomeScreen / LockScreen: `WallpaperService` vẽ cùng logic với `EdgeLightView`.

**Kiểm tra:** preview khớp với khi chạy thật; tắt công tắc gỡ overlay ngay; xoay máy, khoá/mở màn hình không crash;
pin không tụt bất thường (vẽ quá nhiều khung hình).

---

### GĐ 11. Hoàn thiện & phát hành
**Nhánh:** `release/1.0`

1. **No internet** [UI C5]: hiện ở đâu theo câu trả lời của người hướng dẫn.
2. **Dịch**: nhờ agent dịch các chuỗi mới; tự kiểm tra màn dài nhất bằng tiếng Đức.
3. **Lint** sạch; xoá `ComponentsPreviewActivity`.
4. **Icon app** (Image Asset, adaptive), tên app, `versionCode` / `versionName`.
5. **Release build**: bật R8 (`isMinifyEnabled = true`), thêm keep rule nếu cần, chạy thử bản release.
6. **Thử trên nhiều máy**: màn nhỏ, màn lớn, Android cũ nhất theo minSdk và Android mới nhất, máy có/không có flash.
7. **Tài liệu**: nhờ agent viết `docs/CODE_FLOW_GUIDE.md` (luồng code) như đã làm với LoveTester.

---

## Phần 3. Câu hỏi cần có câu trả lời trước từng giai đoạn

| Trước GĐ | Hỏi người hướng dẫn |
|---|---|
| 3 | Splash có cần giống y Figma (gradient + chữ) không? App có quảng cáo không? Logo splash giữ đúng vị trí Figma hay dời về tâm? |
| 3 | Xin quyền Camera ở màn Permission hay khi mở Camera? |
| 5 | Rời màn Flash / chuyển tab thì đèn tắt hay giữ? DJ theo nhịp cố định hay theo nhạc (micro)? |
| 7 | Play Now ở Home dùng chữ mặc định hay chữ đã lưu? |
| 8 | Nút chụp có lưu ảnh không? |
| 9 | Màn Test là màn riêng hay hộp thoại? Flash Alert có cần chạy sau khi khởi động lại máy? |
| 10 | Edge sáng liên tục hay chỉ khi có thông báo? |
| 11 | No internet hiện ở đâu? |

---

## Phần 4. Làm việc với Claude theo từng giai đoạn

1. **Đầu giai đoạn**: nhắn "Bắt đầu GĐ x" kèm file liên quan đã có. Claude chia giai đoạn thành bước nhỏ và giải thích *tại sao*.
2. **Mỗi bước**: bạn tự viết, paste code vào, Claude review và chỉ lỗi.
3. **Kẹt quá 30 phút**: paste lỗi Logcat (dòng `FATAL EXCEPTION` và vài dòng dưới) hoặc ảnh chụp so với Figma.
4. **Cuối giai đoạn**: chạy checklist của giai đoạn, báo kết quả, rồi mới merge.
