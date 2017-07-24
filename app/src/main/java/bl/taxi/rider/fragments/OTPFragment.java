package bl.taxi.rider.fragments;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bl.taxi.rider.R;
import bl.taxi.rider.smsVerifier.OnSmsCatchListener;
import bl.taxi.rider.smsVerifier.SmsVerifyCatcher;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OTPFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OTPFragment extends Fragment {

    TextView otpCountDown, otpMobileNo, otpNumberEdit;

    SmsVerifyCatcher smsVerifyCatcher;

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

        otpCountDown = (TextView) otpFragment.findViewById(R.id.otp_count_down);
        otpMobileNo = (TextView) otpFragment.findViewById(R.id.otp_mobile_no);
        otpNumberEdit = (TextView) otpFragment.findViewById(R.id.otp_number_edit);

        otpMobileNo.setText(getArguments().getString("otp_number"));

        smsVerifyCatcher = new SmsVerifyCatcher(getActivity(), new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                otpNumberEdit.setText(code);//set code in edit text
                //then you can send verification code to server
            }
        });
        smsVerifyCatcher.setPhoneNumberFilter("VK-OLACAB");

        return otpFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new CountDownTimer(20000, 1000) {

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
}