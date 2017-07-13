package bl.taxi.rider;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import bl.taxi.rider.controller.MyApplication;
import bl.taxi.rider.interfaces.RetrofitAPI;
import bl.taxi.rider.models.Model;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SampleActivity extends AppCompatActivity {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.int_id)
    TextView intId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        ButterKnife.bind(this);
        retrofitCall();
    }

    private void retrofitCall () {

        RetrofitAPI service = MyApplication.getService();
        Call<List<Model>> query = service.getData("msg2thirumalai@gmail.com","thirumalai");
        query.enqueue(new Callback<List<Model>>() {
            @Override
            public void onResponse(Call<List<Model>> call, Response<List<Model>> response) {
                System.out.print("response" + response);
                List<Model> result = response.body();
                if (result.size()!= 0) {
                    for (int i = 0; i < result.size(); i++) {
                        String responseStatus = result.get(i).getEmail();
                        String firstName = result.get(i).getFirstName()+" "+result.get(i).getLastName();
                        name.setText(responseStatus);
                        intId.setText(firstName);
                    }

                } else
                    System.out.print("hai result null");
            }
            @Override
            public void onFailure(Call<List<Model>> call, Throwable t) {

                Log.e("Sample", "retrofit", t);
            }

        });
    }
}
