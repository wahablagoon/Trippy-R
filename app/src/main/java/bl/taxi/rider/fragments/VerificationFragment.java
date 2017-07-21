package bl.taxi.rider.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
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
 */
public class VerificationFragment extends Fragment {


    @BindView(R.id.button_back)
    ImageButton buttonBack;
    @BindView(R.id.mobile_number)
    MaterialEditText mobileNumber;
    @BindView(R.id.button_next)
    Button buttonNext;
    Unbinder unbinder;
    @BindView(R.id.button_close)
    ImageButton buttonClose;

    SmsVerifyCatcher smsVerifyCatcher;

    public VerificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verification, container, false);
        unbinder = ButterKnife.bind(this, view);

        smsVerifyCatcher = new SmsVerifyCatcher(getActivity(), new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                mobileNumber.setText(code);//set code in edit text
                //then you can send verification code to server
            }
        });
        smsVerifyCatcher.setPhoneNumberFilter("VK-OLACAB");

        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValidNumber()) {
                    mobileNumber.setError(getString(R.string.enter_a_valid_mobile_number));
                    setenabled(false, android.R.color.darker_gray);
                } else {
                    setenabled(true, R.color.colorPrimary);
                }
            }
        });

        return view;
    }

    @OnClick(R.id.button_back)
    public void setButtonBack() {
        getActivity().finish();
    }

    @OnClick(R.id.button_next)
    public void setButtonNext() {
        Toast.makeText(getActivity(), "Button Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
    public void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    private boolean isValidNumber() {
        String inputNumber = mobileNumber.getText().toString();
        if (inputNumber.length() <= 1) {
            return false;
        } else {
            if (validatePhone()) {
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(91);
                Phonenumber.PhoneNumber phoneNumber = null;

                try {
                    phoneNumber = phoneNumberUtil.parse(inputNumber, isoCode);
                } catch (NumberParseException e) {
                    System.err.println(e);
                }

                boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
                if (isValid) {
                    return true;
                } else {
                    return false;
                }
            }
            setenabled(true, R.color.colorPrimary);
            return true;
        }
    }

    private boolean validatePhone() {
        if (mobileNumber.getText().toString().trim().isEmpty()) {
            mobileNumber.setError(getString(R.string.enter_mobile_number));
            return false;
        } else if (!mobileNumber.getText().toString().trim().isEmpty()) {
            if (mobileNumber.getText().toString().substring(0, 1).matches("0")) {
                mobileNumber.setError("Enter a valid number");
                return false;
            } else {
                int maxLengthofEditText = 10;
                mobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengthofEditText)});
                mobileNumber.setError(null);
            }
            return true;
        }

        return true;
    }

    public void setenabled(boolean bool, int colorID) {
        buttonNext.setEnabled(bool);
        buttonNext.setTextColor(ContextCompat.getColor(getActivity(), colorID));
    }

}
