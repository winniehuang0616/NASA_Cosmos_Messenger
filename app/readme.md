# 🚀 NASA Cosmos Messenger App

以 NASA APOD (Astronomy Picture of the Day) 為核心的 Android 聊天 App。  
使用 Kotlin + Jetpack Compose，支援查詢、收藏、聊天紀錄保存、離線快取與圖片分享。

---

## App 功能

- 聊天輸入日期或自然語句查詢 APOD
- 顯示 APOD 卡片（圖片、標題、描述）
- 聊天訊息自動平滑捲到底部
- 長按 APOD 卡片加入收藏
- 收藏頁刪除收藏
- 收藏頁點擊卡片彈出「生日星空卡」
- 分享「生日星空卡」合成圖片到原生分享面板

---

## 專案架構

**MVVM + Room + Retrofit**：

- `presentation`：Compose UI + ViewModel（狀態更新與流程協調）
- `data/local`：Room Database / Entity / DAO（SQL 與 CRUD）
- `data/remote`：Retrofit API 與 DTO
- `core/util`：共用工具（日期解析）

---

## 專案目錄（主要）

- `presentation/`
  - `nova/`：聊天頁、聊天狀態、聊天資料流
  - `favorites/`：收藏頁、收藏狀態、分享資料流
  - `theme/`：Compose 主題
- `data/local/`
  - `entity/`：`ApodEntity`、`FavoriteApodEntity`、`ChatMessageEntity`
  - `dao/`：`ApodDao`、`FavoriteApodDao`、`ChatMessageDao`
  - `AppDatabase`、`RoomModule`
- `data/remote/`
  - `api/NasaApodApiService`
  - `dto/ApodResponseDto`
  - `NasaApiModule`、`NasaApiConfig`
- `core/util/ApodDateParser.kt`

---

## 聊天資料流（本地快取）

1. 使用者在 `NovaScreen` 輸入內容  
2. `NovaViewModel` 解析文字中的日期  
3. 先查本地 `apod_items`（local-first）  
4. 本地沒有才呼叫 NASA APOD API  
5. 成功資料回寫 Room（`apod_items` / `chat_messages`）  
6. 透過 Flow + StateFlow 推送到 UI

---

## Room 資料表

### 1) `apod_items`

- `date` (PK)
- `title`
- `explanation`
- `mediaType`
- `url`
- `hdUrl`
- `thumbnailUrl`
- `copyright`
- `updatedAt`

### 2) `favorite_apods`

- `date` (PK, FK -> `apod_items.date`)
- `savedAt`

### 3) `chat_messages`

- `id` (PK)
- `role`（`USER` / `SYSTEM` / `ERROR`）
- `text`
- `apodDate` (nullable, FK -> `apod_items.date`)
- `createdAt`

---

## 日期解析支援

- `yyyy-MM-dd`
- `yyyy/MM/dd`
- `yyyy~MM~dd`
- `yyyy.MM.dd`
- `yyyy|MM|dd`
- `yyyy MM dd`
- `yyyyMMdd`
- 可從句子中擷取日期（例如：`show me photo of 2025/03/01`）

---

## Bonus 功能

- 聊天記錄持久化（重開 App 仍可看到）
- APOD local-first 快取查詢
- 收藏頁「生日星空卡」彈窗
- 生日星空卡合成圖片分享（非純文字分享）

---

## 技術棧

- Kotlin
- Jetpack Compose
- Android ViewModel + StateFlow
- Coroutines + Flow
- Room (SQLite)
- Retrofit + OkHttp
- Coil
- Navigation Compose

---

## API Key 管理

- `local.properties` → `BuildConfig` → `NasaApiConfig`

---

## Reference

- NASA APOD API: https://api.nasa.gov/


