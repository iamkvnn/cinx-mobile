# Cinx – EduFuture Android App

## Project Overview
**EduFuture** is a Vietnamese-language e-learning Android app (mock/prototype).  
Package: `com.app.cinx` | minSdk 31 | targetSdk 36 | Language: **Java**

---

## Architecture & Patterns

| Concern | Approach |
|---------|----------|
| UI layer | Plain `AppCompatActivity` + XML layouts (no Fragments) |
| State | Singleton repositories + `UserManager` |
| Navigation | Manual `Intent`-based; `NavHelper.setupNavigation()` for bottom nav |
| Data | 100 % mock — `SampleCourseData.java`; no real network calls |
| Network | `api/` package exists but is **empty** — no Retrofit/OkHttp yet |
| DI | None — singletons accessed via `getInstance()` |

> There is **no ViewModel, no LiveData, no Room, no Jetpack Navigation**.  
> When adding features, follow the existing singleton + Activity pattern unless explicitly upgrading the architecture.

---

## Module Inventory

### `activity/` — Screens
| File | Purpose |
|------|---------|
| `MainActivity` | Landing; switches layout between guest (`activity_main`) and logged-in (`activity_main_logged_in`) based on `UserManager` |
| `LoginActivity` | Email/password login → navigates to `MainActivity`; links to `RegisterActivity` and `ForgotPasswordActivity` |
| `RegisterActivity` | New account form |
| `ForgotPasswordActivity` | 4-step password recovery: email → OTP → new password → success |
| `DiscoveryActivity` | Course search/browse |
| `CourseDetailActivity` | Full course info, curriculum, enroll |
| `LessonActivity` | Video/document/quiz lesson viewer |
| `MyLearningActivity` | "Khóa học của tôi" — three-tab (Đang học / Hoàn thành / Đã lưu) enrolled-course list with real-time search and filter bottom sheet |
| `CartActivity` | Shopping cart with swipe-to-delete |
| `CheckoutActivity` | Order review + payment method selection |
| `PaymentMethodsActivity` | Manage saved payment methods |
| `PaymentSuccessActivity` | Post-purchase confirmation |
| `ProfileActivity` | User profile, linked wallets, vouchers |
| `PurchaseHistoryActivity` | Order history with tab filter |
| `VouchersActivity` | Available vouchers |
| `CertificatesActivity` | Earned certificates |

### `adapter/` — RecyclerView Adapters
One adapter per list type. Naming: `<EntityName>Adapter`.  
`CartAdapter` uses `ItemTouchHelper` for swipe-to-delete.  
`CardStackAdapter` drives the swipeable card stack on the discovery screen.  
`MyLearningAdapter` uses three view types (PROGRESS / COMPLETED / SAVED) driven by `EnrolledCourse.Status`.

### `data/` — Repository Layer
- `CartRepository` — singleton, holds `List<CartItem>`, seeded with sample data.
- `OrderRepository` — singleton, holds `List<Order>`.
- `SampleCourseData` — static factory for mock `Course` lists.

### `model/` — Plain POJOs
`Course`, `Lesson`, `Chapter`, `CartItem`, `Order`, `OrderItem`,  
`Voucher`, `PaymentMethod`, `BankCard`, `LinkedWallet`,  
`Certificate`, `Testimonial`, `Goal`, `QuizQuestion`, `QuizOption`, `LessonType`, `ProfileVoucher`,  
`EnrolledCourse` (with inner `Status` enum; three static factories: `progress()`, `completed()`, `saved()`)

### `util/`
| File | Purpose |
|------|---------|
| `UserManager` | Singleton; tracks `isLoggedIn` + `userEmail` in memory (not persisted) |
| `NavHelper` | Sets up bottom navigation bar and highlights active tab |
| `ToastUtil` | Shows a custom-styled toast anchored top-right |
| `Convert` | Unit/format conversion helpers |

### `config/` — SVG Support
`SvgDecoder`, `SvgDrawableTranscoder`, `SVGModule` — Glide module for rendering SVG images via `androidsvg`.

### `api/`
Empty placeholder package reserved for future Retrofit service interfaces.

---

## Design System

### Color Palette (`res/values/colors.xml`)
| Token | Hex | Usage |
|-------|-----|-------|
| `primary` | `#7C3AED` | Purple — buttons, focused borders, links |
| `primary_variant` | `#6D28D9` | Darker purple |
| `secondary` | `#EC4899` | Pink accents |
| `background` | `#F2F4F8` | Screen background |
| `text_primary` | `#1E293B` | Main text |
| `text_secondary` | `#64748B` | Subtitles, icons |
| `text_placeholder` | `#94A3B8` | Input hints |
| `error` | `#EF4444` | Validation errors |
| `success_green` | `#10B981` | Success states |

### Glass Morphism Components
| Drawable | Description |
|----------|-------------|
| `bg_glass_card` | White 65% opacity, 40dp corners, subtle border — main card container |
| `bg_glass_input` | Input field; focused state adds purple border + 80% white bg |
| `bg_otp_input` | Square OTP digit box (same glass treatment, 16dp corners) |
| `bg_btn_primary` | Dark gradient #1E293B → #0F172A, 16dp corners |
| `bg_main_background` | Solid `#F2F4F8` + blob radial gradients |

### Drawable Naming Conventions
- `bg_*` — shape/state-list backgrounds
- `ic_*` — vector icons (SVG-sourced, all in XML)

### Layout Naming Conventions
- `activity_*` — full-screen activity layouts
- `item_*` — RecyclerView row layouts
- `layout_*` — reusable partial layouts (bottom sheets, panels)
- `view_*` — sub-view layouts (lesson content panels)

---

## Dependencies (`gradle/libs.versions.toml`)
| Library | Version | Note |
|---------|---------|------|
| `androidx.appcompat` | 1.7.1 | Base activity, widgets |
| `com.google.android.material` | 1.13.0 | MaterialButton, etc. |
| `androidx.constraintlayout` | 2.2.1 | Primary layout system |
| `androidx.recyclerview` | 1.3.2 | All lists |
| `androidx.cardview` | 1.0.0 | Card UI |
| `com.github.bumptech.glide` | 4.16.0 | Image loading (+ SVG module) |
| `com.airbnb.android:lottie` | 6.4.0 | Lottie JSON animations (used in lesson player) |
| `androidx.viewpager2` | 1.1.0 | Onboarding / tab paging |
| `androidx.coordinatorlayout` | 1.2.0 | |
| `com.caverock:androidsvg` | 1.4 | SVG rendering via Glide |

---

## Navigation Flow
```
MainActivity (guest)
    └─► LoginActivity ──► RegisterActivity
                      └─► ForgotPasswordActivity (4 steps)
                      └─► MainActivity (logged-in)

MainActivity (logged-in)  [bottom nav via NavHelper]
    ├─ Home          → MainActivity
    ├─ Discover      → DiscoveryActivity → CourseDetailActivity → LessonActivity
    ├─ My Learning   → MyLearningActivity
    └─ Profile       → ProfileActivity
                          ├─ PurchaseHistoryActivity
                          ├─ VouchersActivity
                          ├─ CertificatesActivity
                          └─ PaymentMethodsActivity

CartActivity → CheckoutActivity → PaymentSuccessActivity
```

---

## Activity Boilerplate
Every activity follows this pattern:
```java
public class XxxActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xxx);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        // ... findViews, setListeners
    }
}

---

## MyLearningActivity – Implementation Notes

**Screen**: "Khóa học của tôi" — three-tab list of the user's enrolled courses.

### Layout Structure
`activity_my_learning.xml` uses `CoordinatorLayout` + `AppBarLayout` (same pattern as `PurchaseHistoryActivity`):
- **AppBar** (`bg_purchase_history_header`): title bar (back button + bell icon with notification dot), search row (`etSearch` + `btnFilter` with `filterActiveDot`), `HorizontalScrollView` containing the three tab buttons.
- **Content area** (`app:layout_behavior="@string/appbar_scrolling_view_behavior"`): `RecyclerView` (`rvCourses`, paddingBottom=96dp for nav) + `LinearLayout` (`layoutEmpty`, shown when list is empty).
- **Floating nav**: `include layout="@layout/layout_floating_nav"` at bottom center.

### Data Model — `EnrolledCourse`
`model/EnrolledCourse.java` represents a course in one of three states via the inner `Status` enum:

| Factory method | Status | Extra fields |
|---------------|--------|-------------|
| `EnrolledCourse.progress(...)` | `PROGRESS` | `progress` (0–100), `nextLesson`, `lastAccessed` |
| `EnrolledCourse.completed(...)` | `COMPLETED` | `completionDate`, `grade` |
| `EnrolledCourse.saved(...)` | `SAVED` | `originalPrice`, `rating` |

Common fields: `id`, `title`, `instructor`, `imageUrl`, `category`.

### Adapter — `MyLearningAdapter`
`adapter/MyLearningAdapter.java` uses three view types:
| Constant | Value | Item Layout |
|----------|-------|-------------|
| `VT_PROGRESS` | 0 | `item_course_progress.xml` |
| `VT_COMPLETED` | 1 | `item_course_completed.xml` |
| `VT_SAVED` | 2 | `item_course_saved.xml` |

Implement `MyLearningAdapter.OnCourseActionListener` to receive:
- `onContinueLearning(course)` — "Tiếp tục học" button
- `onGetCertificate(course)` — "Xem chứng chỉ" button
- `onRateCourse(course)` — "Đánh giá" button
- `onViewSavedCourse(course)` — "Xem khóa học" button

Call `adapter.updateList(newList)` to refresh displayed items.

### Tab & Filter State
```java
private enum Tab { PROGRESS, COMPLETED, SAVED }
private Tab    currentTab    = Tab.PROGRESS;
private String activeSort     = "recent";   // applied sort
private String activeCategory = "all";      // applied category
private String tempSort       = "recent";   // pending in bottom sheet
private String tempCategory   = "all";      // pending in bottom sheet
```
Tab style: `setBackgroundResource(R.drawable.bg_order_tab_active/inactive)` + `setTextColor(R.color.primary / R.color.text_secondary)`.

### Filter Bottom Sheet
Inflated from `layout_my_learning_filter.xml` into a `BottomSheetDialog`.  
**Two-phase commit**: `temp*` fields track unsaved choices; `applyFilters()` copies them to `active*`, then calls `renderCourses()` and dismisses. On sheet dismiss without applying, `tempSort/tempCategory` are reset to `active*`.

Sort options (chips): `chipSortRecent` ("recent"), `chipSortHigh` ("progress_high"), `chipSortLow` ("progress_low").  
Category chips: `chipCatAll`, `chipCatDesign`, `chipCatCoding`, `chipCatBusiness`.  
Chip active state: `bg_filter_chip_active` (purple fill, white text) vs `bg_filter_chip` (transparent + border, secondary text).  
`filterActiveDot` (red oval on the filter button) is `VISIBLE` when any non-default filter is active.

### New Drawables Added
| Drawable | Description |
|----------|-------------|
| `ic_bell` | Bell icon for notification area |
| `ic_more_vert` | 3-dot vertical overflow icon |
| `ic_sliders` | Filter/sliders icon (3 lines with knobs) |
| `bg_notification_dot` | Red oval (`@color/error`) for bell badge |
| `bg_filter_chip` | Pill with transparent fill + `#E2E8F0` stroke |
| `bg_filter_chip_active` | Solid `@color/primary` pill |
| `bg_next_lesson_box` | `#F8FAFC` bg + `#E2E8F0` border, 12dp radius — "next lesson" info block |
| `bg_ml_badge` | `#EDE9FE` (violet-100) fill, 4dp radius — "TIẾP THEO" label |
| `bg_ml_cert_btn` | `#EDE9FE` fill, 12dp radius — certificate button background |
| `bg_progress_bar_ml` | layer-list: `#E2E8F0` track + `#8B5CF6→#EC4899` gradient clip |
```
Register in `AndroidManifest.xml` with `android:theme="@style/Theme.AppCompat.Light.NoActionBar"`.

---

## ForgotPasswordActivity – Implementation Notes

**Flow**: Email → OTP (4-digit mock) → New Password → Success

| Step | View ID | Key logic |
|------|---------|-----------|
| 1 | `view_forgot` | Email regex validation, masked email passed to step 2 |
| 2 | `view_otp` | Auto-focus next/prev OTP box; `CountDownTimer` for resend cooldown |
| 3 | `view_reset` | Password ≥ 6 chars, match check, 4-segment strength bar |
| 4 | `view_success` | Navigate back to `LoginActivity` with `FLAG_ACTIVITY_CLEAR_TOP` |

Back button navigates backward through steps (not just `finish()`).

---

## Key Conventions & Rules

1. **No real backend** — all data is hardcoded mock. Treat any "submit" as instantly successful (with optional `Handler.postDelayed` for UX realism).
2. **Vietnamese UI strings** — all user-facing text is in Vietnamese. Keep it consistent.
3. **No string resources for new text** — existing code embeds strings inline; follow the same pattern unless refactoring.
4. **Shake animation** for invalid input — use `TranslateAnimation` + `CycleInterpolator(5)` at 300 ms (pattern from `LoginActivity.shakeView()`).
5. **ToastUtil** for success/info toasts; `setError()` on `EditText` for inline validation.
6. **Custom input background** — always use `bg_glass_input` for EditText backgrounds.
7. **No Kotlin** — project is pure Java; do not introduce Kotlin files.
8. **Assets folder** contains reference HTML prototypes (`forgot_pass.html`, etc.) — these are design specs, not used at runtime.

---

## Common Pitfalls

- `UserManager` state is in-memory only — resets on process kill. Do not rely on it for anything persisted.
- `NavHelper.setupNavigation()` must be called **after** `setContentView` and only on logged-in layouts that have the floating nav included.
- OTP fields use `android:inputType="number"` + `android:maxLength="1"` — use `TextWatcher.afterTextChanged` (not `onTextChanged`) for reliable auto-advance.
- Always call `cancelResendTimer()` in `onDestroy` to avoid memory leaks from `CountDownTimer`.
