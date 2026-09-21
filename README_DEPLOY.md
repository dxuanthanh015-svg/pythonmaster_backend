# Deploy lên Render - Hướng dẫn

## Các file đã tạo:
- ✅ `Dockerfile` - Build Docker image
- ✅ `render.yaml` - Cấu hình Render services
- ✅ `application-prod.properties` - Config production
- ✅ `.dockerignore` - Bỏ các file không cần thiết

## Bước 4: Push lên GitHub

```bash
# Thêm tất cả các file mới
git add .

# Commit
git commit -m "Add Dockerfile and render.yaml for Render deployment with MySQL"

# Push lên GitHub
git push origin main
```

## Bước 5: Deploy trên Render

### Cách 1: Dùng Blueprint (Khuyên dùng)
1. Đăng nhập [render.com](https://render.com)
2. Click "New +" → "New Blueprint Instance"
3. Connect GitHub repo của bạn
4. Render sẽ tự động đọc `render.yaml`
5. Review configuration:
   - Web Service: pythonmaster-api
   - Database: pythonmaster-db (MySQL Free)
6. Click "Create Blueprint Instance"

### Cách 2: Tạo thủ công
1. Đăng nhập [render.com](https://render.com)
2. Tạo MySQL Database:
   - "New +" → "PostgreSQL" → chọn "MySQL"
   - Name: pythonmaster-db
   - Database: pythonmaster
   - User: pythonmaster_user
   - Plan: Free
3. Tạo Web Service:
   - "New +" → "Web Service"
   - Connect GitHub repo
   - Runtime: Docker
   - Dockerfile path: `./Dockerfile`
   - Environment Variables:
     - `SPRING_DATASOURCE_URL`: lấy từ database
     - `SPRING_DATASOURCE_USERNAME`: lấy từ database
     - `SPRING_DATASOURCE_PASSWORD`: lấy từ database
     - `JWT_SECRET`: tự generate
     - `SPRING_PROFILES_ACTIVE`: prod

## Sau khi deploy:

### URL truy cập:
- API: `https://pythonmaster-api.onrender.com`
- Swagger UI: `https://pythonmaster-api.onrender.com/swagger-ui.html`

### Test kết nối:
```bash
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
- Web Service: Free (512 MB RAM, 0.1 CPU)
- MySQL Database: Free (1 GB storage)
- **Tổng: $0/tháng**

## Lưu ý:
1. **Sleep mode**: Free tier sleep sau 15 phút không hoạt động
2. **Awake time**: Lần truy cập đầu tiên mất ~30s
3. **Database**: MySQL free tier giới hạn 1 GB
4. **SSL**: Render tự động cung cấp HTTPS
5. **Logs**: Check Dashboard → pythonmaster-api → Logs
