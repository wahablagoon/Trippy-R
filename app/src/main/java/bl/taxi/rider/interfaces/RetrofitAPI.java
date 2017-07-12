package bl.taxi.rider.interfaces;


import bl.taxi.rider.models.Model;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by DELL on 09/07/2017.
 **/

public interface RetrofitAPI {

    @GET("public/api/user")
    Call <Model> getData();
}
