import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {

    private const val BaseURL= "https://ec2-13-125-91-166.ap-northeast-2.compute.amazonaws.com/"

    private val moshi = Moshi.Builder().build()

    // timeout setting 해주기
    var okHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retro =
        Retrofit.Builder()
            .baseUrl(BaseURL) // actual backend URL
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()

}
