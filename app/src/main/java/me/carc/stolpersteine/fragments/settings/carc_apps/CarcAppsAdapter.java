package me.carc.stolpersteine.fragments.settings.carc_apps;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.stolpersteine.R;


/**
 * Show other apps from Carc
 * Created by bamptonm on 20/01/2018.
 */

public class CarcAppsAdapter extends RecyclerView.Adapter<CarcAppsAdapter.ViewHolder> {

    private List<CarcAppsMenu> mMenuItems;
    private CarcFragment.ClickListener clickListener;
    
    public CarcAppsAdapter(List<CarcAppsMenu> items, CarcFragment.ClickListener listener){
        mMenuItems = items;
        clickListener = listener;
    }
    
    @SuppressWarnings("Annotator")
    @Override
    public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.carc_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder, final int position) {
        final CarcAppsMenu data = mMenuItems.get(position);

        holder.image.setImageResource(data.getIconDrawable());
        holder.title.setText(data.getTitleResourceId());
        holder.desc.setText(data.getSubTitleResourceId());
        holder.elementHolder.setOnClickListener(v -> clickListener.onClick(data));
    }

    @Override
    public int getItemCount() {
        return mMenuItems.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.appImage)
        ImageView image;
        @BindView(R.id.appTitle)
        TextView title;
        @BindView(R.id.appDesc)
        TextView desc;
        @BindView(R.id.card_view)
        CardView elementHolder;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
