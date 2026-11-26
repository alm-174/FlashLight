# Hướng dẫn code giao diện – app Flashlight

Tài liệu này đi kèm `FLASHLIGHT_APP_PLAN.md`. Plan nói **có những màn nào, Activity hay Fragment**.
Tài liệu này nói **dựng giao diện từng màn thế nào**: dùng View gì, bố cục ra sao, tách component nào để dùng lại,
chỗ nào khó và cách xử lý.

Không có layout viết sẵn hoàn chỉnh. Mỗi màn có **cây View** (khung xương) và vài đoạn code ngắn cho chỗ khó.
Bạn tự viết XML theo khung xương, mở Figma đo số liệu chính xác.

---

## Phần A. Quy trình chung: từ Figma ra XML

### A1. Cách đọc số liệu trong Figma
1. Chọn layer → bảng **Properties** bên phải hiện: kích thước, **Radius**, **Padding**, **Gap**, màu, font.
2. Giữ **Ctrl + click** (Windows) để chọn thẳng layer sâu bên trong (chữ, nút) thay vì cả frame.
3. Chọn một layer rồi **giữ Alt và rê chuột** lên layer khác: Figma hiện khoảng cách giữa hai layer (margin).
4. Figma ghi **px**, trên Android ghi **dp** (cỡ chữ ghi **sp**) với **cùng con số**, vì frame thiết kế rộng 360 = 360dp.

### A2. Số liệu đã đọc từ Figma (dùng làm token)

| Token | Giá trị | Ở đâu trong Figma |
|---|---|---|
| `background` | `#111111` | layer "backround" của Homepage |
| `surface` (nền thẻ, ô) | `#22262C` | "BG thẻ" trong ô ngôn ngữ |
| `stroke` (viền) | `#36394A` | Greyscale/600 |
| `primary` | `#34D399` | nút, công tắc, chữ xanh |
| `primary_gradient_end` | `#93FFD8` | nút "Play Now!" (gradient `#34D399 → #93FFD8`) |
| `text_primary` | `#FFFFFF` | "App Name" |
| `text_secondary` | `#C1C7D0` | Greyscale/200, dòng phụ |
| `text_tertiary` | `#A4ACB9` | Greyscale/300 |
| `on_primary` | `#1A1B25` | Greyscale/800, chữ trên nút xanh |
| Font | **Pathway Extreme** | 400 / 500 / 600 / 700 |
| Bo góc thẻ Home | 24dp | thẻ Screenlight |
| Bo góc ô danh sách, nút | 12dp | ô ngôn ngữ, nút Play Now |
| Padding thẻ Home | 12 trên/dưới 16 · 12 trái/phải, gap 14 | thẻ Screenlight |
| Chữ | 18sp SemiBold (tiêu đề), 14sp Bold (nút), 12sp Regular (phụ) | Homepage |

> Màu và số nào **chưa có** trong bảng thì đo thêm theo A1 rồi thêm vào `colors.xml` / `dimens.xml`.
> **Không** viết mã hex hay số dp trực tiếp trong layout.

### A3. Thứ tự làm một màn
1. **Liệt kê khối** trong màn (header, thẻ, danh sách, nút dưới cùng…).
2. Khối nào **lặp ở màn khác** → làm thành component dùng chung (Phần B) trước.
3. Dựng **cây View** cho màn, ghép các component.
4. Dùng `tools:text`, `tools:src`, `tools:visibility` để **xem trước** trong Android Studio mà không ảnh hưởng app thật.
5. Đặt `android:id` cho mọi View mà code sẽ đụng tới. Tên theo kiểu `tvTitle`, `btnSave`, `switchStatus`, `rvColors`.
6. So với Figma ở **Layout Editor**, rồi chạy trên máy ảo và so lần nữa.

### A4. Chọn layout gốc
| Trường hợp | Dùng |
|---|---|
| Màn có nội dung dài hơn màn hình (Home, LED, Edge Lighting) | `NestedScrollView` → bên trong `LinearLayout` dọc |
| Màn ít phần tử, cần căn giữa / bám đáy (Permission, No internet, Flash) | `ConstraintLayout` |
| Một hàng icon + chữ + mũi tên | `LinearLayout` ngang, chữ ở giữa `layout_width="0dp"` + `layout_weight="1"` |
| Danh sách có thể dài (ngôn ngữ, chọn app, màu) | `RecyclerView` |
| Một thẻ có nền, viền, bo góc | `MaterialCardView` |

**Nút dưới cùng + nội dung cuộn** (Edge Lighting có nút Apply cố định):
`ConstraintLayout` gốc → `NestedScrollView` (top → top bar, bottom → top của nút) + nút bám đáy.

### A5. Insets (edge-to-edge)
Mọi Activity gọi `enableEdgeToEdge`. View gốc nhận `padding` theo `systemBars`. Màn có ô nhập (LED) cộng thêm `ime()`.
Màn **Play** (LED, Screen Light) thì ngược lại: **ẩn** thanh hệ thống bằng `WindowInsetsControllerCompat`.

---

## Phần B. Component dùng chung (làm TRƯỚC các màn)

Làm xong phần này, mỗi màn chỉ còn là việc ghép khối.

| # | Component | Dạng | Dùng ở |
|---|---|---|---|
| B1 | Thanh trên cùng (← + tiêu đề + nút phải) | `layout/view_top_bar.xml` + `<include>` | Gần như mọi màn |
| B2 | Thẻ cài đặt (nền surface, viền, bo góc) | style `Widget.App.Card` | Mọi khối "Brightness", "Color"… |
| B3 | Hàng chọn: icon + chữ + phụ đề + mũi tên | `layout/item_nav_row.xml` | Settings, Flash Alert |
| B4 | Hàng công tắc: chữ + Switch | `layout/view_switch_row.xml` | Permission, Flash Alert, Edge Lighting |
| B5 | Hàng thanh trượt: nhãn + giá trị + Slider | `layout/view_slider_row.xml` | Brightness, Font Size, Speed, Flashing ON/OFF, Border |
| B6 | Lưới màu tròn (nút bảng màu + các màu) | `RecyclerView` + `item_color_swatch.xml` | Screen Light, LED, Edge Lighting |
| B7 | Ô chọn có cờ/icon + nút tròn | `item_language.xml` (component **Frame 1707478416**) | Language, Choose Language, Select App |
| B8 | Nút | style `Widget.App.Button.Filled / .Outlined` + `.Gradient` | Mọi nơi (component **Button**) |
| B9 | Chip chọn một (Glow/Blink/Neon/Fade, Run Style, tab Border Style) | `ChipGroup` + style chip riêng | LED, Edge Lighting |
| B10 | Hộp thoại chọn màu | `ColorPickerDialog` | Screen Light, LED, Edge (Add color) |
| B11 | Hộp thoại đánh giá | `RateDialog` (component **Rate us**) | Settings |
| B12 | Nút tròn lớn ON/OFF | `layout/view_power_button.xml` | 3 tab Flash |

### B1. Thanh trên cùng
```
LinearLayout (ngang, chiều cao 56dp, gravity center_vertical)
├── ImageButton btnBack          48×48dp, nền ?selectableItemBackgroundBorderless, icon ←
├── TextView tvTitle             0dp + weight 1, căn giữa, TextAppearance.App.Title
└── FrameLayout endSlot          48dp: chỗ cho nút phải (Save, Select All, phóng to…) hoặc trống
```
- Tiêu đề phải **căn giữa màn hình** dù bên trái có nút ← còn bên phải trống. Cách dễ nhất: **luôn chừa 48dp ở cả hai bên** (`endSlot` trống vẫn chiếm chỗ).
- Màn Language lần đầu **ẩn** `btnBack` bằng `INVISIBLE` (không dùng `GONE`, để tiêu đề không lệch).

### B2. Thẻ (Card)
```xml
<style name="Widget.App.Card" parent="Widget.Material3.CardView.Filled">
    <item name="cardBackgroundColor">@color/surface</item>
    <item name="strokeColor">@color/stroke</item>
    <item name="strokeWidth">1dp</item>
    <item name="cardCornerRadius">@dimen/radius_item</item>
    <item name="cardElevation">0dp</item>
</style>
```
Thẻ lớn ở Home dùng bo `radius_card` (24dp) và **nền gradient mờ** (xem màn Home).

### B3. Hàng chọn (Settings, Flash Alert)
```
MaterialCardView (Widget.App.Card, clickable, foreground ripple)
└── LinearLayout ngang, padding 16dp
    ├── ImageView ivIcon          24dp
    ├── LinearLayout dọc (0dp + weight 1, marginStart 12dp)
    │   ├── TextView tvTitle      Body, text_primary
    │   └── TextView tvSubtitle   Caption, text_secondary (GONE nếu không có)
    ├── TextView tvValue          primary (ví dụ "English" ở Settings), GONE nếu không có
    └── ImageView ivChevron       ›
```
Tạo **một** custom view nhỏ `NavRowView` nhận thuộc tính `app:icon`, `app:title`, `app:subtitle` (khai báo trong
`attrs.xml`) để viết layout gọn: `<NavRowView app:title="@string/settings_share" … />`.
Đây là bài tập tốt về **custom attribute**.

### B4. Hàng công tắc
```
MaterialCardView (Widget.App.Card)
└── LinearLayout ngang, padding 12–16dp
    ├── TextView tvLabel          0dp + weight 1   ("Status: Off" / "Camera" / "Enable Edge Lighting to begin")
    └── MaterialSwitch switch
```
Màu công tắc theo Figma: track **xanh** khi bật, **xám** khi tắt, thumb trắng. Đặt trong theme:
```xml
<style name="Widget.App.Switch" parent="Widget.Material3.CompoundButton.MaterialSwitch">
    <item name="trackTint">@color/selector_switch_track</item>   <!-- checked: primary, mặc định: stroke -->
    <item name="thumbTint">@android:color/white</item>
    <item name="trackDecorationTint">@android:color/transparent</item>
</style>
```
Chữ "Status: Off/On" đổi theo `isChecked`: dùng **string có tham số**, không ghép chuỗi.

### B5. Hàng thanh trượt
```
LinearLayout dọc
├── LinearLayout ngang
│   ├── TextView tvLabel          0dp + weight 1   ("Brightness", "Font Size", "Flashing ON")
│   └── TextView tvValue          primary          ("80%", "64px", "0.5s")
└── com.google.android.material.slider.Slider slider
```
- Style Slider: `trackColorActive` = primary, `trackColorInactive` = stroke, `thumbColor` = primary, `trackHeight` ≈ 4dp, `labelBehavior="gone"` (Figma hiện giá trị ở bên phải chứ không có bong bóng).
- `valueFrom`, `valueTo`, `stepSize` khác nhau ở mỗi chỗ → đặt trong layout của từng màn, không đặt trong style.
- Khối "Flashing Speed" của Flash Alert = tiêu đề + **2** hàng slider trong cùng một Card.

### B6. Lưới màu
```
RecyclerView rvColors   (GridLayoutManager 6 cột, hoặc FlexboxLayoutManager nếu muốn tự xuống dòng)
item_color_swatch.xml:
FrameLayout 40×40dp
├── View vColor          hình tròn (ShapeableImageView hoặc View nền oval), tint = màu
└── View vSelectedRing   vòng viền primary 2dp, chỉ hiện khi đang chọn
```
- Ô đầu tiên là **nút bảng màu** (icon palette) → dùng **2 view type** trong adapter, hoặc `ConcatAdapter`
  (1 adapter cho nút bảng màu + 1 adapter cho các màu), giống cách bạn từng làm thẻ "+".
- Màu đang chọn có **vòng sáng** quanh (Figma Screen Light: ô xanh dương có viền).
- Edge Lighting dùng nút **+** thay vì bảng màu, và có chế độ **xoá** (nút x nhỏ trên mỗi màu) trong hộp thoại Edit.

### B7. Ô danh sách có lựa chọn (component "Frame 1707478416")
Figma có 4 trạng thái: thường, **đã chọn** (chữ xanh + dấu tích xanh), và 2 bản sáng (không dùng vì app tối).
```
MaterialCardView (Widget.App.Card, radius_item, padding 8/12)
└── LinearLayout ngang
    ├── ImageView ivFlag / ivAppIcon   32dp tròn
    ├── TextView tvName                0dp + weight 1, marginStart 12dp
    └── ImageView ivCheck              24dp: vòng rỗng hoặc tích xanh
```
Dùng **selector theo `state_selected`** cho màu chữ (`text_primary` → `primary`) và hình `ivCheck`.
Adapter chỉ cần đặt `itemView.isSelected = …` như bạn đã làm với ô cung hoàng đạo.

### B8. Nút
Đã có Filled / Outlined từ bước setup. Thêm:
- **`.Gradient`** cho "Play Now!" và các nút xanh nổi bật: MaterialButton không vẽ gradient bằng `backgroundTint`
  → đặt `android:background="@drawable/bg_button_gradient"` (shape `<gradient>` primary → primary_gradient_end, bo 12dp)
  **và** `app:backgroundTint="@null"`.
- **Nút xám** (component Button: "Share", nút "Continue" khi chưa bật quyền): đó là trạng thái **disabled** của Filled
  → khai báo `backgroundTint` là selector có `state_enabled="false"` = màu xám. Code chỉ cần `btnContinue.isEnabled = …`.
- **Nút viền màu** ở Home ("Explore Modes" tím, "Set Up Alerts" xanh dương): Outlined, đổi `strokeColor` + `textColor` riêng.

### B9. Chip chọn một
```
ChipGroup (singleSelection = true, selectionRequired = true)
└── Chip × n   style Widget.App.Chip: nền surface, viền stroke; khi checked: viền primary, chữ primary
```
- **Direction** (→ ← ↓ ↑) của LED: dùng `MaterialButtonToggleGroup` gồm 4 icon button vuông là gọn nhất.
- **Run Style** của Edge Lighting là lưới 2 cột 8 nút → `ChipGroup` không xếp lưới đều được; dùng `RecyclerView` Grid 2 cột
  hoặc `GridLayout` cố định, mỗi ô là Chip hoặc button.

### B10. Hộp thoại chọn màu
```
DialogFragment, nền surface bo 16dp, rộng ~ 300dp
└── LinearLayout dọc, padding 16dp
    ├── TextView tvTitle / tvSubtitle     ("Add Border Color", "Set the color of your edge effect") – GONE ở Screen Light
    ├── ColorPickerView                    vùng Saturation/Value ~ 200dp cao
    ├── HueSliderView                      thanh 7 màu
    ├── AlphaSliderView                    thanh trong suốt → màu
    ├── LinearLayout ngang: HEX | R | G | B   (4 EditText nhỏ, có nhãn phía trên)
    ├── MaterialButton btnConfirm         ("Choose" / "Apply")
    └── TextView btnCancel                (chỉ có ở bản Edge)
```
- 3 View trên cùng là **custom View** (vẽ bằng `Canvas` + `LinearGradient`) hoặc dùng thư viện color picker.
  Tự viết là bài tập rất tốt về `onDraw` và `onTouchEvent`; nếu vội thì dùng thư viện.
- Mở hộp thoại bằng `newInstance(initialColor, mode)`; trả màu bằng **Fragment Result API**.
- Gõ HEX → cập nhật picker; kéo picker → cập nhật HEX/RGB: cẩn thận **vòng lặp** (cập nhật qua lại vô hạn).
  Dùng một biến cờ `isUpdatingFromCode` khi gán text bằng code.

### B11. Hộp thoại đánh giá (component "Rate us")
```
DialogFragment, nền surface bo 16dp
└── ConstraintLayout
    ├── ImageButton btnClose          góc phải trên
    ├── ImageView ivMascot            bóng đèn, đổi ảnh theo số sao
    ├── TextView tvTitle              đổi theo số sao
    ├── TextView tvMessage            đổi theo số sao
    ├── RatingBar / 5 ImageView sao   (5 ImageView dễ chỉnh hình hơn RatingBar)
    └── MaterialButton btnSubmit
```
6 trạng thái (0–5 sao) → một bảng `RateState(@DrawableRes mascot, @StringRes title, @StringRes message)`.
Bấm sao thứ n → lấy `states[n]` và gán. **Không** tạo 6 layout.

### B12. Nút tròn ON/OFF (Flash)
```
ConstraintLayout (căn giữa)
├── TextView tvState      "OFF" / "ON"
└── FrameLayout 140dp tròn (clickable)
    ├── View vGlow        vòng sáng xanh mờ, chỉ hiện khi ON
    ├── View vCircle      nền: xám đậm khi OFF, primary khi ON
    └── ImageView ivIcon  đèn pin / đèn báo động / quả cầu disco
```
- Màu nền đổi theo `isSelected` (selector), không đổi bằng code từng màu.
- Vòng sáng: drawable `<shape oval>` + `<gradient type="radial">` từ primary trong suốt dần, hoặc `setShadowLayer`.
- Bấm → animate `scaleX/scaleY` nhẹ (0.95 → 1) cho có cảm giác nhấn.

---

## Phần C. Từng màn hình

Mỗi màn: **khối chính**, **cây View**, **chỗ khó**.

### C1. Splash
Dùng **SplashScreen API**: không cần layout.
- `themes.xml` thêm `Theme.App.Starting` (parent `Theme.SplashScreen`): `windowSplashScreenBackground` = màu xanh,
  `windowSplashScreenAnimatedIcon` = logo, `postSplashScreenTheme` = theme chính.
- Nền gradient + chữ "Flashlight" như Figma **không** làm được hoàn toàn bằng SplashScreen API (chỉ icon + nền 1 màu).
  Nếu bắt buộc giống y Figma: SplashActivity có layout riêng (nền gradient drawable + logo + chữ) và hiện ngay sau splash hệ thống.
  **(Hỏi người hướng dẫn cách công ty làm.)**

### C2. Language
```
ConstraintLayout (background)
├── include view_top_bar          tiêu đề "Language"; endSlot = TextView "Save" màu primary (ẩn khi chưa chọn)
└── RecyclerView rvLanguages      item_language (B7), khoảng cách 8dp giữa các ô, padding ngang 16dp
```
- Ô đã chọn: chữ xanh + tích xanh (B7). Nút Save hiện khi `selected != null`.
- Mở từ Settings: hiện ←, tiêu đề "Choose Language".

### C3. Onboarding (OBD1–4)
```
ConstraintLayout
├── ViewPager2 vpOnboarding       chiếm phần trên
├── LinearLayout (ngang, padding 16dp)
│   ├── dots indicator             chấm hiện tại là thanh dài màu primary, các chấm khác tròn trắng
│   └── MaterialButton btnNext     Outlined nhỏ/nền tối, chữ primary: "Next" / "Get Started!"
└── FrameLayout adContainer       nửa dưới (chỗ quảng cáo native, nếu có)

item_onboarding_page.xml (OnboardingPageFragment):
├── ImageView ivHero      ảnh lớn, scaleType centerCrop, phía dưới có lớp gradient tối dần (View + gradient drawable)
├── TextView tvTitle      căn giữa, Title
└── TextView tvDesc       căn giữa, Caption
```
- **Chấm chỉ trang dạng "thanh dài"**: tự vẽ bằng một `LinearLayout` các View nhỏ, đổi chiều rộng chấm hiện tại khi
  `onPageSelected`. Hoặc dùng `TabLayout` + `TabLayoutMediator` với tab indicator tuỳ chỉnh.
- Ảnh hero: xuất từ Figma **WebP**, đặt `drawable-nodpi` (ảnh lớn không cần nhiều mật độ).

### C4. Permission
```
ConstraintLayout
├── include view_top_bar          "Permission", không ←
├── ImageView ivIllustration      hình minh hoạ
├── TextView tvMessage            "[App name] is requesting access…" (string có tham số tên app)
├── include view_switch_row       "Camera"
└── MaterialButton btnContinue    Filled, disabled (xám) cho đến khi công tắc bật & quyền được cấp
```
Công tắc **không tự bật** khi người dùng gạt: gạt → xin quyền → chỉ bật khi được cấp.

### C5. No internet
- Bản hộp thoại: `DialogFragment` nền surface bo 16dp: icon wifi lớn, tiêu đề, mô tả, nút "Go to Settings" (Filled), "Cancel" (TextView).
- Bản toàn màn hình: cùng nội dung, căn giữa trên nền background. Viết nội dung thành **một** layout `view_no_internet.xml`
  rồi `<include>` vào cả 2 nơi.

### C6. Home
```
ConstraintLayout (background)
└── NestedScrollView
    └── LinearLayout dọc, padding 16dp, khoảng cách các thẻ 12–14dp
        ├── Header: LinearLayout ngang
        │   ├── LinearLayout dọc: tvAppName (Title, 18sp) + tvTagline (Caption, text_secondary)
        │   └── 2 nút tròn 40dp (nền surface): btnCamera, btnSettings
        ├── Thẻ LED (MaterialCardView, radius_card, nền gradient)
        │   ├── Hàng: icon vuông bo góc (nền primary_20) + "Hero Section" / "LED" + nút "Play Now!" (Gradient, 100×36)
        │   ├── Khung chữ chạy: FrameLayout viền primary, nền chấm bi → MarqueeView bên trong
        │   └── Hàng dưới: "• Speed: 5s", "RGB Multi-Color", "Custom Text →" (primary)
        ├── LinearLayout ngang 2 thẻ bằng nhau (mỗi thẻ 0dp + weight 1, khoảng cách 12dp)
        │   ├── Thẻ Flash: icon vuông tím, "Flash", mô tả, nút viền tím "Explore Modes"
        │   └── Thẻ Flash Alert: icon vuông xanh, "Flash Alert", mô tả, nút viền xanh "Set Up Alerts"
        ├── Thẻ Screenlight: hàng icon + tiêu đề + mô tả + nút → ; đường kẻ ; hàng "Preset:" + 5 chấm màu + "Adjust ›"
        └── Thẻ Edge Lighting: hàng icon + tiêu đề + mô tả + nút → ; đường kẻ ; thanh gradient + "Aura Rainbow FX"
```
Chỗ khó:
- **Nền thẻ có gradient mờ** (thẻ LED và các thẻ khác: đen + gradient primary 20% → trong suốt, viền màu mờ):
  dùng `layer-list` gồm lớp `<solid>` đen và lớp `<gradient>` với màu có alpha (`#3334D399` → `#00000000`), bo 24dp.
  Viền: `MaterialCardView` `strokeColor` màu mờ (Figma: `#60A5FA` 25% cho thẻ Screenlight).
  Mỗi thẻ có **màu nhấn riêng** (LED xanh lá, Flash tím, Flash Alert xanh dương, Screenlight xanh, Edge tím)
  → 1 drawable cho mỗi màu nhấn, hoặc tô bằng code từ một `@ColorRes`.
- **Icon vuông bo góc** (nền màu + icon trắng): 1 style `Widget.App.FeatureIcon` (40–44dp, bo 12dp), đổi `backgroundTint`.
- **Chữ chạy trong thẻ LED**: dùng lại `MarqueeView` của màn LED (cỡ nhỏ). Đừng viết bản thứ hai.
- **Thanh gradient "Aura Rainbow FX"**: View cao 8dp, nền gradient tím → xanh, bo tròn.
- Chấm màu preset: 5 View tròn 20dp cố định trong layout là đủ (không cần RecyclerView vì không đổi số lượng).

### C7. Flash (3 tab)
```
FlashActivity:
ConstraintLayout
├── include view_top_bar          "Flash"
├── TabLayout tabs                3 tab có icon + chữ: Flash Light / SOS Light / DJ Mode; indicator xanh dưới tab chọn
└── ViewPager2 vpModes            userInputEnabled = false (không cho vuốt, tránh bật nhầm đèn khi vuốt)

fragment_flash_light.xml / fragment_sos.xml / fragment_dj.xml:
ConstraintLayout → include view_power_button (B12), chỉ khác icon
```
- Tab: `TabLayoutMediator` + `tab.setIcon()` + `tab.text`. Màu icon/chữ chọn/không chọn bằng `tabIconTint`, `tabTextColor`
  (selector), `tabIndicatorColor` = primary.
- 3 Fragment giống nhau 90% → có thể chỉ viết **một** `FlashModeFragment` nhận `mode` qua arguments.
- Trạng thái ON/OFF đọc từ `TorchController.state`, **không** lưu trong Fragment.

### C8. Camera
```
ConstraintLayout (đen)
├── androidx.camera.view.PreviewView previewView   chiếm phần giữa
├── LinearLayout ngang (đè lên đáy preview): icon "−" + Slider zoom + icon "+"
└── LinearLayout ngang (thanh dưới, nền tối): btnClose (X tròn) · btnShutter (tròn trắng 64dp) · btnTorch (tròn)
```
Slider zoom 0..1 → `camera.cameraControl.setLinearZoom(value)`.

### C9. Screen Light
```
ConstraintLayout
├── include view_top_bar          "ScreenLight"
└── NestedScrollView → LinearLayout dọc, padding 16dp
    ├── FrameLayout preview       cao ~140dp, bo 12dp, nền = màu đang chọn; nút phóng to (góc phải trên, nền tối mờ)
    ├── Card: include view_slider_row  "Brightness" "80%"
    └── Card: "Color" + rvColors (B6)

ScreenLightPlayActivity:
FrameLayout (nền = màu đã chọn) + ImageButton btnClose góc trái trên
```
- Đổi độ sáng **ngay khi kéo**: `window.attributes = window.attributes.apply { screenBrightness = value }`.
- Màn Play: ngang, ẩn thanh hệ thống, `keepScreenOn="true"` trên View gốc.

### C10. LED
```
ConstraintLayout
├── include view_top_bar          "LED"
└── NestedScrollView → LinearLayout dọc, padding 16dp, khoảng cách 12dp
    ├── FrameLayout preview       cao ~100dp, nền đen bo 12dp; MarqueeView; nút phóng to
    ├── Card "Text": TextInputLayout (nền surface tối, không gạch chân) + EditText
    ├── Card: view_slider_row "Font Size" + view_slider_row "Scroll Speed"
    ├── Card "Color": rvColors
    ├── Card "Direction": MaterialButtonToggleGroup 4 nút vuông (B9)
    ├── Card "Visual Effects": ChipGroup Glow / Blink / Neon / Fade
    └── Card "Background": RecyclerView ngang: ô "+" (chọn ảnh) + các ảnh nền bo 8dp, ảnh đang chọn có viền primary

LedPlayActivity:
FrameLayout đen (hoặc ảnh nền) + MarqueeView toàn màn hình + btnClose
```
Chỗ khó:
- **`MarqueeView`** (custom View): nhận `text`, `textSizePx`, `speed`, `color`, `direction`, `effect`, `background`.
  `onDraw` vẽ chữ tại vị trí `offset`; một `ValueAnimator` lặp vô hạn tăng `offset` rồi `invalidate()`.
  - Glow / Neon: `paint.setShadowLayer(radius, 0, 0, color)` (cần `setLayerType(LAYER_TYPE_SOFTWARE, null)` cho shadow chữ).
  - Blink: đổi alpha theo thời gian. Fade: alpha giảm dần ở 2 mép.
  - Direction ↓ ↑: dịch theo trục y thay vì x.
- Preview và màn Play dùng **cùng** `MarqueeView`, chỉ khác cỡ. Font size trong preview nên **thu nhỏ theo tỉ lệ**.
- Ô nhập: gõ tới đâu preview đổi tới đó (`doAfterTextChanged`). Nhớ `imeOptions="actionDone"` và insets `ime()`.

### C11. Flash Alert
```
FlashAlertActivity:
ConstraintLayout
├── include view_top_bar          "Flash Alert"
└── LinearLayout dọc, padding 16dp: 3 × NavRowView (B3): Incoming Calls / SMS / Notification

FlashAlertDetailActivity (dùng chung 3 loại):
ConstraintLayout
├── include view_top_bar          tiêu đề theo loại
├── NestedScrollView → LinearLayout dọc
│   ├── FrameLayout 120dp tròn: icon theo loại (nền xanh lá / xanh dương / xanh nhạt)
│   ├── include view_switch_row   "Status: Off/On"
│   ├── NavRowView "Select App"   CHỈ loại Notification; bên phải hiện icon các app đã chọn (LinearLayout chồng icon)
│   └── Card "Flashing Speed": 2 × view_slider_row
└── MaterialButton btnTest        Filled, bám đáy

SelectAppActivity:
├── include view_top_bar          "Select App", endSlot = "Select All"
├── RecyclerView                  item_language (B7) với icon app thay cờ
└── MaterialButton btnDone        hiện khi có ít nhất 1 app được chọn
```
- Các phần khác nhau giữa 3 loại (icon, màu, tiêu đề, có Select App hay không) → một `enum AlertType` chứa
  `@DrawableRes`, `@ColorRes`, `@StringRes`, `hasAppPicker`. Activity chỉ đọc enum, không `if` rải rác.
- Icon app: `packageManager.getApplicationIcon(pkg)`; tải trên luồng nền, vì danh sách có thể dài.
- **Màn Test** (giả lập cuộc gọi đến, khung chat, thông báo trên màn khoá): `DialogFragment` toàn màn hình
  (`STYLE_NO_FRAME`, theme full screen), nội dung chỉ là hình minh hoạ + vài TextView; trong lúc hiện thì cho đèn nháy.

### C12. Edge Lighting
```
ConstraintLayout
├── include view_top_bar          "Edge Lighting"
├── NestedScrollView (top → top bar, bottom → btnApply)
│   └── LinearLayout dọc, padding 16dp, khoảng cách 12dp
│       ├── include view_switch_row          "Enable Edge Lighting to begin"
│       ├── Card "Color" (tiêu đề + "Edit" bên phải) + rvColors (ô đầu là "+")
│       ├── Card "Border Style"
│       │   ├── ChipGroup ngang cuộn được (HorizontalScrollView): Weather & Nature / Arrow / Commerce / …
│       │   └── RecyclerView Grid 6 cột: icon kiểu viền, ô chọn có nền primary
│       ├── Card "Run Style": lưới 2 cột 8 nút (None, Rotation, Run, R Run, Crawler, R Crawler, Snake, R Snake)
│       ├── Card "Border": 4 × view_slider_row (Top Radius, Bottom Radius, Width, Color Speed) – không hiện giá trị
│       └── Card "Notch Style": hàng 4 ô (hình điện thoại + nhãn), ô chọn viền primary
└── MaterialButton btnApply       bám đáy

Lớp EdgeLightView phủ toàn màn (preview viền chạy quanh màn) – chỉ hiện khi công tắc bật.
```
- **Preview viền** phải chạy **ngay trên màn cấu hình** (Figma: icon bướm chạy quanh mép khi bật). Đặt `EdgeLightView`
  là View trên cùng của `ConstraintLayout` gốc, `match_parent`, `clickable="false"` để không chặn thao tác.
- Đổi slider / màu / kiểu → `EdgeLightView` vẽ lại ngay. Đây là lý do cấu hình nên nằm trong **một** `EdgeConfig`
  trong ViewModel: View chỉ cần `render(config)`.
- 3 hộp thoại: Add Border Color (B10), Edit Border Color (lưới màu có nút x + Choose/Cancel), Choose Display Mode
  (3 × view_switch_row + Apply/Cancel). Cả 3 dùng chung một khung `DialogFragment` nền surface bo 16dp.

### C13. Settings
```
ConstraintLayout
├── include view_top_bar          "Settings"
└── LinearLayout dọc, padding 16dp, khoảng cách 8dp
    ├── NavRowView Language       tvValue = tên ngôn ngữ hiện tại (primary)
    ├── NavRowView Share
    ├── NavRowView Rate app
    ├── NavRowView Privacy policy
    └── NavRowView Policy setting
```
Mỗi hàng có icon màu khác nhau (Figma: xanh, cam, vàng, xanh lá, tím) → tham số `app:icon` + `app:iconTint`.

---

## Phần D. Tài nguyên cần chuẩn bị từ Figma

| Loại | Cách xuất | Đặt ở |
|---|---|---|
| Icon nét (component **Icon**, các icon trong hàng, tab) | Chọn icon → Export **SVG** → Android Studio *New → Vector Asset → Local file* | `drawable/ic_*.xml` |
| Cờ quốc gia | SVG → Vector Asset | `drawable/flag_*.xml` |
| Ảnh lớn (Onboarding, ảnh nền LED, minh hoạ Permission, mascot Rate) | Export **WebP** 2x hoặc 3x | `drawable-nodpi/` |
| Icon kiểu viền (Border Style) | SVG → Vector Asset (có thể rất nhiều, đặt tên theo nhóm: `edge_weather_01`…) | `drawable/` |
| Logo app | Android Studio *Image Asset* (adaptive icon) | `mipmap-*` |

Quy tắc đặt tên: `ic_` icon, `bg_` nền/shape, `img_` ảnh, `flag_` cờ, `selector_` selector màu/drawable.

---

## Phần E. Thứ tự dựng giao diện khuyên dùng

1. **Phần B**: B8 nút → B2 thẻ → B1 top bar → B3, B4, B5 hàng → B7 ô danh sách → B6 lưới màu.
   Làm một **màn nháp** (`ComponentsPreviewActivity`, chỉ dùng lúc dev) đặt tất cả component lên để so với Figma.
2. Language → Permission → Settings: toàn component có sẵn, ghép là xong, luyện tay.
3. Onboarding (ViewPager2 + chấm).
4. Flash (TabLayout + B12).
5. Screen Light (+ B10 hộp thoại chọn màu).
6. Home (thẻ gradient, chưa cần chữ chạy thật).
7. LED + `MarqueeView` → quay lại gắn vào thẻ Home.
8. Flash Alert (+ Select App).
9. Edge Lighting + `EdgeLightView`.

**Mỗi màn xong**: chụp màn hình máy ảo đặt cạnh Figma, sửa đến khi khớp rồi mới sang màn khác.
