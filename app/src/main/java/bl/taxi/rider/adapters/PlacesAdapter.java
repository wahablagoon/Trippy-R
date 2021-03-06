package bl.taxi.rider.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import bl.taxi.rider.MapsActivity;
import bl.taxi.rider.R;
import bl.taxi.rider.models.placeautocomplete.PlacesAutoComplete;
import bl.taxi.rider.models.placeautocomplete.Prediction;

/*
 * Created by test on 31/7/17.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private ArrayList<Prediction> placesList;
    private Activity activity;

    private PlacesAutoComplete placesAutoComplete;

    public PlacesAdapter(ArrayList<Prediction> placesList, Activity activity) {
        this.placesList = placesList;
        this.activity = activity;
    }

    public void setPlacesAutoComplete(PlacesAutoComplete placesAutoComplete) {
        this.placesAutoComplete = placesAutoComplete;
    }

    @Override
    public PlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (activity).getLayoutInflater();
        View view = inflater.inflate(R.layout.places_autocomplete, parent, false);
        return new PlacesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacesAdapter.ViewHolder holder, int position) {

        final Prediction prediction = placesList.get(position);

        if (placesAutoComplete.getStatus().equals("OK")) {

            holder.name.setText(prediction.getStructuredFormatting().getMainText());

            holder.location.setText(prediction.getStructuredFormatting().getSecondaryText());

            holder.places_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MapsActivity mapsActivity = (MapsActivity) activity;
                    mapsActivity.onPlaceSelected(mapsActivity.toolbarTitle.getText().toString(), prediction.getDescription(), prediction);
                    mapsActivity.onBackPressed();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, location;
        LinearLayout places_layout;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.place_name);
            location = itemView.findViewById(R.id.place_detail);
            places_layout = itemView.findViewById(R.id.view_layout);
        }
    }
}