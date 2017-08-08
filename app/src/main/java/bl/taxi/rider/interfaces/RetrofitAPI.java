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

    @GET("api/sentOTP")
    Call<List<Model>> sendOTP(
            @Query("countrycode") String cc, @Query("phone") String mobile_no);

    @GET("api/updateOTP")
    Call<List<Model>> updateOTP(
            @Query("countrycode") String cc, @Query("phone") String mobile_no, @Query("verifycode") String verification_code);

    @GET("api/signup")
    Call<List<Model>> createAccount(
            @Query("role") String role, @Query("name") String userName, @Query("phone") String mobileNumber,@Query("countrycode") String countryCode,@Query("email") String email,@Query("own") String own);

    @GET("json")
    Call<PlacesAutoComplete> getPlaces(
            @Query("input") String input, @Query("location") String location,
            @Query("key") String key, @Query("radius") String radius);
}
