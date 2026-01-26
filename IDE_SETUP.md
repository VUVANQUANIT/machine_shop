# 🛠️ IDE Configuration Guide

## ⚠️ Vấn đề: Timezone Error khi chạy từ IDE

Khi bạn chạy ứng dụng từ IDE (Shift+F10 hoặc Debug), bạn sẽ gặp lỗi:
```
FATAL: invalid value for parameter "TimeZone": "Asia/Saigon"
```

### 🔍 Nguyên nhân:

- ✅ **Maven command line**: JVM arguments từ `pom.xml` được áp dụng → Chạy OK
- ❌ **IDE Run/Debug**: IDE chạy trực tiếp main class, không qua Maven plugin → Thiếu JVM arguments → Lỗi

---

## ✅ Giải pháp đã được cấu hình

Tôi đã tạo sẵn cấu hình cho cả **IntelliJ IDEA** và **VS Code**:

### 📁 Files đã tạo:
- `.idea/runConfigurations/MachinesshopApplication.xml` - IntelliJ IDEA
- `.vscode/launch.json` - VS Code

---

## 🎯 Hướng dẫn sử dụng

### **IntelliJ IDEA**

#### Option 1: Dùng Run Configuration có sẵn (Khuyến nghị)
1. Mở project trong IntelliJ IDEA
2. Ở góc trên bên phải, chọn **"MachinesshopApplication"** từ dropdown
3. Click **Run** (▶️) hoặc **Debug** (🐛)
4. ✅ Done! App sẽ chạy với `-Duser.timezone=UTC`

#### Option 2: Tạo Run Configuration mới
1. Click **Run** → **Edit Configurations...**
2. Click **+** → **Spring Boot**
3. Điền thông tin:
   - **Name**: `MachinesshopApplication`
   - **Main class**: `com.example.machinesshop.MachinesshopApplication`
   - **VM options**: `-Duser.timezone=UTC`
   - **Environment variables**: 
     - `JAVA_TOOL_OPTIONS=-Duser.timezone=UTC`
     - `SPRING_PROFILES_ACTIVE=dev`
4. Click **OK** và chạy

#### Option 3: Set Global VM Options (Áp dụng cho tất cả projects)
1. **File** → **Settings** (Ctrl+Alt+S)
2. **Build, Execution, Deployment** → **Compiler**
3. **Shared build process VM options**: Thêm `-Duser.timezone=UTC`
4. Click **OK**

---

### **VS Code**

#### Option 1: Dùng Launch Configuration có sẵn (Khuyến nghị)
1. Mở project trong VS Code
2. Mở **Run and Debug** panel (Ctrl+Shift+D)
3. Chọn **"Spring Boot: MachinesshopApplication"** từ dropdown
4. Click **▶️ Start Debugging** (F5) hoặc **▶️ Run** (Ctrl+F5)
5. ✅ Done!

#### Option 2: Tạo Launch Configuration mới
1. Mở **Run and Debug** panel (Ctrl+Shift+D)
2. Click **create a launch.json file**
3. Chọn **Java**
4. Copy nội dung từ `.vscode/launch.json` đã có sẵn

---

### **Eclipse / Spring Tool Suite (STS)**

1. **Run** → **Run Configurations...**
2. Tạo **Java Application** mới
3. **Main class**: `com.example.machinesshop.MachinesshopApplication`
4. Tab **Arguments** → **VM arguments**: `-Duser.timezone=UTC`
5. Tab **Environment** → Add:
   - `JAVA_TOOL_OPTIONS=-Duser.timezone=UTC`
   - `SPRING_PROFILES_ACTIVE=dev`
6. Click **Run**

---

## 🔧 Alternative: Set Environment Variable Global

Nếu bạn không muốn config từng IDE, có thể set environment variable global:

### Windows (PowerShell - Admin)
```powershell
# Set user environment variable
[System.Environment]::SetEnvironmentVariable("JAVA_TOOL_OPTIONS", "-Duser.timezone=UTC", "User")

# Restart IDE sau khi set
```

### Windows (CMD - Admin)
```cmd
setx JAVA_TOOL_OPTIONS "-Duser.timezone=UTC"
```

### Linux/Mac
```bash
# Add to ~/.bashrc or ~/.zshrc
export JAVA_TOOL_OPTIONS="-Duser.timezone=UTC"

# Reload
source ~/.bashrc  # or source ~/.zshrc
```

**Lưu ý**: Sau khi set, phải **restart IDE** mới có hiệu lực!

---

## ✅ Verify Configuration

Sau khi chạy từ IDE, check log để confirm:

```
✅ Good: "Started MachinesshopApplication in X seconds"
❌ Bad: "FATAL: invalid value for parameter "TimeZone": "Asia/Saigon""
```

Hoặc test API:
```bash
curl http://localhost:8080/api/health
```

---

## 🎯 Best Practice

**Khuyến nghị**: 
- ✅ Dùng Run Configuration có sẵn (đã được tạo)
- ✅ Hoặc set `JAVA_TOOL_OPTIONS` global
- ✅ Đảm bảo tất cả developers trong team đều có config này

**Không khuyến nghị**:
- ❌ Chỉ chạy từ command line (không tiện cho debug)
- ❌ Set timezone trong code (không flexible)

---

## 🐛 Troubleshooting

### Vẫn lỗi sau khi config?

1. **Restart IDE** sau khi thay đổi config
2. **Invalidate Caches** (IntelliJ): File → Invalidate Caches / Restart
3. **Clean & Rebuild**: 
   ```bash
   .\mvnw.cmd clean
   ```
4. **Check VM options** trong Run Configuration có đúng không
5. **Check environment variables** trong Run Configuration

### IDE không load config file?

- **IntelliJ**: Đảm bảo `.idea/runConfigurations/` không bị gitignore
- **VS Code**: Đảm bảo `.vscode/launch.json` tồn tại

---

## 📝 Summary

| Method | Pros | Cons |
|--------|------|------|
| **Run Configuration** | ✅ IDE-specific, dễ quản lý | ⚠️ Phải config từng IDE |
| **Global Env Variable** | ✅ Áp dụng cho tất cả | ⚠️ Ảnh hưởng toàn hệ thống |
| **Command Line Only** | ✅ Đơn giản | ❌ Không tiện debug |

**Khuyến nghị**: Dùng **Run Configuration** (đã được tạo sẵn) ✅

---

## 🎉 Done!

Bây giờ bạn có thể:
- ✅ Chạy từ IDE (Shift+F10) - **Không còn lỗi!**
- ✅ Debug từ IDE (F5) - **Hoạt động hoàn hảo!**
- ✅ Chạy từ command line - **Vẫn OK như cũ!**

**Happy Coding!** 🚀
