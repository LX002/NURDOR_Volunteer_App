import com.example.rma_nurdor_project_v2.dto.EventDto
import com.example.rma_nurdor_project_v2.dto.EventsLogDto
import com.example.rma_project_demo_v1.dto.CityDto
import com.example.rma_project_demo_v1.dto.VolunteerDto
import com.example.rma_project_demo_v1.dto.VolunteerExpandedDto
import com.example.rma_project_demo_v1.dto.VolunteerRoleDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {

    @POST("api/login")
    fun login(@Body volunteer: VolunteerDto): Call<Boolean>

    @POST("api/login/saveVolunteer")
    fun saveVolunteer(@Body volunteer: VolunteerExpandedDto): Call<Boolean>

    @GET("api/volunteers/getVolunteers")
    fun getVolunteers(): Call<List<VolunteerExpandedDto>>

    @GET("api/login/getCities")
    fun getCities(): Call<List<CityDto>>

    @GET("api/login/getRoles")
    fun getRoles(): Call<List<VolunteerRoleDto>>

    @GET("api/events/getEvents")
    fun getEvents(): Call<List<EventDto>>

    @GET("api/events/getEventsLogs")
    fun getEventsLogs(): Call<List<EventsLogDto>>

    @GET("api/events/downloadEventPdf/{idEvent}")
    fun getEventPdf(@Path("idEvent") idEvent: Int): Call<ResponseBody>

    @POST("api/events/insertEvent")
    fun insertEvent(@Body eventDto: EventDto): Call<Boolean>

    @POST("api/events/insertLog")
    fun insertLog(@Body eventsLogDto: EventsLogDto): Call<Boolean>

    @POST("api/events/markAsPresent")
    fun markAsPresent(@Body eventsLogDto: EventsLogDto): Call<Boolean>

    @POST("api/events/insertInitLogs")
    fun insertInitLogs(@Body initLogsDtos: List<EventsLogDto>): Call<Boolean>

}