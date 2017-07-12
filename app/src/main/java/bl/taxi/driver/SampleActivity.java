package bl.taxi.driver;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import bl.taxi.driver.controller.MyApplication;
import bl.taxi.driver.interfaces.RetrofitAPI;
import bl.taxi.driver.models.Model;
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
        Call<Model> query = service.getData();
        query.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(@NonNull Call<Model> call, @NonNull Response<Model> response) {
                System.out.print("hai response" + response);
                Model result = response.body();
                if (result != null) {
                    name.setText(result.getName());
                    intId.setText(String.valueOf(result.getId()));
                } else
                    System.out.print("hai result null");

                //
            }

            @Override
            public void onFailure(@NonNull Call<Model> call, @NonNull Throwable t) {
                Log.e("Sample", "retrofit", t);
            }
        });
    }
}
