package bl.taxi.driver.interfaces;


import bl.taxi.driver.models.Model;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by DELL on 09/07/2017.
 **/

public interface RetrofitAPI {

    @GET("public/api/user")
    Call <Model> getData();
}
