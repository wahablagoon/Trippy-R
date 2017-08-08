package bl.taxi.rider.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {


    @BindView(R.id.button_back)
    ImageButton buttonBack;
    @BindView(R.id.mobile_no)
    TextView mobileNo;
    @BindView(R.id.input_full_name)
    MaterialEditText inputFullName;
    @BindView(R.id.input_email)
    MaterialEditText inputEmail;
    @BindView(R.id.button_register)
    Button buttonRegister;
    Unbinder unbinder;
    @BindView(R.id.create_account_text)
    TextView createAccountText;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OTPFragment.
     */
    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View signUpFragment = inflater.inflate(R.layout.fragment_signup, container, false);
        unbinder = ButterKnife.bind(this, signUpFragment);

        String mobileNumber = getArguments().getString("mobile_number");
        System.out.println("mobileNumber= " + mobileNumber);

        if (mobileNumber != null)
            createAccountText.setText(String.format(getString(R.string.enter_details_to_create_account_with), mobileNumber));// Updating resources in Strings.xml

        return signUpFragment;
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

    @OnClick(R.id.button_register)
    public void setButtonRegister() {
        String fullName = inputFullName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        if(fullName.length()==0)
        {
            inputFullName.setError("Enter your Full Name");
        }
        else if(!isValidEmail(email))
        {
            inputEmail.setError("Enter a valid email");
        }
        else
        {
            Toast.makeText(getActivity(), "Validation Success"+fullName, Toast.LENGTH_SHORT).show();
            /*buttonRegister.setEnabled(false);*/
            String numberOnly = createAccountText.getText().toString().trim();
            numberOnly= numberOnly.replaceAll("[^0-9]", "");
            createAccount(email,fullName,numberOnly);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void createAccount(String email,String fullName, String mobileNumber) {
        RetrofitAPI service = MyApplication.getService();
        Call<List<Model>> query = service.createAccount("1",fullName, mobileNumber,"91",email,"0");
        query.enqueue(new Callback<List<Model>>() {
            @Override
            public void onResponse(@NonNull Call<List<Model>> call, @NonNull Response<List<Model>> response) {
                System.out.println("response = " + response.body());
                buttonRegister.setEnabled(true);
                /*progressBar.setVisibility(View.GONE);*/
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
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            getActivity().finish();
                        }
                        else
                        {
                            buttonRegister.setEnabled(true);
                            Toast.makeText(getActivity(), "SignUp Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Model>> call, @NonNull Throwable t) {
                buttonRegister.setEnabled(true);
                /*progressBar.setVisibility(View.GONE);*/
                Log.e("Error", "retrofit", t);
            }
        });
    }

    public void savePreferences(String userID, String userName, String userEmail, String userMobile) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("user_id", userID);
        editor.putString("user_name", userName);
        editor.putString("user_email", userEmail);
        editor.putString("user_mobile", userMobile);
        editor.apply();
    }
}