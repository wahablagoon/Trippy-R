package bl.taxi.rider.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import bl.taxi.rider.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
                break;
        }
    }
}