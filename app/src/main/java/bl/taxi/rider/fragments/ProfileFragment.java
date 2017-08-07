package bl.taxi.rider.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import bl.taxi.rider.R;
import bl.taxi.rider.SplashIntroActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;
import static bl.taxi.rider.utils.Constants.MY_PREFS_NAME;

public class ProfileFragment extends Fragment {

    @BindView(R.id.mobile_edit_icon)
    ImageButton mobileEditIcon;
    @BindView(R.id.input_mobile)
    EditText textInputMobile;
    @BindView(R.id.name_edit_icon)
    ImageButton nameEditIcon;
    @BindView(R.id.text_input_name)
    EditText textInputName;
    @BindView(R.id.email_edit_icon)
    ImageButton emailEditIcon;
    @BindView(R.id.text_input_email)
    EditText textInputEmail;
    @BindView(R.id.password_edit_icon)
    ImageButton passwordEditIcon;
    @BindView(R.id.text_input_password)
    EditText textInputPassword;
    @BindView(R.id.button_log_out)
    Button logOut;
    Unbinder unbinder;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profileFragment = inflater.inflate(R.layout.fragment_profile, container, false);

        unbinder = ButterKnife.bind(this, profileFragment);
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String userName = prefs.getString("user_name", null);
        String userEmail = prefs.getString("user_email", null);
        String userMobile = prefs.getString("user_mobile", null);
        if (userName != null)
            textInputName.setText(userName);
        if (userEmail != null)
            textInputEmail.setText(userEmail);
        if (userMobile!= null)
            textInputMobile.setText(userMobile);
            textInputMobile.setSelection(textInputMobile.getText().length());//set selection to end
        return profileFragment;
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

    @OnClick({
            R.id.mobile_edit_icon,
            R.id.name_edit_icon,
            R.id.password_edit_icon,
            R.id.button_log_out
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mobile_edit_icon:
                textInputMobile.requestFocus();
                textInputMobile.setSelection(textInputMobile.getText().length());
                break;
            case R.id.name_edit_icon:
                textInputName.requestFocus();
                textInputName.setSelection(textInputName.getText().length());
                break;
            case R.id.password_edit_icon:
                textInputPassword.requestFocus();
                textInputPassword.setSelection(textInputPassword.getText().length());
                break;
            case R.id.button_log_out:
                clearPreferences();
                Intent intent = new Intent(getActivity(), SplashIntroActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }

    private void clearPreferences() { //Clear all saved preferences
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }
}