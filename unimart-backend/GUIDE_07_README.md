# Guide 07 — CRUD APIs (Listings, Reviews) + Postman Testing

This file covers **only Guide 07**. It assumes Guides 03–05 are already done (see the main
`README.md`). Everything Guide 07 asks you to write in code is already in this project; what's
left is Postman setup, seeding a bit of test data, and running the collection.

## ⚠️ A note on Guide 06

I don't have the content of Guide 06 (it wasn't uploaded), and Guide 07 assumes it's already done —
it uses `POST /auth/register`, `POST /auth/login`, and reviews reference a **completed order** that
Guide 07 itself doesn't create. To make Guide 07 actually runnable end-to-end, I built minimal,
reasonable stand-ins for what Guide 06 likely covers:

- **`auth` feature** (`auth/dto`, `auth/service/AuthService.java`, `auth/controller/AuthController.java`)
  — registration hashes the password with BCrypt and returns nothing sensitive; login verifies the
  password and mints a JWT whose `sub` claim is the user's university email (this matches Guide 07's
  code, which uses `authentication.getName()` directly as the email for ownership checks).
- **`order` feature** (`order/entity/Order.java`, `order/entity/OrderStatus.java`,
  `order/repository/OrderRepository.java`) — just enough to let a `Review` reference a real completed
  order. There's no order-creation endpoint yet (that's a full checkout/payment flow, out of scope
  here), so **`scripts/04_seed_review_test_data.sql`** inserts a completed order directly for testing.

**If your actual Guide 06 does things differently** (different DTO field names, a different JWT
subject claim, a real order-creation endpoint, etc.), tell me what it contains and I'll line this up
exactly instead of guessing. Everything below still works as-is in the meantime.

---

## What's new in this zip vs. the Guides 03–05 version

```
src/main/java/lk/ac/kln/unimart/
├── auth/
│   ├── controller/AuthController.java     POST /auth/register, POST /auth/login
│   ├── dto/RegisterRequest.java, LoginRequest.java, AuthResponse.java
│   └── service/AuthService.java
├── listing/
│   ├── controller/ListingController.java  full CRUD + search + nested reviews
│   ├── dto/ListingRequest.java, ListingResponse.java
│   ├── mapper/ListingMapper.java
│   └── service/ListingService.java, ListingSpecifications.java
├── review/
│   ├── controller/ReviewController.java   create/update/delete
│   ├── dto/ReviewCreateRequest.java, ReviewUpdateRequest.java, ReviewResponse.java
│   ├── entity/Review.java
│   ├── mapper/ReviewMapper.java
│   ├── repository/ReviewRepository.java
│   └── service/ReviewService.java
├── order/
│   ├── entity/Order.java, OrderStatus.java
│   └── repository/OrderRepository.java
└── common/exception/
    ├── ResourceNotFoundException.java     -> 404
    ├── ConflictException.java             -> 409
    └── ForbiddenException.java            -> 403

scripts/
└── 04_seed_review_test_data.sql           seeds a category + a completed order

docs/postman/
├── UniMart_API_v1.postman_collection.json     Auth / Listings / Reviews / Negative Tests
└── UniMart_Local.postman_environment.json     base_url, access_token, seeded test values
```

`SecurityConfig` and `CorsConfig` (already present from Guide 03/05) were updated to match Guide 07
Part F exactly: `POST /auth/**`, `GET /listings/**`, `GET /reviews/**`, and `/actuator/health` are
public; everything else requires a bearer token.

---

## Part A — Install and set up Postman

1. Install the **Postman desktop app** for your OS from postman.com/downloads.
2. Open Postman → **Import** → select both files from `docs/postman/`:
   - `UniMart_API_v1.postman_collection.json`
   - `UniMart_Local.postman_environment.json`
3. Top-right environment selector → choose **UniMart Local**.
4. Confirm `base_url` = `http://localhost:8080/api/v1` and `access_token` is currently empty —
   that's expected, the Login requests fill it in automatically.

The collection is already organized into the four folders Guide 07 asks for: **Auth**, **Listings**,
**Reviews**, **Negative Tests**. Collection-level auth is Bearer `{{access_token}}`; individual public
requests override this with "No Auth", matching Part A step 5.

---

## Part B — Start the backend and seed data

1. Make sure MySQL is running and Flyway has applied `V1__` and `V2__` (see the main README if not —
   `flyway_schema_history`, `orders`, `reviews`, etc. should already exist from Guides 04–05).
2. Start `UniMartApplication` from IntelliJ, or `mvn spring-boot:run`.
3. In Postman, run **Auth → Register seller** and **Auth → Register buyer** once each. These use the
   `seller_email` / `buyer_email` values from the environment (`seller@stu.kln.ac.lk` /
   `buyer@stu.kln.ac.lk` by default — edit them in the environment if your class uses a different
   university email domain).
4. Run **Auth → Login (seller)** — this stores the seller's token in `access_token` automatically.
5. Run **Listings → Create listing** — this stores the new listing's id in `listing_id` automatically.
6. Open MySQL Workbench and run **`scripts/04_seed_review_test_data.sql`**:
   - It seeds a `Textbooks` category if one doesn't already exist.
   - It shows you the `users` and `listings` tables so you can find the seller's and buyer's ids and
     the listing's id and price.
   - Edit the three placeholder values in the `INSERT INTO orders (...)` statement (listing id, buyer
     id, price) and run it — this creates a `COMPLETED` order to review.
   - Copy the resulting order id into Postman's `completed_order_id` environment variable.

---

## Part C — Run the happy-path requests

Run these folders top to bottom (order matters — later requests depend on variables set by earlier
ones):

1. **Auth** — Register seller, Register buyer, Login (seller).
2. **Listings** — Create listing, Get listing by id, List listings, Update listing, *(leave "Archive
   listing" until last, since Guide 07 wants Reviews tested first)*.
3. **Reviews** — Login (buyer) *(switches `access_token` to the buyer)*, Create review, Update review,
   List reviews for listing, Delete review.
4. Go back to **Listings → Archive listing** last, once you're done needing that listing for review
   tests.

Each request already has `pm.test(...)` assertions matching Guide 07 Part G, and successful runs
populate `listing_id` / `review_id` / `access_token` for you — no manual copy-pasting needed except the
one seeded `completed_order_id`.

---

## Part D — Run the negative tests (Part H)

The **Negative Tests** folder covers all nine cases from Guide 07's table:

| Request in collection | Expected |
|---|---|
| Create listing without token | 401 |
| Create listing with blank title | 400 + `fieldErrors.title` |
| Update another seller's listing | 403 |
| Get archived/non-existent listing | 404 |
| Review a non-completed order | 409 |
| Review another buyer's order | 403 |
| Create a second review for the same order | 409 |
| Rating outside 1–5 | 400 + `fieldErrors.rating` |
| Expired/malformed JWT | 401 |

Two of these need a specific state to actually trigger:

- **"Update another seller's listing"** — run this *while logged in as the buyer* (right after
  **Reviews → Login (buyer)**), targeting the seller's `listing_id`.
- **"Review a non-completed order"** — run this *before* you seed the completed order in Part B, or
  seed a second, still-`POSTED` order and point `completed_order_id` at that one temporarily.
- **"Review another buyer's order"** — run this while logged in as a *third* user (register one more
  account) so the caller genuinely isn't the order's buyer.

---

## Part E — Collection runner evidence (Part I)

1. Postman → collection **⋮** menu → **Run collection**.
2. Select the **UniMart Local** environment, keep the folder order as-is, and run.
3. Confirm all requests pass. Anything that legitimately depends on manual state (the three cases
   above) — run those individually right before the full run, or adjust the seeded data to match.
4. **Export → Collection v2.1**, with "Export as: Collection v2.1 (no environment values)" so no local
   secrets leak into the exported file. Save it under `docs/postman/` (overwriting the one already
   there is fine — it's already secret-free) or a separate documentation repo, as your lecturer
   specifies.
5. Take a screenshot of the Collection Runner's pass/fail summary (or export the run's JSON results)
   as lab evidence. **Redact/crop out any visible token or password values** before submitting.

---

## Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| Every request returns 401, even public ones | Backend not running, or wrong `base_url` port | Confirm `mvn spring-boot:run` is up and `SERVER_PORT` matches `base_url`. |
| Register/Login returns 401 or 403 instead of 200/201 | `SecurityConfig` matcher typo, or CORS unrelated (Postman ignores CORS) | Check `/api/v1/auth/**` is `permitAll()` for POST in `SecurityConfig`. |
| Create review always 404 "Order not found" | `completed_order_id` wasn't updated after seeding | Re-run `04_seed_review_test_data.sql` step 4's `SELECT`, copy the real id into the environment. |
| Create review always 403 "Only the buyer can review" | Logged in as the seller, not the buyer | Run **Reviews → Login (buyer)** before **Create review**. |
| 500 error instead of 400 on validation | A `@Valid` annotation is missing on a controller parameter | Already present on all write endpoints in this project — check you didn't remove `@Valid`. |
| Postman can't reach `localhost` | Backend crashed on startup | Check the IntelliJ console — usually a Guides 04/05 environment-variable or MySQL connectivity issue. |

---

## Completion checklist

- [ ] Postman workspace **UniMart Lab** created, collection **UniMart API v1** imported, environment **UniMart Local** selected.
- [ ] Seller and buyer registered; seller logged in and `access_token` populated automatically.
- [ ] A listing created via Postman (`listing_id` populated automatically).
- [ ] `scripts/04_seed_review_test_data.sql` run; `completed_order_id` set to the real seeded order id.
- [ ] Listing CRUD (create/get/list/update/archive) all pass with DTO validation and ownership enforced.
- [ ] Review CRUD enforces "completed order only" and "one review per order".
- [ ] All 9 negative tests in the **Negative Tests** folder pass.
- [ ] Collection exported without secret values; run evidence captured with tokens/passwords redacted.
