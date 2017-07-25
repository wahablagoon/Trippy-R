package bl.taxi.rider.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bl.taxi.rider.R;
import bl.taxi.rider.smsVerifier.OnSmsCatchListener;
import bl.taxi.rider.smsVerifier.SmsVerifyCatcher;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
    @BindView(R.id.button_next)
    Button buttonNext;
    @BindView(R.id.otp_count_down)
    TextView otpCountDown;
    Unbinder unbinder;

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

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        System.out.println("OTP Number" + getArguments().getString("otp_number"));
        //otpMobileNo.setText(getArguments().getString("otp_number"));

        smsVerifyCatcher = new SmsVerifyCatcher(getActivity(), new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                otpNumberEdit.setText(code);//set code in edit text
                otpNumberEdit.setSelection(code.length());
                //then you can send verification code to server
            }
        });
        smsVerifyCatcher.setPhoneNumberFilter("51465");

        unbinder = ButterKnife.bind(this, otpFragment);
        return otpFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OTP_countDownTimer = new CountDownTimer(25000, 1000) {

            public void onTick(long millisUntilFinished) {
                otpCountDown.setText(getResources().getQuantityString(R.plurals.numberOfSecondsRemaining,
                        (int) (millisUntilFinished / 1000), (int) (millisUntilFinished / 1000)));
            }

            public void onFinish() {
                //mTextField.setText("done!");
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
}