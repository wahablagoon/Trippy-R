package bl.taxi.rider.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.florent37.expectanim.ExpectAnim;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;

import bl.taxi.rider.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.github.florent37.expectanim.core.Expectations.belowOf;
import static com.github.florent37.expectanim.core.Expectations.centerInParent;

public class IntroFragment extends SlideFragment {


    @BindView(R.id.intro_title)
    TextView introTitle;
    @BindView(R.id.intro_description)
    TextView introDescription;
    Unbinder unbinder;

    public IntroFragment() {
        // Required empty public constructor
    }

    public static IntroFragment newInstance() {
        return new IntroFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro, container, false);
        unbinder = ButterKnife.bind(this, view);

            new ExpectAnim()
                    .expect(introTitle)
                    .toBe(centerInParent(true, true))
                    .expect(introDescription)
                    .toBe(centerInParent(true, true))
                    .toBe(belowOf(introTitle))
                    .toAnimation()
                    .setDuration(1500)
                    .start();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
