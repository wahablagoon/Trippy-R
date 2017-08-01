package bl.taxi.rider.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

import bl.taxi.rider.MapsActivity;
import bl.taxi.rider.R;
import bl.taxi.rider.fragments.DestinationFragment;
import bl.taxi.rider.models.placeautocomplete.PlacesAutoComplete;
import bl.taxi.rider.models.placeautocomplete.Prediction;

/*
 * Created by test on 31/7/17.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Prediction> placesList;
    private Fragment fragment;
    private FragmentActivity activity;

    private PlacesAutoComplete placesAutoComplete;

    public PlacesAdapter(Context context, ArrayList<Prediction> placesList, DestinationFragment fragment,
                         FragmentActivity activity) {

        this.mContext = context;
        this.placesList = placesList;
        this.fragment = fragment;
        this.activity = activity;
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
        final int holderPosition = position;
        System.out.println("Response in Adapter"+placesList.get(position).getDescription());
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
            holder.places_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MapsActivity mapsActivity = (MapsActivity) activity;
                    mapsActivity.dropText.setText(placesList.get(holderPosition).getDescription());
                    mapsActivity.selectedLocation = placesList.get(holderPosition);
                    mapsActivity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
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