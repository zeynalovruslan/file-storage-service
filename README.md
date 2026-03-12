# 📦 File Storage Service

Spring Boot əsaslı **File Storage Service** layihəsi.  
Servis faylların **upload, download, list və delete** əməliyyatlarını təmin edir.

Fayllar **MinIO (S3 compatible storage)** və ya **Local filesystem** üzərində saxlanılır amma
yanlız konfiqurasiya tənzimləmələrini əlavə etməklə kodu dəyişmədən başqa providerlardan da istifadə edə bilərsiniz.

Fayl metadatası **MySQL** verilənlər bazasında saxlanılır.

Layihə **Docker Compose** vasitəsilə bütün dependency-lərlə birlikdə **bir komanda ilə işə düşür**.

---

# 🚀 Texnologiyalar

- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- MinIO (S3 compatible object storage)
- Docker
- Docker Compose
- API Key Authentication
- Audit Logging

---

# 🏗 Sistem Arxitekturası


Client
│
▼
Spring Boot Application
│
├── MySQL (metadata, api keys)
│
└── Storage Provider
├── MinIO
└── Local Storage


Docker Compose aşağıdakı servisləri işə salır:

- **app** → Spring Boot API
- **db** → MySQL database
- **minio** → Object storage
- **minio-init** → bucket yaradılması

---

# 🔐 Authentication

Servis **API Key authentication** istifadə edir.

Bütün request-lərdə aşağıdakı header göndərilməlidir:


X-API-Key: your-api-key


---

# 👑 Secret API Key

Secret API key yalnız **API key idarə etmək üçün** istifadə olunur.

Secret key yalnız aşağıdakı endpointlər üçün keçərlidir:


/api-keys/**


Secret key **file endpointlərində istifadə edilə bilməz**.

---

# 📁 Storage Provider

Servis iki storage tipi dəstəkləyir.

## MinIO Storage

Fayllar MinIO bucket-də saxlanılır.


storage.provider=MINIO


---

## Local Storage

Fayllar lokal filesystem-də saxlanılır.


storage.provider=LOCAL


Local storage path:


storage.local.base-path=./data/files


---

# 📡 API Endpoints

## 🔑 API Key Management

### Yeni API Key yaratmaq


POST /api-keys


Body:


name


Response:


generated-api-key


---

## 📤 File Upload


POST /files


Request type:


multipart/form-data


Header:


X-API-Key


Example curl:


curl -X POST http://localhost:8080/files

-H "X-API-Key: YOUR_API_KEY"
-F "file=@test.pdf"


---

## 📥 File Download


GET /files/{id}


Example:


curl -X GET http://localhost:8080/files/1

-H "X-API-Key: YOUR_API_KEY"


---

## 📋 File List


GET /files


Example:


curl -X GET http://localhost:8080/files

-H "X-API-Key: YOUR_API_KEY"


---

## 🗑 File Delete


DELETE /files/{id}


Example:


curl -X DELETE http://localhost:8080/files/1

-H "X-API-Key: YOUR_API_KEY"


---

# 📊 Audit Logging

Sistem aşağıdakı əməliyyatları loglayır:

- File upload
- File download
- File delete
- API key creation
- Authentication failures

Audit məlumatları:

- action
- status
- ip address
- user agent
- api key id

---

# ⚙️ Layihəni işə salmaq

## 1️⃣ Repository klonla


git clone https://github.com/zeynalovruslan/file-storage-service


Repo daxilinə keç:


cd file-storage-service


---

## 3️⃣ Docker Compose ilə işə sal


docker compose up -d --build


---

# 🌐 Servislər

| Service | Port |
|-------|------|
| App | 8080 |
| MySQL | 3306 |
| MinIO | 9000 |
| MinIO Console | 9001 |

---

# 🧭 MinIO Console

Brauzerdə aç:


http://localhost:9001


Login üçün:


MINIO_ROOT_USER
MINIO_ROOT_PASSWORD


---

# 📂 Layihə Strukturu


src
├── controller
├── service
├── repository
├── entity
├── security
├── storage
└── audit


---

# 🐳 Docker Services

Docker Compose aşağıdakı servisləri işə salır:

- app
- mysql
- minio
- minio-init

`minio-init` konteyneri MinIO hazır olduqdan sonra **bucket avtomatik yaradır**.

---

# 🔧 Konfiqurasiya

Əsas konfiqurasiyalar `.env` və `application.properties` vasitəsilə edilir.

Misal:


DB_HOST=db
DB_PORT=3306

MINIO_ENDPOINT=http://minio:9000

MINIO_BUCKET=files

STORAGE_PROVIDER=MINIO





