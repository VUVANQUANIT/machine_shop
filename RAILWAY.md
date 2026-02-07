# Deploy lên Railway

## Cách hoạt động

- **Push code lên `main`** → Railway **tự build lại và deploy** (chỉ khi bạn đã kết nối repo GitHub với Railway).
- Lần đầu bạn phải **tạo project trên Railway** và **kết nối repo**, sau đó mỗi lần push `main` sẽ tự chạy.

## Bước 1: Tạo project trên Railway

1. Vào [railway.app](https://railway.app), đăng nhập (GitHub).
2. **New Project** → **Deploy from GitHub repo**.
3. Chọn repo **machinesshop** (hoặc repo chứa code backend).
4. Railway sẽ phát hiện **Dockerfile** và dùng nó để build.

## Bước 2: Cấu hình biến môi trường

Trong project Railway → chọn service (backend) → tab **Variables** → thêm:

| Biến | Giá trị | Ghi chú |
|------|--------|--------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Bắt buộc |
| `DATABASE_URL` | `jdbc:postgresql://...?sslmode=require` | URL Neon (JDBC) |
| `DATABASE_USERNAME` | `neondb_owner` | User DB |
| `DATABASE_PASSWORD` | `...` | Mật khẩu DB |
| `JWT_SECRET` | Chuỗi bí mật mạnh (64+ ký tự) | Dùng riêng cho production |

**Lưu ý:** Railway tự set biến `PORT`. Dockerfile đã dùng `--server.port=${PORT}` nên không cần set `SERVER_PORT`.

## Bước 3: Deploy

- **Lần đầu:** Sau khi thêm Variables, Railway sẽ tự build và deploy.
- **Sau này:** Mỗi khi bạn **push lên branch `main`**, Railway sẽ:
  1. Build lại image từ Dockerfile
  2. Deploy bản mới
  3. App chạy với env đã cấu hình

## Bước 4: Lấy URL public

- Railway → service → **Settings** → **Networking** → **Generate Domain**.
- Bạn sẽ có URL dạng: `https://machinesshop-production-xxxx.up.railway.app`.
- Dùng URL này làm **API URL** cho frontend (trong `environment.prod.ts`).
- Cấu hình **CORS** ở backend cho phép origin là URL frontend của bạn.

## Tóm tắt

| Câu hỏi | Trả lời |
|--------|--------|
| Đẩy Dockerfile lên main có tự chạy không? | **Có**, nếu repo đã được kết nối với Railway. Mỗi push `main` → build + deploy. |
| Cần làm gì trước? | Tạo project Railway, connect GitHub repo, thêm Variables (DB, JWT, profile). |
| Database? | Dùng Neon (hoặc Postgres do Railway cung cấp), điền `DATABASE_*` vào Variables. |
