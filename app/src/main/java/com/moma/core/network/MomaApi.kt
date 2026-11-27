package com.moma.core.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Contract for the MoMA backend, mirroring `moma_openapi.yaml`.
 *
 * The gym-first build runs on Room seed data (BuildConfig.USE_MOCK_DATA), but the
 * shapes here match the documented API so swapping the data source is a
 * repository change only.
 */
interface MomaApi {

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(@Body body: SignInRequest): SignInResponse

    @GET("rest/v1/gyms")
    suspend fun gyms(@Header("apikey") apiKey: String): List<GymDto>

    @GET("rest/v1/gym_programs")
    suspend fun programs(
        @Header("apikey") apiKey: String,
        @Query("gym_id") gymId: String,
        @Query("order") order: String = "day_index.asc,start_time.asc"
    ): List<ProgramDto>

    @GET("rest/v1/coach_requirements")
    suspend fun requirements(
        @Header("apikey") apiKey: String,
        @Query("gym_id") gymId: String
    ): List<RequirementDto>

    @POST("functions/v1/match-coaches")
    suspend fun matchCoaches(
        @Header("Authorization") bearer: String,
        @Body body: MatchRequest
    ): List<MatchDto>

    @POST("functions/v1/create-booking")
    suspend fun createBooking(
        @Header("Authorization") bearer: String,
        @Body body: CreateBookingRequest
    ): BookingDto

    @POST("functions/v1/confirm-payment")
    suspend fun confirmPayment(
        @Header("Authorization") bearer: String,
        @Body body: ConfirmPaymentRequest
    ): BookingDto

    @GET("rest/v1/bookings")
    suspend fun bookings(
        @Header("Authorization") bearer: String,
        @Query("gym_id") gymId: String,
        @Query("order") order: String = "created_at.desc"
    ): List<BookingDto>

    @GET("rest/v1/coaches/{id}")
    suspend fun coach(
        @Header("Authorization") bearer: String,
        @Path("id") id: String
    ): CoachDto
}

data class SignInRequest(val email: String, val password: String)
data class SignInResponse(val access_token: String, val token_type: String, val expires_in: Long)

data class GymDto(
    val id: String,
    val name: String,
    val city: String,
    val address: String,
    val tagline: String,
    val verified: Boolean
)

data class ProgramDto(
    val id: String,
    val gym_id: String,
    val day_index: Int,
    val style_id: String,
    val title: String,
    val focus: String,
    val rule_set_id: String,
    val start_time: String,
    val end_time: String,
    val level: String,
    val coach_role: String,
    val assigned_coach_id: String?,
    val is_open_mat: Boolean
)

data class RequirementDto(
    val id: String,
    val gym_id: String,
    val role_title: String,
    val style_id: String,
    val rule_set_id: String,
    val required_credential_id: String,
    val minimum_grade: String,
    val minimum_years: Int,
    val sessions_per_week: Int,
    val notes: String
)

data class MatchRequest(
    val requirement_id: String,
    val day_index: Int,
    val require_verified_credential: Boolean = true
)

data class MatchDto(
    val coach_id: String,
    val name: String,
    val total_score: Int,
    val gate_passed: Boolean,
    val gate_reason: String,
    val breakdown: Map<String, Int>
)

data class CreateBookingRequest(
    val gym_id: String,
    val coach_id: String,
    val program_id: String,
    val slot_id: String,
    val rule_set_id: String
)

data class ConfirmPaymentRequest(val booking_id: String, val payment_intent_id: String)

data class BookingDto(
    val id: String,
    val gym_id: String,
    val coach_id: String,
    val program_title: String,
    val style_id: String,
    val rule_set_id: String,
    val day_index: Int,
    val time: String,
    val price: Double,
    val commission: Double,
    val status: String
)

data class CoachDto(
    val id: String,
    val name: String,
    val headline: String,
    val verified: Boolean,
    val years_experience: Int,
    val style_ids: List<String>,
    val rule_set_ids: List<String>
)
