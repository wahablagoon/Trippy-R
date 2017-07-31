package bl.taxi.rider.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.StringTokenizer;

import bl.taxi.rider.R;
import bl.taxi.rider.fragments.DestinationFragment;
import bl.taxi.rider.models.PlacesAutoComplete;

/**
 * Created by test on 31/7/17.
 */

public class PlacesAdapter extends BaseAdapter {
    private Context mContext;
    DestinationFragment destinationFragment;
    private List<PlacesAutoComplete> placesList;
    public PlacesAdapter(Context context, List<PlacesAutoComplete> placesList, DestinationFragment destinationFragment)

    {

        this.mContext = context;
        this.destinationFragment=destinationFragment;
        this.placesList = placesList;
    }


    @Override
    public int getCount() {
        return placesList.size();
    }

    @Override
    public Object getItem(int position) {
        return placesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class Holder
    {
        PlacesAutoComplete Place;
        TextView name, location;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Holder customViewHolder;
        if(view==null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(R.layout.places_autocomplete, parent, false);
            customViewHolder=new Holder();
            customViewHolder.name = (TextView) view.findViewById(R.id.place_name);
            customViewHolder.location = (TextView) view.findViewById(R.id.place_detail);
            view.setTag(customViewHolder);
        } else {
            customViewHolder = (Holder) view.getTag();
        }

        StringTokenizer st=new StringTokenizer(customViewHolder.Place.getPlaceDesc(), ",");

        customViewHolder.name.setText(st.nextToken());
        String desc_detail="";
        for(int i=1; i<st.countTokens(); i++) {
            if(i==st.countTokens()-1){
                desc_detail = desc_detail + st.nextToken();
            }else {
                desc_detail = desc_detail + st.nextToken() + ",";
            }
        }
        customViewHolder.location.setText(desc_detail);


        return view;
    }
}
