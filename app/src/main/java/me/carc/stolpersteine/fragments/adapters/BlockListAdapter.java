package me.carc.stolpersteine.fragments.adapters;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.osmdroid.util.GeoPoint;

import me.carc.stolpersteine.R;
import me.carc.stolpersteine.common.utils.MapUtils;
import me.carc.stolpersteine.data.remote.model.Coordinates;
import me.carc.stolpersteine.data.remote.model.Stolpersteine;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class BlockListAdapter extends PagedListAdapter<Stolpersteine, BlockListAdapter.PoiListHolder> {

    private GeoPoint currentLocation;

    private SparseArray<TextView> distanceArray = new SparseArray<>();

    public BlockListAdapter(GeoPoint loc) {
        super(Stolpersteine.DIFF_CALLBACK);
        currentLocation = loc;
    }

    @Override
    public @NonNull PoiListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.block_list_item_layout, viewGroup, false);
        return new PoiListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PoiListHolder holder, int pos) {

        Stolpersteine item = getItem(pos);

        if(item == null) {
            Log.d("DEAD", "onBindViewHolder: ");
            return;
        }

        if(item.getPerson() == null) {
            Log.d("DEAD", "onBindViewHolder: ");
            return;
        }

        holder.name.setText(item.getPerson().getFullName());
        holder.desc.setText(item.getLocation().buildAddress());

        // find distance from my location
        holder.distance.setText(String.valueOf(item.getBlockId()));
        distanceArray.put(pos, holder.distance);

        if(item.getImages() != null && !TextUtils.isEmpty(item.getImages().getThumbnail()))
            Glide.with(holder.mView.getContext())
                .load(item.getImages().getThumbnail())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.icon);
        else
            Glide.with(holder.mView.getContext())
                    .load(R.drawable.fb_stolpersteine)
                    .into(holder.icon);

    }


    public Stolpersteine getItemByBlockPos(int pos) {
        return getItem(pos);
    }

    /**
     * Calculate the distance to osmNode
     *
     * @param node the node
     * @return formatted distance
     */
    private String getdistance(Stolpersteine node) {
        Coordinates ords = node.getLocation().getCoordinates();
        double d = MapUtils.getDistance(currentLocation, ords .getLatitude(), ords.getLongitude());
        return MapUtils.getFormattedDistance(d);
    }

    /**
     * View Holder
     */
    static class PoiListHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageView icon;
        TextView name;
        TextView desc;
        LinearLayout compassHolder;
        TextView distance;

        private PoiListHolder(View itemView) {
            super(itemView);
            mView = itemView;

            this.icon = itemView.findViewById(R.id.icon);
            this.name = itemView.findViewById(R.id.fullname);
            this.desc = itemView.findViewById(R.id.address);
            this.distance = itemView.findViewById(R.id.distance);
            this.compassHolder =itemView.findViewById(R.id.rhsHolder);
        }
    }
}
