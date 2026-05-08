package com.example.nurdor_volunteer_app_v3.retrofit

import com.example.nurdor_volunteer_app_v3.dto.cityDto.CityDto
import com.example.nurdor_volunteer_app_v3.dto.eventDto.CreateEventDto
import com.example.nurdor_volunteer_app_v3.dto.standDto.DonationDto
import com.example.nurdor_volunteer_app_v3.dto.eventDto.EndEventResultDto
import com.example.nurdor_volunteer_app_v3.dto.eventDto.EventDto
import com.example.nurdor_volunteer_app_v3.dto.eventsLogDto.EventsLogDto
import com.example.nurdor_volunteer_app_v3.dto.authDto.LoginDto
import com.example.nurdor_volunteer_app_v3.dto.authDto.RegisterDto
import com.example.nurdor_volunteer_app_v3.dto.eventDto.CreateEventsLogDto
import com.example.nurdor_volunteer_app_v3.dto.standDto.StandDto
import com.example.nurdor_volunteer_app_v3.dto.eventDto.StartEventDto
import com.example.nurdor_volunteer_app_v3.dto.eventDto.StartEventResultDto
import com.example.nurdor_volunteer_app_v3.dto.eventsLogDto.UpdatePresenceDto
import com.example.nurdor_volunteer_app_v3.dto.volunteerDto.VolunteerDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    // auth-service calls
    @POST("/login")
    fun login(@Body volunteer: LoginDto): Call<Map<String, Object>>

    @POST("/register")
    fun register(@Body volunteer: RegisterDto): Call<Map<String, Object>>

    // volunteer-service calls
    @GET("/volunteer/volunteers")
    fun fetchAllVolunteers(): Call<List<VolunteerDto>>
    @GET("/cities")
    fun fetchAllCities(): Call<List<CityDto>>

    // events-log-service calls
    @GET("/volunteer/allEventsLogs")
    fun fetchAllEventsLogs(): Call<List<EventsLogDto>>

    @POST("/volunteer/insertLogs")
    fun insertEventLog(@Body eventsLogs: List<CreateEventsLogDto>): Call<List<EventsLogDto>>

    @DELETE("/volunteer/deleteLog")
    fun deleteEventsLog(@Query("idEvent") idEvent: Int, @Query("idVolunteer") idVolunteer: Int): Call<String>

    @PATCH("/volunteer/updatePresence")
    fun updatePresence(@Body updatePresenceDto: UpdatePresenceDto): Call<EventsLogDto>

    @PATCH("/volunteer/updateLastSeen")
    fun updateLastSeenTimestamp(@Body updatePresenceDto: UpdatePresenceDto): Call<String>

    // event-service calls
    @POST("/admin/newEvent")
    fun createEvent(@Body eventDto: CreateEventDto): Call<EventDto>

    @DELETE("/admin/deleteEvent/{idEvent}")
    fun deleteEvent(@Path("idEvent") idEvent: Int): Call<String>

    @GET("/volunteer/eventPdf/{idEvent}")
    fun downloadPdfWithEventById(@Path("idEvent") idEvent: Int): Call<ResponseBody>

    @GET("/volunteer/getEvents")
    fun fetchAllEvents(): Call<List<EventDto>>

    @POST("/admin/start")
    fun startEvent(@Body startEventDto: StartEventDto): Call<StartEventResultDto>

    @POST("/admin/end/{idEvent}")
    fun endEvent(@Path("idEvent") idEvent: Int): Call<EndEventResultDto>

    // donation-service calls
    @GET("/volunteer/stands")
    fun fetchAllStands(): Call<List<StandDto>>

    @PATCH("/volunteer/addDonation")
    fun fetchDonationResponse(@Body donationDto: DonationDto): Call<String>

}