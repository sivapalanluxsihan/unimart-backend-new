# UniMart Backend — Guides 03, 04, 05

This project is the finished result of:

- **Guide 03** — Spring Boot MVC project in IntelliJ (package-by-feature structure, health endpoint, security scaffolding).
- **Guide 04** — MySQL Server + Workbench setup (schema + least-privilege account).
- **Guide 05** — Connecting Spring Boot to MySQL via environment variables, Flyway migrations, JWT config.

Everything that can be generated ahead of time (code, config, SQL scripts) is included. A few steps
**cannot** be zipped and must be done by hand on your machine — they're listed below in the order you'll hit them.

---

## 0. What's in this zip

```
unimart-backend/
├── pom.xml                                Maven project (Spring Boot 4.1.x, Java 21)
├── .env.example                           Names + safe placeholders (safe to commit)
├── .gitignore                             Keeps real secrets out of Git
├── README.md                              This file
├── scripts/
│   ├── 01_create_schema_and_user.sql      Guide 04 §3 — run in Workbench as admin
│   ├── 02_verify_connection.sql           Guide 04 §4 — run as unimart_app
│   └── 03_backup_and_restore.sh           Guide 04 §7 — mysqldump helpers
└── src/
    ├── main/java/lk/ac/kln/unimart/
    │   ├── UniMartApplication.java
    │   ├── common/api/                    PublicController (ping), ApiError
    │   ├── common/exception/              GlobalExceptionHandler
    │   ├── config/                        CorsConfig, SecurityConfig
    │   ├── security/                      JwtService, CurrentUser
    │   ├── auth/entity, repository/       User entity + repository (Category/Role too)
    │   ├── listing/entity, repository/    Listing, Category, ListingStatus
    │   ├── review/, order/, notification/ Empty feature folders, ready for later labs
    ├── main/resources/
    │   ├── application.yml                Base config (env-driven, from Guide 05 §3)
    │   ├── application-local.yml          Verbose logging for local dev
    │   ├── application-prod.yml           Quiet logging, locked-down actuator
    │   └── db/migration/
    │       ├── V1__create_core_tables.sql      users, categories, listings (Guide 05 §4)
    │       └── V2__create_transaction_tables.sql  images, messaging, orders, payments,
    │                                              reviews, notifications (rest of Guide 01's catalogue)
    └── test/java/lk/ac/kln/unimart/
        └── UniMartApplicationTests.java   Context-load smoke test
```

The `auth` and `listing` packages have real entities/repositories wired to the migrations, since Guide 05
gives a worked `Listing` example and the tables already exist. `review`, `order`, and `notification` are left
as empty, correctly-named folders (with `.gitkeep` placeholders) — that's deliberate: those guides only ask
you to establish the structure, not implement full CRUD yet. Add entities/controllers there in your next lab.

---

## 1. Manual action — install prerequisites

These can't be packaged into a zip; install them yourself first:

1. **JDK 21** (e.g. Temurin 21 or Oracle JDK 21). Confirm with `java -version`.
2. **IntelliJ IDEA** (Community or Ultimate) with the Maven plugin (bundled by default).
3. **MySQL Server 8.4 LTS** and **MySQL Workbench** — see [MySQL setup](#2-manual-action--mysql-server--workbench-guide-04) below.
4. Optional but recommended: **Git**, so you can commit `.env.example` (not your real secrets).

---

## 2. Manual action — MySQL Server & Workbench (Guide 04)

1. Install MySQL Server 8.4 LTS and MySQL Workbench (Windows: MySQL Installer, Custom/Developer Default
   setup, keep port 3306, set a strong root password stored in a password manager. macOS/Linux: official
   packages, or Docker Desktop for the server while Workbench runs natively).
2. Open MySQL Workbench, connect as an **administrative** account (e.g. root), open a SQL tab, and run:
   ```
   scripts/01_create_schema_and_user.sql
   ```
   **Edit the password placeholder first** — replace `replace-with-a-strong-local-password` with a real
   local-only password before running it. This creates the `unimart` schema (utf8mb4) and a least-privilege
   `unimart_app` account. The Spring Boot app must **never** connect as root.
3. Create a new Workbench connection: name `UniMart Local`, hostname `127.0.0.1`, port `3306`, username
   `unimart_app`. Test the connection, then run `scripts/02_verify_connection.sql` to confirm access
   (it will show no application tables yet — those appear after Flyway runs in the next section).
4. Keep the ER model / `.mwb` file from Guide 01 nearby; after Flyway creates the schema you can use
   **Database → Reverse Engineer** in Workbench to compare the live schema against your model.

---

## 3. Manual action — open the project in IntelliJ (Guide 03)

1. **File → Open** and select the `unimart-backend` folder (the one containing `pom.xml`). IntelliJ detects
   it as a Maven project and downloads dependencies automatically — no network access was available while
   generating this zip, so this first import is where dependency resolution actually happens.
2. Confirm the Project SDK is Java 21: **File → Project Structure → Project → SDK**.
3. (Optional but matches the guide) **Right-click `pom.xml` → Maven → Add as Maven Project**, then use
   **View → Tool Windows → Maven** to generate the Maven wrapper if you want `./mvnw` available:
   right-click the project root → **Add Framework Support** isn't it — instead run, from a terminal with
   system Maven installed once:
   ```
   mvn -N wrapper:wrapper -Dmaven=3.9.9
   ```
   This creates `mvnw`, `mvnw.cmd`, and `.mvn/wrapper/`. If you'd rather just use IntelliJ's bundled Maven
   or a system `mvn`, you can skip this — every command below also works as `mvn ...`.
4. Sanity check before touching the database:
   ```
   mvn -v
   mvn clean compile
   ```

---

## 4. Manual action — generate a JWT secret (Guide 05 §2)

Run once and keep the output somewhere safe (a password manager, not a text file in the repo):

```
openssl rand -base64 48
```

No `openssl`? IntelliJ's bundled terminal on Windows may not have it — use Git Bash, WSL, or generate
48 random bytes another way and Base64-encode them. The important part is: **decoded length must be
at least 32 bytes** (the app checks this at startup and fails fast if it isn't).

---

## 5. Manual action — set environment variables in IntelliJ (Guide 05 §7)

The app reads all secrets/config from environment variables — nothing is hard-coded in `application.yml`.

1. **Run → Edit Configurations → + → Application** (or let IntelliJ create one when you first run
   `UniMartApplication`).
2. Main class: `lk.ac.kln.unimart.UniMartApplication`.
3. In **Environment variables**, add (click the folder icon for the multi-line editor):
   ```
   SPRING_PROFILES_ACTIVE=local
   DB_URL=jdbc:mysql://127.0.0.1:3306/unimart
   DB_USERNAME=unimart_app
   DB_PASSWORD=<the password you set in scripts/01_create_schema_and_user.sql>
   JWT_SECRET=<the output of openssl rand -base64 48>
   JWT_ACCESS_MINUTES=15
   APP_ALLOWED_ORIGINS=http://localhost:5173
   SERVER_PORT=8080
   ```
4. Apply and close. **Do not** paste these into source files, VM options, screenshots, or commit them —
   `.gitignore` already excludes `.env*` (except `.env.example`) for this reason.

---

## 6. Run and verify (Guide 03 §5, Guide 05 §8)

1. Run `UniMartApplication` from IntelliJ (or `mvn spring-boot:run`).
2. Watch the console for:
   - Flyway applying `V1__create_core_tables.sql` and `V2__create_transaction_tables.sql`.
   - HikariCP pool startup with no connection errors.
   - Tomcat starting on port 8080.
3. Check the public ping endpoint:
   ```
   curl http://localhost:8080/api/v1/public/ping
   ```
   Expect a 200 with `"status":"UP"`.
4. Check actuator health:
   ```
   curl http://localhost:8080/actuator/health
   ```
   Expect `{"status":"UP"}`.
5. Back in Workbench, refresh the `unimart` schema (or re-run `scripts/02_verify_connection.sql`) and
   confirm `flyway_schema_history`, `users`, `categories`, `listings`, `listing_images`, `conversations`,
   `messages`, `orders`, `payments`, `reviews`, and `notifications` all exist.
6. Run the test suite (needs the same environment variables as step 5, since the context test talks to the
   real database):
   ```
   mvn clean test
   ```

---

## 7. Connecting the frontend (Guides 01–02)

Your frontend's `.env.local` should already point at:
```
VITE_API_BASE_URL=http://localhost:8080/api/v1
```
and `APP_ALLOWED_ORIGINS` on the backend should list the frontend's exact origin
(`http://localhost:5173` by default from `npm run dev`). CORS will reject the request otherwise —
there's no wildcard fallback, by design (Guide 03 §7 / Guide 05 troubleshooting table).

---

## 8. Troubleshooting quick reference

| Symptom | Likely cause | Fix |
|---|---|---|
| `Access denied for user` | Wrong password/host/grants | Re-test the same `unimart_app` credentials in Workbench; re-run `01_create_schema_and_user.sql` if needed. |
| `Unknown database 'unimart'` | Schema not created, or `DB_URL` typo | Re-run `01_create_schema_and_user.sql`; check the URL/case exactly. |
| `Communications link failure` | MySQL service stopped, wrong port/firewall | Start the MySQL service; confirm `127.0.0.1:3306` is reachable. |
| `JWT secret must be at least 32 bytes` | `JWT_SECRET` missing/too short | Regenerate with `openssl rand -base64 48` and update the run configuration. |
| `Flyway validation failed` | A migration file was edited after it already ran | Restore the original migration content; make new changes in a new `V3__...sql` file instead. |
| CORS blocked in the browser | `APP_ALLOWED_ORIGINS` doesn't exactly match the frontend origin | Match protocol, host, and port exactly (`http://localhost:5173`, not `127.0.0.1`). |
| `401` on `/api/v1/public/ping` | Shouldn't happen — that path is `permitAll()` | Confirm you didn't rename the package/path; check `SecurityConfig`. |
| Port 8080 already in use | Another process is bound to it | Stop it, or set `SERVER_PORT=8081` in the run configuration. |

---

## 9. Completion checklist (Guides 03–05 combined)

- [ ] MySQL server running; `unimart` schema created with `utf8mb4`.
- [ ] `unimart_app` least-privilege account created and used (never root) in the app config.
- [ ] Project opens in IntelliJ, Java 21 SDK selected, `mvn clean compile` succeeds.
- [ ] Public ping endpoint returns HTTP 200.
- [ ] `JWT_SECRET` generated, ≥32 bytes decoded, set only via environment variables.
- [ ] `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` / `APP_ALLOWED_ORIGINS` set in the IntelliJ run configuration.
- [ ] Flyway applies both migrations; `flyway_schema_history` plus all application tables exist in Workbench.
- [ ] `/actuator/health` reports `UP`.
- [ ] `mvn clean test` passes.
- [ ] CORS origin is exact, not a wildcard.
- [ ] No secrets committed to Git (`.gitignore` covers `.env*`, `application-secrets.yml`, `*.pem`, `*.key`).

---

## Guide 07 (CRUD APIs + Postman)

Guide 07's Listing/Review CRUD, auth endpoints, Postman collection, and seed scripts have since been
added to this same project. See **`GUIDE_07_README.md`** for that walkthrough — it covers Postman
setup, seeding a completed order, running the collection, and the negative-test evidence Guide 07
asks for.
