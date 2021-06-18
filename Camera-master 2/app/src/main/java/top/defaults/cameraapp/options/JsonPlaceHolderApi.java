package top.defaults.cameraapp.options;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonPlaceHolderApi {
    @GET("get_fuel_efficiency?images=")
    Call<List<Post>> getResults();

//    @POST()
}