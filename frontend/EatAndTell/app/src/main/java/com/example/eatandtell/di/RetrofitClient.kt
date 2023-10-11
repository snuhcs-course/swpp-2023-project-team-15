import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {

    private const val BaseURL= "http://ec2-13-125-91-166.ap-northeast-2.compute.amazonaws.com/users/"

    private val moshi = Moshi.Builder().build()

    val retro =
        Retrofit.Builder()
            .baseUrl(BaseURL) // actual backend URL
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

}