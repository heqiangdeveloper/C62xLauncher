package com.chinatsp.volcano.api;

import com.chinatsp.volcano.api.response.VolcanoResponse;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface IHomeCardApi {
    public static String API_HOME_CARD = "callback/home/cards";

    @GET(API_HOME_CARD)
    Observable<VolcanoResponse> getHomeCards(@QueryMap Map<String, String> queryFields, @HeaderMap Map<String, String> headers);
}
