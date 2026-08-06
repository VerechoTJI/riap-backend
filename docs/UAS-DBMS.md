# UAS 與 DBMS 模組說明

本文件說明使用者驗證子系統（UAS）與資料存取層（DBMS）的實作,並對齊團隊現行的
**Spring Boot + Spring Data JPA** 架構。

UAS 以 `com.riap.user` feature module 加入;DBMS 的需求（CRUD／交易／連線池／完整性）
由 Spring 基礎設施加上一支外鍵 schema 腳本滿足。可乾淨併入 `main`(零衝突),`mvn test` 全綠。

---

## 一、實作內容

### UAS(`com.riap.user`）
- **領域／持久層**:`UserAccountEntity`(UUID 主鍵、`login_identifier` 唯一)、
  `UserAccountRepository`(Spring Data JPA）。
- **驗證服務**:帳號註冊 + 登入驗證;密碼以 **BCrypt** 雜湊（透過 `spring-security-crypto`);
  針對 locked／disabled 狀態回傳對應結果,且不洩漏帳號是否存在。
- **JWT 登入 token**:`JwtService`(jjwt)——`/api/auth/login` 成功時回傳已簽章的 token。
- **角色權限控管（RBAC）**:`@RequireRole` 註解 + `AuthInterceptor`;未標註的端點維持開放,
  因此不影響 LMS／RCS 的控制器。其他子系統在自己的端點加上 `@RequireRole` 即可套用。
- **登出**:透過記憶體中的 `TokenBlacklist` 撤銷 token 的 jti。
- **REST 端點**:`/api/auth` 的 register、login、logout、me(另含一個示範用的 admin-only 端點)。

### DBMS
- **PostgreSQL profile**(`application-postgres.properties`)——專案要求的資料庫（DBMS-N-12);
  預設 profile 保持 H2,讓測試與本機快速啟動不需外部資料庫。
- **參照完整性**:外鍵 `listings.landlord_id → user_accounts.id`,透過 idempotent 的
  `schema-postgres.sql` 在 Hibernate 建表後補上(保留 UUID 主鍵,不更動 LMS 的 entity)。
- **連線池**:HikariCP 為 Spring Boot 自動配置的預設連線池。

---

## 二、需求對照

| 需求 | 實作位置 |
|---|---|
| UAS-F-01（註冊／登入／登出 + token）、UAS-F-03 | AuthenticationService、JwtService、AuthController |
| UAS-F-02 / UAS-F-04（角色權限） | `@RequireRole` + AuthInterceptor |
| UAS-N-09（BCrypt 加密） | PasswordEncoderConfig（BCrypt） |
| DBMS-F-02（CRUD） | Spring Data JPA;STD DBMS-TC01 |
| DBMS-F-03（外鍵完整性） | schema-postgres.sql;STD DBMS-TC02 |
| DBMS-N-11（孤兒資料防止） | 外鍵 RESTRICT;STD DBMS-TC03 |
| DBMS-N-03 / N-08（ACID 交易） | `@Transactional`;STD DBMS-TC04 |
| DBMS-N-05 / N-10（連線池） | HikariCP;STD DBMS-TC05 |
| DBMS-N-12（PostgreSQL） | postgres profile |

---

## 三、測試

- `mvn test` → **49 個測試全綠**(有設定 `DB_PASSWORD` 時,含對 PostgreSQL 的外鍵測試)。
- 未設定 `DB_PASSWORD` 時,僅限 PostgreSQL 的測試會**自動跳過**(BUILD SUCCESS)——
  不需要 Docker、也不需要 PostgreSQL,CI 與其他人的 `mvn test` 不會被影響。
- 已於 PostgreSQL 18 端到端驗證:啟動、自動建表(UUID 主鍵)、register／login(JWT),
  以及外鍵對「孤兒房源插入」與「刪除仍有房源的房東」的拒絕。

STD DBMS 測試案例對應:
- TC01 → `UserAccountRepositoryTest.createReadUpdateDelete`(H2）
- TC02 / TC03 → `DbmsForeignKeyPostgresTest`(真 PostgreSQL,DB_PASSWORD gating）
- TC04 / TC05 → `DbmsTransactionAndPoolTest`(H2)

---

## 四、在 PostgreSQL 上執行

```powershell
# 1. 建立資料庫（一次）
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "CREATE DATABASE riap;"

# 2. 設定密碼並以 postgres profile 啟動
$env:DB_PASSWORD = "<你的 postgres 密碼>"
mvn spring-boot:run "-Dspring-boot.run.profiles=postgres"

# 3. 驗證表已建立
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d riap -c "\dt"
```

> 不加 profile 即使用 H2(測試／快速啟動);加上 `postgres` profile 才連 PostgreSQL。
> 密碼透過 `DB_PASSWORD` 環境變數提供,**不寫進原始碼**。使用者或 DB 名稱不同時,
> 可另外設定 `DB_USERNAME` / `DB_NAME`。

---

## 五、API 端點(`/api/auth`)

| 方法 | 路徑 | 說明 | 權限 |
|---|---|---|---|
| POST | `/api/auth/register` | 註冊帳號,回傳 userId | 公開 |
| POST | `/api/auth/login` | 登入,成功回傳 `token` + role | 公開 |
| POST | `/api/auth/logout` | 登出(撤銷目前 token) | 需登入 |
| GET | `/api/auth/me` | 取得目前使用者 id / role | 需登入 |
| GET | `/api/auth/admin-area` | RBAC 示範端點 | 僅 ADMIN |

其他子系統若要做角色限制,在自己的 controller 方法加上 `@RequireRole(UserRole.XXX)` 即可
(空值代表「任何已登入者」)。

---

## 六、注意事項 / 後續

- Token 發放已完成;refresh token、以及把記憶體 `TokenBlacklist` 換成持久化儲存
  (Redis／DB)可作為後續工作。
- 外鍵在 **PostgreSQL** 層強制(H2 單元測試為 mock／slice 性質);若 CI 之後有 Docker,
  可再加 Testcontainers 的 PG 測試。
- `feat/rcs` 與 `feature/lss-listing-search` 仍使用舊的 `com.riap.pbi` 結構,
  併入 `main` 時需做類似的 Spring Boot 遷移(屬各自子系統 owner 的工作)。
