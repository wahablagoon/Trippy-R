package bl.taxi.rider.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import bl.taxi.rider.R;
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

    public VerificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verification, container, false);
        unbinder = ButterKnife.bind(this, view);
        mobileNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                /*if (s.length() != 0)
                    buttonClose.setVisibility(View.VISIBLE);
                else
                    buttonClose.setVisibility(View.GONE);*/

            }
        });

        return view;
    }

    @OnClick(R.id.button_close)
    public void one() {

        Toast.makeText(getActivity(), "Working", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
