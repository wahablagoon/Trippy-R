package bl.taxi.rider.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import bl.taxi.rider.MapsActivity;
import bl.taxi.rider.R;
import bl.taxi.rider.controller.MyApplication;
import bl.taxi.rider.interfaces.RetrofitAPI;
import bl.taxi.rider.models.Model;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static bl.taxi.rider.utils.Constants.MY_PREFS_NAME;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordFragment extends Fragment {


    @BindView(R.id.button_back)
    ImageButton buttonBack;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.verify_text)
    TextView verifyText;
    @BindView(R.id.input_password)
    MaterialEditText inputPassword;
    @BindView(R.id.forget_password)
    TextView forgetPassword;
    @BindView(R.id.button_log_in)
    Button buttonLogIn;
    Unbinder unbinder;

    String mobileNumber;

    public PasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OTPFragment.
     */
    public static PasswordFragment newInstance() {
        return new PasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View passwordFragment = inflater.inflate(R.layout.fragment_password, container, false);
        unbinder = ButterKnife.bind(this, passwordFragment);

        mobileNumber = getArguments().getString("mobile_number");
        System.out.println("mobileNumber= " + mobileNumber);

        if (mobileNumber != null)
            verifyText.setText(String.format(getString(R.string.enter_password), mobileNumber));// Updating resources in Strings.xml

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()<=5) {
                    setEnabled(false, android.R.color.darker_gray);
                } else {
                    setEnabled(true, R.color.colorPrimary);
                }
            }
        });

        return passwordFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.button_back)
    public void setButtonBack() {
        getFragmentManager().popBackStack();
    }

    public void savePreferences(String userID, String userName, String userEmail, String userMobile) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("user_id", userID);
        editor.putString("user_name", userName);
        editor.putString("user_email", userEmail);
        editor.putString("user_mobile", userMobile);
        editor.apply();
    }

    @OnClick(R.id.button_log_in)
    public void setButtonNext() {
        buttonLogIn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        String getPassword = inputPassword.getText().toString();
        //Send OTP to verify
        verifyPassword(getPassword);
    }

    private void verifyPassword(String getPassword) {
        RetrofitAPI service = MyApplication.getService();
        Call<List<Model>> query = service.verifyUser("1",mobileNumber,"91", getPassword);
        query.enqueue(new Callback<List<Model>>() {
            @Override
            public void onResponse(@NonNull Call<List<Model>> call, @NonNull Response<List<Model>> response) {
                System.out.println("response = " + response);
                buttonLogIn.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                List<Model> result = response.body();
                if (result != null && result.size() != 0) {
                    for (int i = 0; i < result.size(); i++) {
                        String responseStatus = result.get(i).getStatus();
                        if (responseStatus.matches("Success")) {
                            String userID = String.valueOf(result.get(i).getUserid());
                            String userName = result.get(i).getName();
                            String userEmail = result.get(i).getEmail();
                            String userMobile = result.get(i).getPhone();
                            savePreferences(userID, userName, userEmail, userMobile); //save the details in shared preferences
                            Intent intent = new Intent(getActivity(), MapsActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                        else
                        {
                            buttonLogIn.setEnabled(true);
                            Toast.makeText(getActivity(), "Invalid Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Model>> call, @NonNull Throwable t) {
                buttonLogIn.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Log.e("Error", "retrofit", t);
            }
        });

    }

    public void setEnabled(boolean bool, int colorID) {
        buttonLogIn.setEnabled(bool);
        buttonLogIn.setTextColor(ContextCompat.getColor(getActivity(), colorID));
    }
}