# Deploy lên Railway - Hướng dẫn

## Các file đã tạo:
- ✅ `Dockerfile` - Build Docker image
- ✅ `application-prod.properties` - Config production
- ✅ `.dockerignore` - Bỏ các file không cần thiết

## Bước 4: Push lên GitHub

```bash
# Thêm tất cả các file mới
git add .

# Commit
git commit -m "Add Dockerfile for Railway deployment with MySQL"

# Push lên GitHub
git push origin main

Bước 5: Deploy trên Railway
Cách 1: Deploy qua GitHub Repository (Khuyên dùng)
Đăng nhập railway.app

Click "New Project" → Chọn "Deploy from GitHub repo" và chọn repository của bạn.

Thêm MySQL Database vào cùng Project:

Trong giao diện Project, click "+ New" → chọn "Database" → chọn "Add MySQL".

Cấu hình biến môi trường (Environment Variables):

Click vào thẻ Web Service của bạn → chuyển sang tab "Variables".

Thêm các biến môi trường sau (trỏ trực tiếp từ MySQL của Railway):

SPRING_DATASOURCE_URL: jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useSSL=false&allowPublicKeyRetrieval=true

SPRING_DATASOURCE_USERNAME: ${{MySQL.MYSQLUSER}}

SPRING_DATASOURCE_PASSWORD: ${{MySQL.MYSQLPASSWORD}}

JWT_SECRET: tự generate

SPRING_PROFILES_ACTIVE: prod

Tạo Domain công khai:

Vào tab "Settings" của Web Service → Tìm mục "Networking" / "Public Networking".

Click "Generate Domain" để nhận URL truy cập public (dạng .up.railway.app).

Sau khi deploy:
URL truy cập:
API: https://pythonmaster-api.up.railway.app

Swagger UI: https://pythonmaster-api.up.railway.app/swagger-ui.html
### Test kết nối:
# Test health
curl [https://pythonmaster-api.up.railway.app/swagger-ui.html](https://pythonmaster-api.up.railway.app/swagger-ui.html)

# Test login
curl -X POST [https://pythonmaster-api.up.railway.app/api/v1/auth/login](https://pythonmaster-api.up.railway.app/api/v1/auth/login) \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
# Test health
curl https://pythonmaster-api.onrender.com/swagger-ui.html

# Test login
curl -X POST https://pythonmaster-api.onrender.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Import Excel data:
- Sau khi deploy thành công, database sẽ được tạo mới
- Admin mặc định sẽ được tạo: username=admin, password=admin123
- Bạn cần import lại Excel data qua API hoặc upload file

## Chi phí:
- Railway tặng $5 free trial credits/tháng (hoặc $5/tháng gói Hobby).

- Đủ tài nguyên cho 1 Web Service + 1 MySQL Database hoạt động mượt mà.

- Tổng: ~$0/tháng (trong hạn mức credits tặng kèm).

## Lưu ý:
- Không Sleep: Railway không ngủ (no sleep) sau 15 phút ngưng hoạt động như Render. Server chạy liên tục 24/7.

- Khởi động tức thì: Không mất ~30s chờ server tỉnh dậy ở lần request đầu tiên.

- Domain custom: Phải bấm Generate Domain trong tab Settings để cấp domain công khai .up.railway.app.

- SSL: Railway tự động cung cấp chứng chỉ HTTPS cho mọi domain public.

- Logs: Check Dashboard → Chọn Web Service → Xem log thời gian thực tại tab Deployments / Logs.
