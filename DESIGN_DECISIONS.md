# Design Decisions: Separate Module Classes per Backend

## Decision

Instead of a single `GraphicsModule` with backend selection via config:
```scala
// OLD APPROACH (not chosen)
val graphics = new GraphicsModule(GraphicsConfig(backend = OpenGL))
```

We use **separate module classes** for each backend:
```scala
// NEW APPROACH (chosen)
val graphics = new LwjglOpenGLGraphicsModule(OpenGLConfig(...))
val vulkanGraphics = new LwjglVulkanGraphicsModule(VulkanConfig(...))
```

## Rationale

### 1. **Type Safety**
- Compile-time guarantee of which backend you're using
- No runtime errors from invalid backend/config combinations
- Better IDE support and autocomplete

### 2. **Clearer API**
- `LwjglOpenGLGraphicsModule` is immediately clear about what it does
- No need to check config to know which backend
- Self-documenting code

### 3. **Backend-Specific Configuration**
- Each backend can have its own config type with relevant options
- OpenGL has `profile: OpenGLProfile`
- Vulkan has `validationLayers: Boolean` and `requiredExtensions: Seq[String]`
- WebGL has `alpha: Boolean`, `antialias: Boolean`
- No need to have a union type with optional fields for all backends

### 4. **Better Separation of Concerns**
- Each backend module is independent
- Easier to maintain and test
- Can evolve independently

### 5. **Platform-Specific Availability**
- Some backends only available on certain platforms
- `LwjglVulkanGraphicsModule` only compiles on JVM
- `WebGPUGraphicsModule` only compiles on JS
- Type system enforces this

### 6. **Easier to Extend**
- Adding a new backend doesn't require modifying shared enums/configs
- Just create a new module class
- No breaking changes to existing code

## Module Class Naming Convention

```
{Platform}{Backend}GraphicsModule
```

Examples:
- `LwjglOpenGLGraphicsModule` - LWJGL platform, OpenGL backend
- `LwjglVulkanGraphicsModule` - LWJGL platform, Vulkan backend
- `WebGLGraphicsModule` - Browser platform, WebGL backend
- `WebGPUGraphicsModule` - Browser platform, WebGPU backend
- `NativeOpenGLGraphicsModule` - Native platform, OpenGL backend (future)

## Configuration Types

Each backend has its own config type:

```scala
// OpenGL
case class OpenGLConfig(
  version: (Int, Int),
  profile: OpenGLProfile,
  vsync: Boolean,
  // ... OpenGL-specific options
)

// Vulkan
case class VulkanConfig(
  apiVersion: (Int, Int, Int),
  validationLayers: Boolean,
  requiredExtensions: Seq[String],
  // ... Vulkan-specific options
)

// WebGL
case class WebGLConfig(
  version: Int, // 1 or 2
  alpha: Boolean,
  antialias: Boolean,
  // ... WebGL-specific options
)
```

All configs extend a base `GraphicsConfig` trait for shared options:
```scala
trait GraphicsConfig {
  def vsync: Boolean
  def msaa: Int
  def depthBits: Int
  def stencilBits: Int
}
```

## Usage Pattern

```scala
// JVM - OpenGL
val openGLGraphics = new LwjglOpenGLGraphicsModule(
  OpenGLConfig(version = (3, 3), profile = OpenGLCoreProfile)
)

// JVM - Vulkan
val vulkanGraphics = new LwjglVulkanGraphicsModule(
  VulkanConfig(apiVersion = (1, 0, 0), validationLayers = true)
)

// Browser - WebGL
val webGLGraphics = new WebGLGraphicsModule(
  WebGLConfig(version = 2, alpha = true, antialias = true)
)

// All implement the same GraphicsModule trait
runtime.addModule(openGLGraphics)  // or vulkanGraphics, webGLGraphics, etc.
```

## Trade-offs

### Pros ✅
- Type safety
- Clearer API
- Backend-specific options
- Better separation
- Platform enforcement
- Easier to extend

### Cons ⚠️
- More classes to maintain
- Can't switch backends at runtime (but this is rarely needed)
- Slightly more verbose (but clearer)

## Conclusion

The benefits of separate module classes far outweigh the costs. The type safety, clarity, and flexibility make this the better design choice for a cross-platform graphics system.
