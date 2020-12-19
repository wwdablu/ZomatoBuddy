package com.soumya.wwdablu.hungry.network

import com.soumya.wwdablu.hungry.model.network.categories.CategoriesModel
import com.soumya.wwdablu.hungry.model.network.cities.CityModel
import com.soumya.wwdablu.hungry.model.network.collections.CollectionModel
import com.soumya.wwdablu.hungry.model.network.cuisine.CuisineModel
import com.soumya.wwdablu.hungry.model.network.establishments.EstablishmentModel
import com.soumya.wwdablu.hungry.model.network.search.SearchModel
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ZomatoAPI {

    @GET("/api/v2.1/categories")
    fun getCategories() : Observable<CategoriesModel>

    @GET("/api/v2.1/collections")
    fun getCollections(@Query("lat") lat: String,
                       @Query("lon") lon: String) : Observable<CollectionModel>

    @GET("/api/v2.1/cities")
    fun getCity(@Query("lat") lat: String,
                @Query("lon") lon: String) : Observable<CityModel>

    @GET("/api/v2.1/cuisines")
    fun getCuisines(@Query("lat") lat: String,
                    @Query("lon") lon: String) : Observable<CuisineModel>

    @GET("/api/v2.1/establishments")
    fun getEstablishments(@Query("lat") lat: String,
                          @Query("lon") lon: String) : Observable<EstablishmentModel>

    @GET("/api/v2.1/search")
    fun searchByCollectionId(@Query("lat") lat: String,
                             @Query("lon") lon: String,
                             @Query("collection_id") collectionId: Int) : Observable<SearchModel>
}