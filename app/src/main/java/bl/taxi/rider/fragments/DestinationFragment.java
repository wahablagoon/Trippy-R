package bl.taxi.rider.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import bl.taxi.rider.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationFragment extends Fragment {

    @BindView(R.id.backButton)
    ImageButton backButton;
    @BindView(R.id.places_text)
    EditText placesText;
    @BindView(R.id.closeButton)
    ImageButton closeButton;
    @BindView(R.id.places_view)
    RecyclerView placesView;
    Unbinder unbinder;

    public DestinationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment DestinationFragment.
     */
    public static DestinationFragment newInstance() {
        return new DestinationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_destination, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}