package bl.taxi.rider.interfaces;


import java.util.List;

import bl.taxi.rider.models.Model;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by DELL on 09/07/2017.
 **/

public interface RetrofitAPI {

    @GET("api/rider/signin/{email}/{password}")
    Call<List<Model>> getData(
            @Path("email") String email,@Path("password") String password);
}
