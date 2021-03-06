package bl.taxi.rider.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

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

    String strMobileNumber;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public VerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verification, container, false);
        unbinder = ButterKnife.bind(this, view);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

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
                    setEnabled(false, android.R.color.darker_gray);
                } else {
                    setEnabled(true, R.color.colorPrimary);
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
        buttonNext.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        //save mobile number for passing it to next window
        strMobileNumber = mobileNumber.getText().toString();

        RetrofitAPI service = MyApplication.getService();
        Call<List<Model>> query = service.sendOTP("91", strMobileNumber);
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
                                OTPFragment otpFragment = OTPFragment.newInstance();
                                Bundle arguments = new Bundle();
                                arguments.putString("mobile_number", strMobileNumber);
                                otpFragment.setArguments(arguments);
                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction().addToBackStack(null)
                                        .replace(R.id.content_frame, otpFragment).commitAllowingStateLoss();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private boolean isValidNumber() {
        String inputNumber = mobileNumber.getText().toString();
        if (inputNumber.length() <= 1) {
            return false;
        }
        if (validatePhone()) {
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(91);
                Phonenumber.PhoneNumber phoneNumber = null;

                try {
                    phoneNumber = phoneNumberUtil.parse(inputNumber, isoCode);
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }

                return phoneNumberUtil.isValidNumber(phoneNumber);
            }
            setEnabled(true, R.color.colorPrimary);
            return true;
    }

    private boolean validatePhone() {
        if (mobileNumber.getText().toString().trim().isEmpty()) {
            return false;
        } else if (!mobileNumber.getText().toString().trim().isEmpty()) {
            if (mobileNumber.getText().toString().substring(0, 1).matches("0")) {
                return false;
            } else {
                int maxLengthOfEditText = 10;
                mobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengthOfEditText)});
                mobileNumber.setError(null);
            }
            return true;
        }

        return true;
    }

    public void setEnabled(boolean bool, int colorID) {
        buttonNext.setEnabled(bool);
        buttonNext.setTextColor(ContextCompat.getColor(getActivity(), colorID));
    }
}