package com.example.feature_search.model.remote

import com.example.feature_search.model.response.SearchResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface to define HTTP methods use to communicate with the OMDB Apis
 */
interface OmdbService {

    /**
     * Companion Object lets us create a static scope similar to use static keyword in Java
     */
    companion object {

        /**
         * Const Val is a compile time constant, this means it's initialized and added to memory
         * when you compile rather than when it's running.
         *
         * In this case we are using it to define the BASE_URL of the OMDB Api
         */
        private const val HOST = "http://www.omdbapi.com"

        /**
         * We are using lazy to create a instance of this OMDB service class, lazy means it will only
         * run code within it's scope{} when the first time the variable is used. Any subsequent uses
         * will return the result of the scope{} first them it was ran.
         *
         * In this case the first instance variable is called it was run the Retrofit Builder and use it to
         * create the OMDB Service. After wards that instance from the first time will be the value
         */
        val instance: OmdbService by lazy {
            Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create()
        }
    }

    /**
     * @GET -> This is GET http call we add the retrofit annotation, we use "." because this endpoint has no path
     *
     * suspend -> this is now a coroutine method which means we want to run this on another thread that's NOT MAIN,
     * we don't want to run this on the main thread because we don't know how long it will take and we don't want to
     * freeze the screen during this time or it will lead to a ANR(Application Not Responding)
     *
     * @Query -> Used to add queries to the endpoint based on the value passed in
     * Response<SearchResponse> -> This will return the Full Http Details, which includes things like status code.
     * The Search Response will be in the body property of the response object...
     */
    @GET(".")
    suspend fun searchByQuery(
        @Query("s") query: String,
        @Query("apikey") apiKey: String = "368629d5" // No you can't use my limit, Add your own key :D
    ): Response<SearchResponse>

}
