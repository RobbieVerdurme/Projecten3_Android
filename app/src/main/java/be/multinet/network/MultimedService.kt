package be.multinet.network

import be.multinet.model.Therapist
import be.multinet.network.Request.LoginRequestBody
import be.multinet.network.Response.UserChallengeResponse
import be.multinet.network.Response.UserDataResponse
import com.auth0.android.jwt.JWT
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.lang.RuntimeException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * This calls takes the multimedApi to comunicate with the server
 */
class MultimedService : IApiProvider {
    /**
     * the server baseurl
     */
    private val baseUrl: String = "https://10.0.2.2:44384/api/"

    /**
     * The [IMultimedApi] instance
     */
    private val server: IMultimedApi

    init {
        server = createServer()
    }

    /**
     * get okHttpClient that ignores the certificate
     */
    fun getUnsafeOkHttpClient(): OkHttpClient{
        try{
            //create trust manager that does nog validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(
                object: X509TrustManager{
                    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}

                    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf<X509Certificate>()
                    }
                }
            )

            //install the alltrusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            //create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier(object: HostnameVerifier{
                override fun verify(p0: String?, p1: SSLSession?): Boolean {
                    return true
                }
            })
            return builder.build()

        } catch (e:Exception){
            throw RuntimeException(e)
        }
    }

    /**
     * Build the retrofit database
     */
    private fun createServer(): IMultimedApi{
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            )
            .client(getUnsafeOkHttpClient())
            .build()
        return retrofit.create(IMultimedApi::class.java)
    }

    /**
     * Login the user
     * returns a jwt token
     */
    override suspend fun loginUser(userbody: LoginRequestBody): Response<JWT> {
        return server.loginUser(userbody)
    }

    /**
     * asks for a user in the database a id
     */
    override suspend fun getUser(userid: Int): Response<UserDataResponse> {
        return server.getUser(userid)
    }

    /**
     * asks for the challenges of a user with id
     */
    override suspend fun getChallengesUser(userid: Int): Response<List<UserChallengeResponse>> {
        return server.getChallengesUser(userid)
    }

    /**
     * set the challenge to completed
     *
    override suspend fun completeChallenge(userid: Int, challengeId: Int): Response<>{
        return server
    }
    */

    /**
     * ask for the therapists of the user with id
     */
    override suspend fun getTherapists(userid: Int): Response<List<Therapist>> {
        return server.getTherapists(userid)
    }
}