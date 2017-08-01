package bl.taxi.rider.interfaces;


import java.util.List;

import bl.taxi.rider.models.Model;
import bl.taxi.rider.models.placeautocomplete.PlacesAutoComplete;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by DELL on 09/07/2017.
 **/

public interface RetrofitAPI {

    @GET("api/rider/signin/{email}/{password}")
    Call<List<Model>> getData(
            @Path("email") String email,@Path("password") String password);

    @GET("api/sentOTP/1/{cc}/{mobile_no}")
    Call<List<Model>> sendOTP(
            @Path("cc") String cc, @Path("mobile_no") String mobile_no);

    @GET("json")
    Call<PlacesAutoComplete> getPlaces(
            @Query("input") String input, @Query("key") String key);
}