# MoMA — Product Requirements Document (PRD)

| Field | Value |
|---|---|
| Product | MoMA (Masters of Martial Arts) — Android marketplace for martial arts coaching |
| Document version | 1.0 (design baseline) |
| Status | Proposed for implementation approval |
| Owner | Product / entrepreneurship seminar team |
| Platform | Android (Kotlin, Jetpack Compose), Android Studio |
| Source material | `Assignments/Assignment2.pdf`, `MoMA_Android_App_Design.md`, `MoMA_Android_Solution.html`, `MoMA_system_architecture.png`, `MoMA_booking_and_payment_flow.png` |
| Related documents | SRS, UX/UI Design Specs, SAD, DDD, API Documentation |

> **Source convention.** Statements traceable to the assignment brief are marked **[Brief]**. Decisions proposed in the supplied design/solution artefacts are marked **[Design]**. Targets introduced by this PRD for the first time are marked **[Proposed]** and require stakeholder confirmation.

---

## 1. Product vision and objectives

### 1.1 Vision

MoMA is a shared/gig-economy marketplace that connects martial arts instructors with students who want flexible, personalised training. Instructors monetise their expertise on their own schedule; students discover, compare, book, pay for and review lessons in one place. **[Brief]**

The first commercial beachhead is a **BJJ gym in Johor Bahru that needs four specialist coaches** for a fixed weekly curriculum. The general two-sided marketplace follows once that program is staffed and running. **[Design]**

### 1.2 Product objectives

| ID | Objective | Rationale / source |
|---|---|---|
| PO-1 | Give instructors autonomy over what, when and to whom they teach | Brief: flexibility, autonomy |
| PO-2 | Give students convenient, affordable access to verified specialist coaches | Brief: convenience, cost-effectiveness |
| PO-3 | Establish trust before any money changes hands (verified credentials, transparent prices, reviews, dispute path) | Brief key features + Design principles |
| PO-4 | Make booking and payment server-authoritative so no client can forge a confirmed booking | Design principle: server-authoritative money |
| PO-5 | Support the BJJ gym's four hiring needs with a credential-gated shortlist and ruleset-aware scheduling | Design: first stakeholder |
| PO-6 | Work gracefully on mobile networks with offline reads and queued non-payment writes | Design: offline-first reads |
| PO-7 | Provide a role-aware experience for students, coaches, gym owners and administrators | Brief target users + Design roles |
| PO-8 | Generate platform revenue via 10–20% commission, with optional instructor subscriptions and advertising later | Brief revenue model |

### 1.3 Business and success metrics

Financial context from the brief **[Brief]**: initial investment **MYR 220,000**; revenue **MYR 660,000 / 1,430,000 / 2,200,000** and net profit **MYR 140,000 / 400,000 / 530,000** in years 1–3.

Product-level success measures **[Proposed — confirm before committing]**:

| Metric | Baseline | Target |
|---|---|---|
| Gym program coverage | 3 of 7 days staffed internally | 4 open coach requirements filled; Wed/Thu/Fri sessions bookable |
| Credential-gate precision | n/a | 100% of shortlisted candidates have a verified required credential |
| Booking conversion (search → confirmed booking) | n/a | ≥ 25% within first gym cohort |
| Payment confirmation success | n/a | ≥ 98% of PaymentIntents reaching `CONFIRMED` within 60 s of success webhook |
| Crash-free sessions | n/a | ≥ 99.5% |
| Accessibility | n/a | Zero critical Accessibility Scanner findings on release-critical screens |

---

## 2. Target market and user personas

### 2.1 Target market

- **Demographics:** individuals aged **18–55** interested in martial arts training: beginners, intermediate and advanced practitioners. **[Brief]**
- **Psychographics:** people who value flexibility, autonomy and personalised training and who want convenient, affordable learning. **[Brief]**
- **Geographic beachhead:** Johor Bahru, Malaysia; currency **MYR**; PDPA-aligned handling of personal data. **[Design]**

### 2.2 Personas

#### P1 — Aina, the student / gym member (primary)

| Attribute | Detail |
|---|---|
| Age / context | 27, working professional, beginner BJJ practitioner, trains twice weekly |
| Goals | Find a verified coach for a specific style, compare price and schedule, book and pay quickly, track progress |
| Pain points | Traditional schools are rigid and expensive; unsure which coach is qualified; no way to see progress |
| Needs | Style/credential/ruleset filters, transparent price, verified badge, reviews, booking status, dashboard |
| Success moment | She books Wed "Judo for BJJ" with a verified black belt, pays by card, and later rates the session |
| App areas | Search, matching, coach detail, checkout, bookings, reviews, dashboard, groups |

#### P2 — Kenji, the coach / instructor (primary supply side)

| Attribute | Detail |
|---|---|
| Age / context | 34, Judo 4th dan, 12 years coaching, wants extra income alongside his main job |
| Goals | Publish lessons, control availability, be verified, receive payouts, build reputation |
| Pain points | Scheduling by phone, no proof of qualification to customers, delayed or opaque payments |
| Needs | Listing editor, availability grid, credential upload + verification state, sales report, Stripe Connect payouts |
| Success moment | His verified dan certificate makes him eligible for the gym's Wed program; he is booked and paid |
| App areas | Instructor workspace, listing editor, availability, sales report, payouts, profile/portfolio |

#### P3 — The gym owner / program manager (first customer) **[Design]**

| Attribute | Detail |
|---|---|
| Context | Owns a BJJ gym in Johor Bahru; runs a Mon–Sun curriculum |
| Goals | Fill four specialist roles (Judo black belt, wrestler, Kyokushin black belt, Muay Thai coach), publish the weekly program, manage coverage |
| Pain points | Hard to source verified specialists; unclear who can coach ADCC vs ACBJJ Battle Jiu-Jitsu rulesets |
| Needs | Gym home coverage KPIs, weekly curriculum, coach shortlist with mandatory credential gate, role posting, program bookings |
| Success moment | Four open requirements are filled by verified coaches and the Wed/Thu/Fri sessions appear as bookable |
| App areas | Gym home, weekly curriculum, coach shortlist, coach evaluation, role posting, bookings |

#### P4 — The admin / moderator (trusted operator)

| Attribute | Detail |
|---|---|
| Context | MoMA operations staff responsible for trust, safety and payment integrity |
| Goals | Verify credentials, moderate reports, resolve disputes, monitor payouts |
| Pain points | Unverified coaches reaching customers, fraudulent or abusive content, unresolved payment conflicts |
| Needs | Admin console, credential verification queue, dispute queue, moderation queue, sales/commission report |
| Success moment | Every shortlisted coach has an approved credential; open disputes have auditable resolutions |

---

## 3. User stories

Format: *As a <role>, I want <capability> so that <benefit>.* Priority follows **MoSCoW**: Must / Should / Could / Won't (this release).

### Epic A — Authentication and profile (Brief wireframe areas 1–2)

| ID | User story | Priority | Acceptance criteria |
|---|---|---|---|
| US-A01 | As a new user, I want to register with name, phone, email and password so that I have a MoMA account | Must | Supabase Auth user created; profile row created; privacy notice accepted; invalid fields blocked; JWT stored in Keystore-backed storage |
| US-A02 | As a returning user, I want to sign in with email/password or social login so that I can resume quickly | Must | Token endpoint called; session restored after restart; role claim routes to the correct home |
| US-A03 | As a user who forgot my password, I want a reset link so that I can recover access | Must | `POST /auth/v1/recover`; neutral confirmation shown whether or not the account exists |
| US-A04 | As an instructor, I want to edit my portfolio (bio, styles, qualifications, media) so that students can evaluate me | Must | Owner-only RLS; offline edits queued in outbox; public portfolio shows verified badge |
| US-A05 | As a user, I want to sign out so that my tokens are cleared | Must | Tokens removed; unauthenticated graph shown |

### Epic B — Discovery, listings and matching (areas 3, 4, 6)

| ID | User story | Priority | Acceptance criteria |
|---|---|---|---|
| US-B01 | As a student, I want to browse style categories so that I can find relevant coaches | Must | Six focus styles visible in release 1 (BJJ Gi, BJJ No-Gi, Judo, Wrestling, Kyokushin, Muay Thai) **[Design]** |
| US-B02 | As a student, I want search with autocomplete and filters so that I can narrow by style, city, price, rating and availability | Must | Debounced search; suggestions after 2 characters; results cached into Room; "saved data" banner when offline |
| US-B03 | As a gym owner, I want search to filter by verified credential and ruleset so that only eligible coaches appear | Must | Unverified credentials excluded before scoring; ADCC / ACBJJ Battle Jiu-Jitsu filter available |
| US-B04 | As a coach, I want to publish/edit a lesson listing so that it appears in discovery | Must | Title, style, ruleset, level, duration, price, city, active flag; owner-only RLS |
| US-B05 | As a user, I want ranked match results with a score explanation so that I understand why a coach was recommended | Should | Server returns factor breakdown: style 30%, availability 25%, distance 15%, rating 15%, price 10%, verification 5% **[Design]** |
| US-B06 | As a gym owner, I want a credential gate before matching so that unqualified coaches never reach my shortlist | Must | Mandatory check on verified credential and required ruleset experience before scoring |

### Epic C — Booking and payment (areas 7, 9 partly)

| ID | User story | Priority | Acceptance criteria |
|---|---|---|---|
| US-C01 | As a student, I want to see availability for a coach so that I can pick a time | Must | Slots show FREE / HELD / BOOKED; ruleset shown on program slots; stale slots refreshed |
| US-C02 | As a student, I want a 4-step checkout (Lesson → Slot → Payment → Review) so that I can complete a purchase confidently | Must | Order summary shows price, coach, ruleset, time, venue; service fee disclosed |
| US-C03 | As a student, I want to pay securely by card so that my booking is confirmed | Must | Server creates PaymentIntent with 10–20% commission; card data handled by Stripe SDK only; no PAN/CVV in SQLite or logs |
| US-C04 | As a student, I want my booking state to update after payment so that I know it is confirmed | Must | Webhook `payment_intent.succeeded` sets booking CONFIRMED and slot BOOKED; client syncs; server state wins |
| US-C05 | As a user, I want held slots to expire so that inventory is not blocked forever | Must | Server holds slot for 10 minutes; expiry releases to FREE |
| US-C06 | As a user, I want to cancel a booking so that refund policy is applied consistently | Should | `cancel-booking` function enforces server policy; status CANCELLED; refund recorded |
| US-C07 | As a gym owner, I want to book a coach for a specific program day with the correct ruleset so that my curriculum is covered | Must | Wed Judo, Thu Wrestling (ADCC), Fri Striking (ACBJJ Battle Jiu-Jitsu) stored on booking |

### Epic D — Trust, safety and support (areas 8, 9, 11, 12)

| ID | User story | Priority | Acceptance criteria |
|---|---|---|---|
| US-D01 | As a student, I want to rate a completed lesson once with stars, tags, text and optional media so that others can trust the review | Must | Review requires COMPLETED booking for the same student/instructor pair; unique on `booking_id`; media queued offline |
| US-D02 | As a user, I want to raise a dispute with evidence so that a payment problem can be reviewed | Should | Booking reference, reason, details, evidence upload; ticket visible to reporter and admins only |
| US-D03 | As a user, I want to report a user or content so that moderators can act | Should | Reasons: spam, hate speech, false information, adult content, violence, IP violation, other; offline queue |
| US-D04 | As a user, I want a support form and contact options so that I can get help | Must | Subject, issue, image, email, phone; email/phone/chat contacts; offline draft queued |
| US-D05 | As an admin, I want to verify credentials so that only qualified coaches are shortlisted and paid | Must | Admin-only decision; approval sets `verified` flag used by the matching gate |
| US-D06 | As an admin, I want dispute and moderation queues so that cases are resolved with an audit trail | Should | Status transitions and decisions recorded; affected users see read-only state |

### Epic E — Community and analytics (areas 5, 10)

| ID | User story | Priority | Acceptance criteria |
|---|---|---|---|
| US-E01 | As a user, I want to discover and join groups so that I can connect with others | Should | Suggested / Your groups / Near you tabs; membership enforced by RLS |
| US-E02 | As a member, I want to post, like and comment so that discussions are useful | Should | Optimistic UI; drafts saved locally; WorkManager replay with idempotency key |
| US-E03 | As a student, I want a training dashboard so that I can see sessions, duration, intensity and estimates | Should | Today / Week / Month ranges; chart and stat cards; data from analytics function |
| US-E04 | As a user, I want to toggle and reorder dashboard widgets so that the dashboard suits me | Could | Preferences stored locally and synced |
| US-E05 | As a coach/admin, I want a sales report so that I can track revenue, commission and booking trends | Should | Server-generated aggregates; no raw payment data on device |

---

## 4. Core features

| # | Feature | Description | Priority | Phase |
|---|---|---|---|---|
| F-01 | Authentication | Email/password + Google/Facebook sign-in, password reset, secure session restore | Must | 1 |
| F-02 | Profiles & portfolios | Role-aware profile, instructor portfolio, verification badge, edit with offline queue | Must | 1 |
| F-03 | Style catalogue & listings | Six focus styles, listing editor, content detail | Must | 1 |
| F-04 | Search & discovery | Debounced search, autocomplete, filters, cached results | Must | 1 |
| F-05 | Gym program & requirements | Weekly Mon–Sun curriculum, four coach requirements, ruleset tags, role posting | Must (beachhead) | 1 |
| F-06 | Matching with credential gate | Hard credential/ruleset gate + weighted score with explanation | Should | 2 |
| F-07 | Availability & checkout | Slot states, 4-step checkout, Stripe PaymentIntent, server confirmation | Must | 1 |
| F-08 | Bookings | Upcoming/completed/cancelled tabs, status chips, cancel/review/dispute actions | Must | 1 |
| F-09 | Reviews | 5-star rating, quick tags, text and media composer | Must | 1 |
| F-10 | Trust & safety | Disputes, reports, admin verification and moderation | Should | 3 |
| F-11 | Community | Groups, feed, posts, comments, likes | Should | 2 |
| F-12 | Analytics | Training dashboard, custom widgets, sales report | Should | 2–3 |
| F-13 | Support | Feedback form, image upload, email/phone/chat contacts | Must | 1 |
| F-14 | Notifications | FCM for booking, payment, dispute, forum events | Should | 2 |
| F-15 | Offline sync | Room local source of truth, outbox + WorkManager, conflict rules | Must | 1 |
| F-16 | Accessibility | 48 dp targets, TalkBack labels, text scaling, status not by colour alone | Must | 4 (release gate) |

### 4.1 Feature-to-wireframe coverage

The brief defines 13 wireframe areas. All 13 are covered by the feature set above; the full screen-by-screen mapping is in **UX/UI Design Specs**, section 3.

---

## 5. Explicitly out of scope

### 5.1 Not in the product at all (excluded by design)

| Item | Reason |
|---|---|
| iOS, web or desktop clients | Assignment specifies an Android app built in Android Studio **[Brief + Design]** |
| Storing card numbers, CVV or Stripe secret/service-role keys on the device or in SQLite | Security rule; Stripe owns tokenisation **[Design]** |
| Client-side authorisation of payments or booking confirmation | Money state is server-authoritative **[Design]** |
| Offline payment authorisation | No offline card authorisation; booking/pay require network **[Design]** |
| Live video streaming, live chat between users, or one-to-one messaging | Not in the brief; "chat" in support means a contact channel, not a messaging product |
| Video hosting/streaming infrastructure; media storage beyond images and short review/dispute uploads | Only image/video attachments in reviews and evidence are specified |
| Multi-language/localisation beyond the delivered English UI | Not requested; MYR currency and Malaysia PDPA are in scope |
| Native maps turn-by-turn navigation, GPS tracking or check-in | Optional coarse location for nearby search only |
| Automated payout disputes/adjudication by AI; automatic refunds without admin review | Disputes require human moderation |
| Ads delivery network, advertiser self-service portal, instructor billing/subscription checkout UI | Revenue streams named in the brief but not specified as v1 features **[Brief + Design]** |

### 5.2 Deferred to later phases (not "never")

| Item | Phase / trigger |
|---|---|
| Instructor subscription plans and promoted placement | Phase 3; subscription status already affects promoted placement in server rules **[Design]** |
| Targeted advertising | Phase 3; requires advertiser supply and ad-serving integration |
| Advanced analytics and custom widgets beyond Today/Week/Month | Phase 2–3 |
| Gym-broad marketplace rollout beyond the BJJ beachhead; additional styles beyond the six focus styles | After the four gym roles are filled **[Design]** |
| Supabase Realtime for live forum threads | Optional after Phase 2 |
| Certificate pinning in production builds | Hardening/Play Store phase **[Design]** |
| Maestro end-to-end automation and Accessibility Scanner in CI | Hardening phase **[Design]** |

---

## 6. Business model alignment

| Revenue stream | Product support in this release |
|---|---|
| Commission 10–20% per lesson | Resolved server-side from instructor level/plan; shown as included service fee at checkout; recorded in `bookings.commission_cents` and payouts **[Brief + Design]** |
| Instructor subscription (optional) | Deferred UI; backend rule reserved for promoted placement and advanced analytics |
| Targeted advertising | Deferred; no ad inventory or SDK in v1 |

---

## 7. Release phasing

| Phase | Scope | Exit criterion |
|---|---|---|
| **1 — MVP / gym-first** | Auth, profiles, six style catalogue, search, coach detail with credential gate, gym curriculum and requirements, availability, checkout + Stripe test mode, bookings, reviews, support, offline reads/outbox | Gym can publish Mon–Sun curriculum and see four open requirements; a verified coach can be booked and paid **[Design]** |
| **2 — Matching & community** | Ranked matching with factor breakdown, groups/feed/posts, FCM notifications, training dashboard | Shortlists and forum interactions work with role-aware access |
| **3 — Trust & monetisation** | Disputes, reports, admin console/verification/moderation, sales report, subscriptions/ads groundwork | Admin can resolve a dispute with audit record |
| **4 — Hardening & release** | Accessibility, performance, offline conflict testing, certificate pinning, Play Store checklist | All PRD acceptance criteria and NFR targets met |

---

## 8. Dependencies, assumptions and risks

| Category | Item |
|---|---|
| Dependencies | Supabase project (Auth, PostgREST, PostgreSQL + RLS, Storage, Edge Functions); Stripe account + Connect; Firebase Cloud Messaging; Google/Facebook OAuth credentials; Android Studio with latest stable SDK |
| Assumptions | minSdk 26; Kotlin 2.x; Compose BOM; one gym as first customer; Johor Bahru as initial city; MYR pricing |
| Risk — credential fraud | Mitigation: admin verification before shortlisting/payout; document allowlist and re-scan |
| Risk — slot double-booking | Mitigation: atomic server transaction + 10-minute hold + version conflict check |
| Risk — offline data loss | Mitigation: outbox with idempotency keys; server wins for booking/payment, field-level LWW for text |
| Risk — scope creep from 13 wireframe areas | Mitigation: phased delivery and explicit out-of-scope list above |
| Risk — payment non-conformance / PDPA | Mitigation: Stripe-only card handling, RLS, data deletion request flow, privacy notice at registration |

---

## 9. Acceptance criteria (product level)

1. A new user can register, log out, reset a password and restore a session after restart.
2. An instructor can publish a lesson and availability slots that appear in student search.
3. A student can search/filter, open a coach, select a slot, pay with a Stripe test card and see a confirmed booking.
4. A completed booking can be rated once with text and optional media.
5. Reports, disputes and support tickets can be submitted offline and sync when connectivity returns.
6. Forum posts, comments and likes work with role-aware access and optimistic UI.
7. Dashboard widgets can be toggled/reordered and show Today/Week/Month ranges.
8. No payment secrets or other users' private data are stored in SQLite.
9. The gym can publish the Mon–Sun curriculum and see four open coach requirements with mandatory credentials and rulesets.
10. Coach shortlists exclude candidates without a verified Judo / Kyokushin black belt or equivalent credential and show ADCC / ACBJJ Battle Jiu-Jitsu experience where required.
11. A coach can be booked for Wed Judo, Thu Wrestling (ADCC) or Fri Striking (ACBJJ Battle Jiu-Jitsu) with the correct ruleset stored on the booking.

*Criteria 1–8 from the supplied design solution; 9–11 from the gym-first stakeholder scope.* **[Design]**

---

## Appendix A — Source traceability

| Source | Information used |
|---|---|
| `Assignment2.pdf` | Motivation, explanation, key features, revenue model, target market, marketing, operations, financial projections, 13 wireframe areas, Figma reference |
| `MoMA_Android_App_Design.md` | Roles, phases, technology stack, architecture, 19 screen specifications, Room tables, API catalogue, security, testing, roadmap, acceptance criteria |
| `MoMA_Android_Solution.html` | Gym-first first stakeholder, four coach requirements, weekly curriculum, four roles, 33-screen inventory, role-aware bottom navigation, credential gate, matching weights, NFR and delivery plan |
| PNG diagrams | Client → Supabase → external services architecture; six-step booking/payment sequence |
