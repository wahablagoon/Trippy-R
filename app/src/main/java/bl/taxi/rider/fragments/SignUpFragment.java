package bl.taxi.rider.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import bl.taxi.rider.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
}