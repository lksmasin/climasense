package org.climasense.sources.baiduip

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import org.climasense.sources.baiduip.json.BaiduIPLocationResult

interface BaiduIPLocationApi {
    @GET("location/ip")
    fun getLocation(
        @Query("ak") ak: String,
        @Query("coor") coor: String
    ): Observable<BaiduIPLocationResult>
}
