# Writable

A tablet-first Android application for annotating and editing PDF documents with stylus support. Import existing PDFs or create blank documents, then annotate pages with pressure-sensitive ink — built entirely with modern Android tech.

---

## Features

- **Import PDF** — open any PDF from device storage; each page is rendered and stored independently
- **Blank documents** — create documents from scratch without any source file
- **Stylus-only drawing** — input is intentionally restricted to stylus/pen (touch ignored), optimised for tablet workflows
- **Pressure-sensitive strokes** — pen pressure is captured per-point and preserved in storage
- **Configurable pen** — choose stroke color and width (4–64 dp range) per document
- **Page management** — add, reorder, and remove individual pages
- **Document library** — home screen grid with customisable cover/spine/bookmark colours

---

## Architecture

The project follows a layered Clean Architecture approach with a clear separation between the data and UI layers.

```
app/
└── src/main/java/com/vastausf/writable/
    ├── data/
    │   ├── db/             # Room database, entities, DAOs, migrations
    │   ├── di/             # Hilt modules
    │   ├── pageCanvas/     # Stroke/point data models
    │   ├── pdfImporter/    # PDF rendering pipeline
    │   └── repository/     # Single source of truth for all data
    ├── navigation/         # Type-safe Compose Navigation routes
    └── ui/
        ├── screens/        # HomeScreen + EditorScreen with ViewModels
        ├── theme/          # Material3 theming
        └── widgets/        # DrawingLayer, StylusPanel, DocumentCover, …
```

### Database

The app uses **Room** with two tables and a schema that is designed around independent page access.

#### `documents`

| Column | Type | Description |
|---|---|---|
| `id` | Long (PK) | Auto-generated |
| `title` | String | Document name |
| `createdAt` | Long | Creation timestamp |
| `openedAt` | Long | Last-opened timestamp (used for home sort) |
| `pagesIds` | String | Comma-separated ordered list of page IDs |
| `coverColor` | Int | ARGB cover colour |
| `spineColor` | Int | ARGB spine colour |
| `bookmarkColor` | Int | ARGB bookmark colour |
| `stylusWidth` | Float | Saved pen width for this document |
| `stylusColor` | Int | Saved pen colour for this document |

#### `pages`

| Column | Type | Description |
|---|---|---|
| `id` | Long (PK) | Auto-generated |
| `documentId` | Long | Owning document |
| `type` | Enum | `Blank` or `Image` |
| `ratio` | Float | Aspect ratio (width / height) |
| `url` | String? | File path for image-backed pages |
| `canvasData` | ByteArray? | CBOR-serialised stroke array |

**Why this split?**

The document row acts as a lightweight manifest: it tells the app *which* pages exist and in *what order* without touching the pages table at all. When the editor needs to render a specific range of pages (e.g. the visible viewport ± 2), it reads only those page IDs from the ordered list and issues a targeted `SELECT … WHERE id IN (…)` — no full-table scan, no deserialisation of unneeded strokes.

### PDF Import Pipeline

Importing a PDF does not keep the original file. Instead:

1. Android's `PdfRenderer` opens the file via a `ParcelFileDescriptor`.
2. Each requested page is rendered to a `Bitmap` at **2× the native resolution**.
3. The bitmap is written as a `PNG` to the app's internal `filesDir` under the name `page_{id}.png`.
4. A `PageEntity` of type `Image` is inserted, pointing at that file.

This makes every page **self-contained and independent of the source PDF**. The source file can be moved or deleted without affecting the document. Pages can also be mixed freely — a single document can contain both imported PDF pages and newly created blank pages.

### Canvas Data

The background image (the rendered PDF page PNG) and the user's ink are stored separately:

- **Background** — a file on disk, loaded lazily with Coil.
- **Ink (canvas data)** — stored in the `canvasData` column of the `pages` table as a CBOR binary blob.

The in-memory representation is a list of `Stroke` objects:

```kotlin
@Serializable
data class Stroke(
    val points: List<StrokePoint>,
    val color: Int,
    val width: Float,
)

@Serializable
data class StrokePoint(
    val x: Float,
    val y: Float,
    val pressure: Float,
)
```

CBOR (Concise Binary Object Representation) was chosen over JSON because it produces significantly smaller blobs for float-heavy stroke arrays. Serialisation and deserialisation happen only when a page is loaded or saved — the rest of the time the app works with plain Kotlin objects.

Writes are **debounced 500 ms** after the last stroke completes, batching rapid input into a single DB write.

### Lazy Page Loading

The editor uses a `LazyColumn` and tracks the currently visible page range. Only pages within a **±2 window** around the visible centre are kept in memory as `PageState` objects (page entity + deserialised strokes). Pages outside this range are evicted from the ViewModel's state map, freeing heap space. This keeps memory usage bounded regardless of document length.

### Drawing Layer

`DrawingLayer` is a Compose `Canvas` that:

- Filters all pointer events to `PointerType.Stylus` only — finger touch is completely ignored by design.
- Accumulates points into the current stroke in real-time.
- Renders existing strokes and the in-progress stroke using **quadratic Bézier interpolation** for smooth curves.
- Emits a completed `Stroke` via callback when the stylus is lifted; the ViewModel appends it and schedules a debounced save.

---

## Tech Stack

| Category | Library / Tool |
|---|---|
| Language | Kotlin 2.3.20 |
| UI | Jetpack Compose (BOM 2026.03.01), Material3 |
| Navigation | Navigation Compose 2.9.7 (type-safe serialised routes) |
| Database | Room 2.8.4 |
| Dependency Injection | Hilt 2.59.2 |
| Serialisation | kotlinx-serialization-cbor + json 1.11.0 |
| Image Loading | Coil 2.7.0 |
| PDF Rendering | Android framework `PdfRenderer` |
| Async | Kotlin Coroutines + Flow |
| Testing | JUnit 4, MockK, Truth, Turbine |
| Code Generation | KSP 2.3.6 |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 36 |

---

## Testing

Unit tests live in `app/src/test/` and cover the repository layer in isolation using hand-written fakes (`FakeDocumentDao`, `FakePageDao`, `FakePdfPageRenderer`, `FakeFileStorage`). Flow assertions use the **Turbine** library; no mocking framework is required for the data layer because the interfaces are small enough to implement directly. 18 test cases cover document CRUD, page ordering, batch page operations, and image cleanup on delete.

---

## Design Decisions

| Decision | Rationale                                                                                                 |
|---|-----------------------------------------------------------------------------------------------------------|
| Stylus-only input | Eliminates accidental palm marks; the app targets tablet + stylus workflows exclusively                   |
| Pages independent of source PDF | The document remains intact even if the source file is later deleted or moved                             |
| Background and ink stored separately | Allows the ink layer to be cleared, replaced, or synced independently without re-importing the page image |
| CBOR for stroke data | smaller than equivalent JSON for float arrays; no schema overhead                                         |
| Debounced DB writes | Prevents write amplification during fast drawing — strokes commit once per "pen lift + 500 ms idle"       |
| Document manifest (pagesIds list) | Random-access page lookup without scanning the pages table; supports reordering without row updates       |
