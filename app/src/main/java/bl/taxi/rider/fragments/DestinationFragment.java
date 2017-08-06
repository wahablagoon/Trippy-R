package bl.taxi.rider.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import bl.taxi.rider.R;
import bl.taxi.rider.adapters.PlacesAdapter;
import bl.taxi.rider.controller.MyApplication;
import bl.taxi.rider.interfaces.RetrofitAPI;
import bl.taxi.rider.models.placeautocomplete.PlacesAutoComplete;
import bl.taxi.rider.models.placeautocomplete.Prediction;
import bl.taxi.rider.utils.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationFragment extends Fragment implements TextWatcher, Callback<PlacesAutoComplete> {

    Unbinder unbinder;
    @BindView(R.id.input_search_places)
    EditText inputSearchPlaces;
    @BindView(R.id.input_places_clear)
    ImageButton inputPlacesClear;
    @BindView(R.id.places_list_view)
    RecyclerView placesListView;

    private RetrofitAPI retrofitAPI;
    private PlacesAdapter placesAdapter;
    private ArrayList<Prediction> placesList = new ArrayList<>();
    private String mCurrentLocation;

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
        MyApplication.setCacheInstance(Constants.AUTO_COMPLETE_URL);
        retrofitAPI = MyApplication.getCacheService();
        unbinder = ButterKnife.bind(this, view);

        mCurrentLocation = getArguments().getString("strLocation");
        if (mCurrentLocation == null)
            mCurrentLocation = "";

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        placesListView.setLayoutManager(linearLayoutManager);
        placesAdapter = new PlacesAdapter(placesList, this, getActivity());
        placesListView.setAdapter(placesAdapter);
        inputSearchPlaces.addTextChangedListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        getPlacesList(inputSearchPlaces.getText().toString().trim());
    }

    private void getPlacesList(String query) {
        Call<PlacesAutoComplete> autoCompleteCall = retrofitAPI.getPlaces(query, mCurrentLocation,
                Constants.GOOGLE_API_KEY, "5000");
        autoCompleteCall.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<PlacesAutoComplete> call, @NonNull Response<PlacesAutoComplete> response) {
        PlacesAutoComplete placesAutoComplete = response.body();
        if (placesAutoComplete == null) {
            return;
        }
        if (placesAutoComplete.getStatus().equals("OK")) {
            placesAdapter.setPlacesAutoComplete(placesAutoComplete);
            placesList.clear();
            placesList.addAll(placesAutoComplete.getPredictions());
            placesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(@NonNull Call<PlacesAutoComplete> call, @NonNull Throwable t) {
        t.printStackTrace();
    }

    @OnClick(R.id.input_places_clear)
    public void onViewClicked() {
        inputSearchPlaces.setText("");
        placesList.clear();
        placesAdapter.notifyDataSetChanged();
    }
}