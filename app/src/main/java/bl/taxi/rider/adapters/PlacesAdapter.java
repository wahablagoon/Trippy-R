package bl.taxi.rider.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.StringTokenizer;

import bl.taxi.rider.R;
import bl.taxi.rider.fragments.DestinationFragment;
import bl.taxi.rider.models.placeautocomplete.PlacesAutoComplete;
import bl.taxi.rider.models.placeautocomplete.Prediction;

/*
 * Created by test on 31/7/17.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    DestinationFragment destinationFragment;
    private Context mContext;
    private List<Prediction> placesList;

    private PlacesAutoComplete placesAutoComplete;

    public PlacesAdapter(Context context, List<Prediction> placesList,
                         DestinationFragment destinationFragment) {

        this.mContext = context;
        this.destinationFragment = destinationFragment;
        this.placesList = placesList;
    }

    public PlacesAutoComplete getPlacesAutoComplete() {
        return placesAutoComplete;
    }

    public void setPlacesAutoComplete(PlacesAutoComplete placesAutoComplete) {
        this.placesAutoComplete = placesAutoComplete;
    }

    @Override
    public PlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.places_autocomplete, parent, false);
        return new PlacesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacesAdapter.ViewHolder holder, int position) {
        if (placesAutoComplete.getStatus().equals("OK")) {
            StringTokenizer st = new StringTokenizer(placesList.get(position).getDescription(), ",");

            holder.name.setText(st.nextToken());
            String desc_detail = "";
            for (int i = 1; i < st.countTokens(); i++) {
                if (i == st.countTokens() - 1) {
                    desc_detail = desc_detail + st.nextToken();
                } else {
                    desc_detail = desc_detail + st.nextToken() + ",";
                }
            }
            holder.location.setText(desc_detail);
            holder.txtlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

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
        LinearLayout txtlay;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.place_name);
            location = itemView.findViewById(R.id.place_detail);
            txtlay = itemView.findViewById(R.id.txtlay);
        }
    }
}