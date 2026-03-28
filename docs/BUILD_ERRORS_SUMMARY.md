# Build Errors Summary

## Current Status

### ✅ Compiles Successfully
- `runtimeJVM` - ✅
- `runtimeJS` - ✅  
- `graphicsJVM` - ✅
- `graphicsJS` - ✅
- `graphics_webgl` - ✅

### ❌ Compilation Errors

#### `graphics_lwjgl`
**Main Issue**: `Window` type not found, even though:
- `Window.class` exists in `modules/core/graphics/.jvm/target/scala-3.3.5/classes/seer/graphics/Window.class`
- `graphics_lwjgl` depends on `graphics.jvm`
- `Window` is in package `seer.graphics`
- Both `GLFWWindowImpl` and `LwjglOpenGLGraphicsModule` are in `seer.graphics` package

**Errors**:
1. `GLFWWindowImpl.scala:24` - `type Window is not a member of seer.graphics`
2. `LwjglOpenGLGraphicsModule.scala:32` - `type Window is not a member of seer.graphics`
3. Type errors with `getSize()` tuple access
4. Missing `glfwFreeCallbacks` import

**Possible Causes**:
- Scala 3 TASTy file issue
- Classpath resolution problem
- Package visibility issue
- Compilation order issue

**Files Affected**:
- `modules/backends/graphics-lwjgl/src/main/scala/GLFWWindowImpl.scala`
- `modules/backends/graphics-lwjgl/src/main/scala/LwjglOpenGLGraphicsModule.scala`

#### `examplesjs`
**Issues**:
- Old code using `onCreate` callback (doesn't exist in new API)
- Old code using `WebglGraphicsModule` (renamed to `WebGLGraphicsModule`)

**Files Affected**:
- `examplesjs/src/main/scala/agents/Flocking.scala`
- `examplesjs/src/main/scala/fluids/ReactionDiffusion.scala`
- Other example files using old API

## Next Steps

1. **Fix Window visibility issue** - Investigate why compiler can't find Window trait
2. **Fix getSize() tuple access** - Use proper tuple destructuring
3. **Fix glfwFreeCallbacks import** - Add proper import
4. **Update old example code** - Migrate to new API
5. **Run tests** - Verify functionality once compilation succeeds

## Investigation Needed

The `Window` trait exists and compiles, but isn't visible to `graphics_lwjgl`. This suggests:
- TASTy file issue
- Classpath problem  
- Package export issue
- Scala 3 visibility rules

Need to investigate Scala 3 package/trait visibility rules or try alternative approaches.
