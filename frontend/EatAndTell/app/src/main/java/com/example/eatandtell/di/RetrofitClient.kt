import com.example.eatandtell.di.ApiService
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {

    private const val BaseURL= "https://ec2-13-125-91-166.ap-northeast-2.compute.amazonaws.com/"

    private val moshi = Moshi.Builder().build()

    val retro =
        Retrofit.Builder()
            .baseUrl(BaseURL) // actual backend URL
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

}
