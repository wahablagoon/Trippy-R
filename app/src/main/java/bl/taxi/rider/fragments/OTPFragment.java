package bl.taxi.rider.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bl.taxi.rider.MapsActivity;
import bl.taxi.rider.R;
import bl.taxi.rider.controller.MyApplication;
import bl.taxi.rider.interfaces.RetrofitAPI;
import bl.taxi.rider.models.Model;
import bl.taxi.rider.smsVerifier.OnSmsCatchListener;
import bl.taxi.rider.smsVerifier.SmsVerifyCatcher;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OTPFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OTPFragment extends Fragment {

    SmsVerifyCatcher smsVerifyCatcher;

    CountDownTimer OTP_countDownTimer;
    @BindView(R.id.otp_button_back)
    ImageButton otpButtonBack;
    @BindView(R.id.otp_mobile_no)
    TextView otpMobileNo;
    @BindView(R.id.otp_number_edit)
    MaterialEditText otpNumberEdit;
    @BindView(R.id.button_log_in)
    Button buttonNext;
    @BindView(R.id.otp_count_down)
    TextView otpCountDown;
    Unbinder unbinder;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public OTPFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OTPFragment.
     */
    public static OTPFragment newInstance() {
        return new OTPFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View otpFragment = inflater.inflate(R.layout.fragment_otp, container, false);
        unbinder = ButterKnife.bind(this, otpFragment);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        otpNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()!=4) {
                    setEnabled(false, android.R.color.darker_gray);
                } else {
                    setEnabled(true, R.color.colorPrimary);
                }
            }
        });

        System.out.println("OTP Number" + getArguments().getString("otp_number"));
        //otpMobileNo.setText(getArguments().getString("otp_number"));

        smsVerifyCatcher = new SmsVerifyCatcher(getActivity(), new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                otpNumberEdit.setText(code);//set code in edit text
                otpNumberEdit.setSelection(code.length());
                //then you can send verification code to server
                verifyOTP(code);
            }
        });
        smsVerifyCatcher.setPhoneNumberFilter("51465");

        return otpFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OTP_countDownTimer = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                otpCountDown.setText(getResources().getQuantityString(R.plurals.numberOfSecondsRemaining,
                        (int) (millisUntilFinished / 1000), (int) (millisUntilFinished / 1000)));
            }

            public void onFinish() {
                otpCountDown.setVisibility(View.GONE);
            }
        }.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
        OTP_countDownTimer.cancel();
    }

    @Override
    public void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.otp_button_back)
    public void setButtonBack() {
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.button_log_in)
    public void setButtonNext() {
        buttonNext.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        String verifyOTPNumber = otpNumberEdit.getText().toString();
        //Send OTP to verify
        verifyOTP(verifyOTPNumber);
    }

    public void setEnabled(boolean bool, int colorID) {
        buttonNext.setEnabled(bool);
        buttonNext.setTextColor(ContextCompat.getColor(getActivity(), colorID));
    }

    public void verifyOTP(String verifyOTPNumber) {
        RetrofitAPI service = MyApplication.getService();
        Call<List<Model>> query = service.updateOTP("91", "9677772323",verifyOTPNumber);
        query.enqueue(new Callback<List<Model>>() {
            @Override
            public void onResponse(@NonNull Call<List<Model>> call, @NonNull Response<List<Model>> response) {
                buttonNext.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                List<Model> result = response.body();
                if (result != null && result.size() != 0) {
                    for (int i = 0; i < result.size(); i++) {
                        String responseStatus = result.get(i).getStatus();
                        if (responseStatus.matches("Success")) {
                            Intent intent = new Intent(getActivity(), MapsActivity.class);
                            startActivity(intent);
                        }
                        else {
                            buttonNext.setEnabled(true);
                            Toast.makeText(getActivity(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Model>> call, @NonNull Throwable t) {
                buttonNext.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Log.e("Error", "retrofit", t);
            }
        });
    }
}