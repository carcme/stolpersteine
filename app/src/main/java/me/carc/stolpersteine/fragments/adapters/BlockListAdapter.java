package me.carc.stolpersteine.fragments.adapters;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Locale;

import me.carc.stolpersteine.R;
import me.carc.stolpersteine.data.remote.model.Stolpersteine;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class BlockListAdapter extends PagedListAdapter<Stolpersteine, BlockListAdapter.PoiListHolder> {

    public BlockListAdapter() {
        super(Stolpersteine.DIFF_CALLBACK);
    }

    @SuppressWarnings("Annotator")
    @Override
    public @NonNull
    PoiListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.block_list_item_layout, viewGroup, false);
        return new PoiListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PoiListHolder holder, int pos) {

        Stolpersteine item = getItem(pos);

        if (item == null) {
            Log.d("DEAD", "onBindViewHolder: ");
            return;
        }

        if (item.getPerson() == null) {
            Log.d("DEAD", "onBindViewHolder: ");
            return;
        }

        holder.name.setText(item.getPerson().getFullName());
        holder.desc.setText(item.getLocation().buildAddress());

        // find distance from my location
        if (item.isHasBiography())
            holder.hasBio.setVisibility(View.VISIBLE);
        else
            holder.hasBio.setVisibility(View.GONE);

        if (item.getNumImages() > 0) {
            holder.imageCount.setText(String.format(Locale.getDefault(), "%d x ", item.getNumImages()));
            holder.imageCount.setVisibility(View.VISIBLE);
        } else {
            holder.imageCount.setVisibility(View.GONE);
        }

        if (item.getImages() != null && !TextUtils.isEmpty(item.getImages().getThumbnail()))
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
     * View Holder
     */
    static class PoiListHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageView icon;
        TextView name;
        TextView desc;
        TextView hasBio;
        TextView imageCount;

        private PoiListHolder(View itemView) {
            super(itemView);
            mView = itemView;

            this.icon = itemView.findViewById(R.id.icon);
            this.name = itemView.findViewById(R.id.fullname);
            this.desc = itemView.findViewById(R.id.address);
            this.hasBio = itemView.findViewById(R.id.hasBio);
            this.imageCount = itemView.findViewById(R.id.imageCount);
        }
    }
}
