# MoMA — API Documentation

| Field | Value |
|---|---|
| API style | HTTPS REST (Supabase Auth / PostgREST / Storage) + RPC-style Edge Functions |
| Base host | `https://<PROJECT>.supabase.co` |
| Version | v1 (design baseline) |
| Machine-readable spec | `moma_openapi.yaml` (OpenAPI 3.1.0) |
| Related documents | SRS, SAD, DDD |

---

## 1. Overview

### 1.1 Base URLs

```text
Supabase Auth:     https://<PROJECT>.supabase.co/auth/v1
REST data:         https://<PROJECT>.supabase.co/rest/v1
Edge Functions:    https://<PROJECT>.supabase.co/functions/v1
Storage:           https://<PROJECT>.supabase.co/storage/v1
```

### 1.2 Headers

Authenticated calls:

```http
Authorization: Bearer <access_token>
apikey: <SUPABASE_ANON_PUBLIC_KEY>
Content-Type: application/json
```

- The **anon/public key is safe to ship in the Android app**. Row Level Security (RLS) is the security boundary.
- The **service-role key and Stripe secret keys must never appear in client code**; they exist only in Edge Function environment variables.
- All requests use HTTPS. Production builds should apply certificate pinning.

### 1.3 Authentication

| Method | Endpoint | Notes |
|---|---|---|
| Email/password register | `POST /auth/v1/signup` | Creates Auth user; app then creates `users` profile row |
| Login | `POST /auth/v1/token?grant_type=password` | Returns `access_token`, `refresh_token`, `expires_in` |
| Social login | Supabase OAuth flow | Google / Facebook providers configured in Supabase |
| Password reset | `POST /auth/v1/recover` | Always returns neutral success |
| Current user | `GET /auth/v1/user` | Requires Bearer token |
| Refresh | `POST /auth/v1/token?grant_type=refresh_token` | Uses refresh token |

Tokens are stored in Android Keystore-backed storage and cleared on logout. A `401` response means the client should refresh or re-authenticate.

### 1.4 Conventions

- Money fields are integers in **minor units** (cents), currency `MYR`.
- Timestamps are UTC ISO 8601 strings.
- IDs are UUIDs, except Stripe IDs (`pi_...`, `pm_...`).
- PostgREST filters use `?column=eq.value`, `gte`, `lte`, `in.(...)`, `order`, `select`.
- Write replays from the offline outbox include `Idempotency-Key`.
- Date ranges use `from` / `to` ISO date parameters.

---

## 2. Endpoint catalogue

### 2.1 Auth (public / Bearer)

| Method | Path | Purpose | Auth |
|---|---|---|---|
| POST | `/auth/v1/signup` | Register with email/password | Public |
| POST | `/auth/v1/token?grant_type=password` | Login | Public |
| POST | `/auth/v1/token?grant_type=refresh_token` | Refresh session | Public |
| POST | `/auth/v1/recover` | Forgot password | Public |
| GET | `/auth/v1/user` | Current user | Bearer |

### 2.2 Profiles and catalogue

| Method | Path | Purpose | Auth |
|---|---|---|---|
| GET | `/rest/v1/users?id=eq.<uid>` | Own profile | Bearer + RLS |
| PATCH | `/rest/v1/users?id=eq.<uid>` | Update own profile | Bearer + RLS |
| GET | `/rest/v1/instructor_profiles` | Browse instructor portfolios | Bearer/RLS |
| PATCH | `/rest/v1/instructor_profiles?user_id=eq.<uid>` | Edit own portfolio | Instructor + RLS |
| GET | `/rest/v1/martial_art_styles` | Style catalogue | Public |
| GET | `/rest/v1/credentials?user_id=eq.<uid>` | Own credential states | Bearer + RLS |
| POST | `/rest/v1/credentials` | Upload credential metadata | Bearer + RLS |

### 2.3 Gym program (first stakeholder)

| Method | Path | Purpose | Auth |
|---|---|---|---|
| GET | `/rest/v1/gyms?id=eq.<gymId>` | Gym details | Owner/RLS |
| GET | `/rest/v1/gym_programs?gym_id=eq.<gymId>` | Weekly curriculum | Owner/RLS |
| POST | `/rest/v1/gym_programs` | Publish program day | Owner/RLS |
| GET | `/rest/v1/coach_requirements?program_id=eq.<id>` | Open coach requirements | Owner/RLS |
| POST | `/rest/v1/coach_requirements` | Post requirement | Owner/RLS |

### 2.4 Listings, search and matching

| Method | Path | Purpose | Auth |
|---|---|---|---|
| GET | `/rest/v1/lesson_listings` | Filter by style/city/level/ruleset | Public/RLS |
| POST | `/rest/v1/lesson_listings` | Create lesson listing | Instructor + RLS |
| PATCH | `/rest/v1/lesson_listings?id=eq.<id>` | Edit listing | Owner + RLS |
| GET | `/rest/v1/availability_slots?listing_id=eq.<id>&starts_at=gte.<ts>` | Slots for listing | Public/RLS |
| POST | `/functions/v1/search` | Search and autocomplete | Bearer |
| POST | `/functions/v1/match-instructors` | Credential-gated ranked matches | Bearer |

### 2.5 Booking and payment

| Method | Path | Purpose | Auth |
|---|---|---|---|
| POST | `/functions/v1/create-booking` | Hold slot + create PaymentIntent | Student/gym member |
| POST | `/functions/v1/confirm-booking` | Confirm after verified payment | Server / webhook |
| POST | `/functions/v1/cancel-booking` | Cancel with refund policy | Involved user |
| GET | `/rest/v1/bookings?student_id=eq.<uid>` | Student bookings | Student/RLS |
| GET | `/rest/v1/bookings?instructor_id=eq.<uid>` | Instructor bookings | Instructor/RLS |
| GET | `/rest/v1/payments?booking_id=eq.<id>` | Payment status | Involved user/RLS |
| GET | `/rest/v1/payouts?instructor_id=eq.<uid>` | Payout history | Instructor/admin |

### 2.6 Trust, support and community

| Method | Path | Purpose | Auth |
|---|---|---|---|
| POST | `/rest/v1/reviews` | Review for completed booking | Student + RLS |
| GET | `/rest/v1/reviews?instructor_id=eq.<id>` | Instructor reviews | Public/RLS |
| POST | `/rest/v1/reports` | Report user/content | Bearer + RLS |
| POST | `/rest/v1/disputes` | Raise booking dispute | Involved user |
| GET | `/rest/v1/disputes?raised_by=eq.<uid>` | My disputes | Bearer + RLS |
| POST | `/rest/v1/support_tickets` | Submit support ticket | Bearer/guest |
| GET | `/rest/v1/forum_groups` | Group list | Public/RLS |
| POST | `/rest/v1/group_members` | Join group | Bearer + RLS |
| GET | `/rest/v1/posts?group_id=eq.<id>` | Group feed | Member/RLS |
| POST | `/rest/v1/posts` | Create post | Member + RLS |
| POST | `/rest/v1/comments` | Comment on post | Member + RLS |
| POST | `/rest/v1/post_likes` | Like post | Member + RLS |

### 2.7 Analytics, notifications, storage

| Method | Path | Purpose | Auth |
|---|---|---|---|
| GET | `/functions/v1/analytics/dashboard?range=week` | Dashboard data | Bearer |
| GET | `/functions/v1/reports/sales?from=&to=` | Instructor/admin sales report | Instructor/admin |
| POST | `/functions/v1/notify` | Send FCM notification | Server/admin |
| POST | `/storage/v1/object/avatars/<uid>/<file>` | Upload avatar | Bearer + RLS |
| POST | `/storage/v1/object/review-media/<uid>/<file>` | Upload review media | Bearer + RLS |
| POST | `/storage/v1/object/credential-documents/<uid>/<file>` | Upload credential document | Bearer + admin read |

---

## 3. Key contracts

### 3.1 Login

**Request** `POST /auth/v1/token?grant_type=password`

```json
{ "email": "aina@example.com", "password": "verySecret1" }
```

**Response 200**

```json
{
  "access_token": "eyJhbGciOi...",
  "refresh_token": "v1.Mr4g...",
  "expires_in": 3600,
  "token_type": "bearer",
  "user": { "id": "5e2a...", "email": "aina@example.com" }
}
```

### 3.2 Search

**Request** `POST /functions/v1/search`

```json
{
  "q": "Judo black belt",
  "lat": 1.4927,
  "lng": 103.7414,
  "style": "JUDO",
  "credentialVerified": true,
  "ruleset": "GI_NO_GI",
  "day": "WED",
  "maxPriceCents": 15000,
  "limit": 20
}
```

**Response 200**

```json
{
  "results": [
    {
      "instructorId": "b1d2...",
      "name": "Kenji Watanabe",
      "styles": ["JUDO"],
      "credentialsVerified": true,
      "ratingAvg": 5.0,
      "reviewCount": 64,
      "hourlyRateCents": 15000,
      "rulesets": ["GI_NO_GI"],
      "availableDays": ["WED", "THU"]
    }
  ],
  "traceId": "req_abc123"
}
```

### 3.3 Matching

**Request** `POST /functions/v1/match-instructors`

```json
{
  "style": "Brazilian Jiu-Jitsu",
  "level": "BEGINNER",
  "city": "Johor Bahru",
  "budgetCentsMax": 15000,
  "preferredDays": ["SAT", "SUN"],
  "goal": "FITNESS",
  "requirementId": "cr_123",
  "requiredCredential": "JUDO_BLACK_BELT",
  "requiredRuleset": "GI_NO_GI"
}
```

**Response 200**

```json
{
  "gate": { "applied": true, "excludedCount": 9 },
  "matches": [
    {
      "instructorId": "b1d2...",
      "name": "Kenji Watanabe",
      "score": 0.96,
      "factors": {
        "style": 0.30,
        "availability": 0.25,
        "distance": 0.15,
        "rating": 0.15,
        "price": 0.10,
        "verification": 0.05
      },
      "credentialsVerified": true
    }
  ],
  "traceId": "req_def456"
}
```

**Scoring weights** (server-side, documented in the UI): style 30%, availability overlap 25%, distance 15%, rating/review quality 15%, price fit 10%, verification/response rate 5%. For gym sourcing, a **mandatory gate** (verified credential + required ruleset experience) runs before scoring **[Design]**.

### 3.4 Create booking

**Request** `POST /functions/v1/create-booking`

```json
{
  "listingId": "lst_123",
  "slotId": "slot_456",
  "requirementId": "cr_789",
  "ruleset": "GI_NO_GI",
  "paymentMethodId": "pm_card_visa"
}
```

Headers include `Idempotency-Key: 4f1c...` when replaying an offline write.

**Response 201**

```json
{
  "bookingId": "bk_789",
  "status": "PENDING_PAYMENT",
  "amountCents": 15000,
  "commissionCents": 2250,
  "currency": "MYR",
  "ruleset": "GI_NO_GI",
  "holdExpiresAt": "2026-11-27T10:45:00Z",
  "paymentIntent": {
    "id": "pi_3ABx...",
    "clientSecret": "pi_3ABx..._secret_..."
  },
  "traceId": "req_ghi789"
}
```

**Server rules:** atomic slot check in a transaction; slot `HELD` for 10 minutes; commission 10–20% resolved from instructor level/plan; PaymentIntent created with `application_fee_amount = commissionCents`; card details go directly to Stripe.

### 3.5 Confirm booking (webhook-driven)

**Request** `POST /functions/v1/confirm-booking`

```json
{ "bookingId": "bk_789", "paymentIntentId": "pi_3ABx..." }
```

**Response 200**

```json
{
  "bookingId": "bk_789",
  "status": "CONFIRMED",
  "slotStatus": "BOOKED",
  "receiptUrl": "https://...",
  "payoutId": "po_123",
  "traceId": "req_jkl012"
}
```

Confirmation is only trusted when the Stripe webhook signature verifies and the event is `payment_intent.succeeded`. Clients may poll this endpoint for state but cannot set status themselves.

### 3.6 Cancel booking

**Request** `POST /functions/v1/cancel-booking`

```json
{ "bookingId": "bk_789", "reason": "SCHEDULE_CONFLICT" }
```

**Response 200**

```json
{
  "bookingId": "bk_789",
  "status": "CANCELLED",
  "refund": { "amountCents": 15000, "status": "PENDING", "stripeRefundId": "re_123" },
  "traceId": "req_mno345"
}
```

### 3.7 Submit review

**Request** `POST /rest/v1/reviews`

```json
{
  "bookingId": "bk_789",
  "instructorId": "b1d2...",
  "studentId": "5e2a...",
  "rating": 5,
  "title": "Excellent session",
  "body": "Throws finally make sense in sparring.",
  "tags": ["HELPFUL", "GOOD_VALUE"]
}
```

**Response 201** — created row. Constraints: booking must be `COMPLETED` for the same student/instructor; one review per booking (`UNIQUE booking_id`). Media is uploaded separately to Storage and linked through `review_media`.

### 3.8 Analytics dashboard

**Request** `GET /functions/v1/analytics/dashboard?range=week`

**Response 200**

```json
{
  "range": "week",
  "summary": {
    "sessions": 4,
    "durationMinutes": 220,
    "intensity": "MEDIUM",
    "caloriesEstimate": 1120,
    "changePct": 8
  },
  "series": [{ "label": "W1", "value": 45 }, { "label": "W2", "value": 60 }],
  "widgets": [
    { "type": "SESSIONS_DURATION", "position": 1, "enabled": true },
    { "type": "CALORIES", "position": 2, "enabled": true }
  ],
  "traceId": "req_pqr678"
}
```

### 3.9 Sales report

**Request** `GET /functions/v1/reports/sales?from=2026-11-01&to=2026-11-30`

**Response 200**

```json
{
  "currency": "MYR",
  "grossCents": 742000,
  "commissionCents": 111300,
  "bookings": 61,
  "goalProgressPct": 84,
  "trend": [40, 63, 52, 78, 70, 92, 84],
  "payoutSummary": { "paidCents": 620000, "pendingCents": 10700 },
  "traceId": "req_stu901"
}
```

### 3.10 Storage upload

**Request** `POST /storage/v1/object/credential-documents/<uid>/dan-certificate.pdf`

Headers: `Authorization: Bearer <jwt>`, `apikey: <anon>`, `Content-Type: application/pdf`.

**Response 200**

```json
{ "Key": "credential-documents/<uid>/dan-certificate.pdf" }
```

Credential documents are private: only the owner and admins may read them. MIME types and sizes are validated client- and server-side.

---

## 4. Errors

### 4.1 Error envelope

```json
{
  "error": {
    "code": "SLOT_NO_LONGER_AVAILABLE",
    "message": "This time slot was just booked.",
    "traceId": "req_abc123"
  }
}
```

### 4.2 HTTP status codes

| Status | Meaning | Typical use |
|---|---|---|
| 400 | Validation error | Malformed request body or invalid field |
| 401 | Unauthenticated | Missing/expired JWT |
| 403 | Forbidden | Role or RLS denies operation |
| 404 | Not found | Resource id does not exist or is not visible |
| 409 | Conflict | Slot already booked, stale version |
| 422 | Business-rule failure | Review without completed booking, unpaid hold expired |
| 429 | Rate limited | Too many search/autocomplete requests |
| 500 | Unexpected | Server fault; report with `traceId` |

### 4.3 Domain error codes

| Code | HTTP | Meaning | Client action |
|---|---|---|---|
| `INVALID_CREDENTIALS` | 401 | Login failed | Show generic message |
| `TOKEN_EXPIRED` | 401 | JWT expired | Refresh or re-authenticate |
| `ROLE_NOT_ALLOWED` | 403 | Role claim insufficient | Hide destination; show explanation |
| `RLS_DENIED` | 403 | Row not accessible | Refresh or return to previous screen |
| `SLOT_NO_LONGER_AVAILABLE` | 409 | Slot taken or hold expired | Refresh slots and let user pick again |
| `STALE_VERSION` | 409 | Concurrent update | Refetch and retry with new version |
| `DUPLICATE_IDEMPOTENCY_KEY` | 409 | Replayed write already processed | Treat as success and sync state |
| `PAYMENT_NOT_COMPLETED` | 422 | PaymentIntent not successful | Return to checkout |
| `PAYMENT_PROVIDER_ERROR` | 422 | Stripe declined/failed | Show payment error; keep booking pending briefly |
| `REVIEW_REQUIRES_COMPLETED_BOOKING` | 422 | Booking not eligible | Hide review action |
| `REVIEW_ALREADY_EXISTS` | 422 | Duplicate review | Open existing review |
| `CREDENTIAL_NOT_VERIFIED` | 422 | Gate failed | Explain gate; invite browsing other coaches |
| `UNSUPPORTED_MEDIA_TYPE` | 400 | MIME/size not allowed | Show allowed formats and limits |
| `RATE_LIMIT_EXCEEDED` | 429 | Too many requests | Back off and retry |

---

## 5. Webhooks

| Event | Handler | Effect |
|---|---|---|
| `payment_intent.succeeded` | `/functions/v1/confirm-booking` | Booking `CONFIRMED`, slot `BOOKED`, payout scheduled, FCM sent |
| `payment_intent.payment_failed` | `/functions/v1/confirm-booking` | Booking remains/falls back to `PENDING_PAYMENT` or `CANCELLED`, hold released |
| `charge.refunded` | `/functions/v1/cancel-booking` | Refund recorded against booking |
| `payout.paid` | payout sync | `payouts.status = PAID` |

Webhook signatures are verified with the Stripe secret key held only in Edge Functions. Handlers are idempotent by provider event id.

---

## 6. Non-functional API behaviour

| Topic | Rule |
|---|---|
| Transport | HTTPS only; TLS 1.2+ |
| Idempotency | `Idempotency-Key` required for retried booking/review/report writes |
| Rate limiting **[Proposed]** | Search 60 req/min/user; auth 10 req/min/IP; others 120 req/min/user |
| Pagination | PostgREST `limit` / `offset`; Edge Functions `limit` with max 50 |
| Observability | Every error carries `traceId`; structured logs in Edge Functions |
| Data minimisation | No PAN/CVV, passwords or service-role keys in requests/responses |
| Versioning | `/v1` path segment; additive changes are backward compatible |

---

## 7. Android client usage (Retrofit)

```kotlin
interface MoMApi {
    @POST("functions/v1/create-booking")
    suspend fun createBooking(@Body request: CreateBookingRequest): CreateBookingResponse

    @POST("functions/v1/confirm-booking")
    suspend fun confirmBooking(@Body request: ConfirmBookingRequest): BookingDto

    @POST("functions/v1/match-instructors")
    suspend fun match(@Body request: MatchRequest): List<InstructorMatchDto>

    @POST("functions/v1/search")
    suspend fun search(@Body request: SearchRequest): SearchResponse

    @GET("rest/v1/bookings")
    suspend fun bookings(@Query("student_id") studentId: String): List<BookingDto>

    @GET("functions/v1/analytics/dashboard")
    suspend fun dashboard(@Query("range") range: String): DashboardDto
}
```

Interceptors attach `Authorization`, `apikey` and `Idempotency-Key`; error bodies are mapped to domain errors using the `error.code` field.

---

## 8. API acceptance checks

- [ ] All endpoints require the correct auth scope; RLS verified with role-specific tests.
- [ ] Booking creation holds the slot atomically and expires after 10 minutes.
- [ ] Confirmation only trusts verified Stripe webhooks.
- [ ] Commission is calculated server-side and returned to the client.
- [ ] Error envelope and `traceId` present on every 4xx/5xx response.
- [ ] No card data or secret key appears in any request, response or log.
- [ ] Idempotent replay does not create duplicate bookings, reviews or likes.
