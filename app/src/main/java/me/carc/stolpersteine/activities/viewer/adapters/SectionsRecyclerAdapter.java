package me.carc.stolpersteine.activities.viewer.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import me.carc.stolpersteine.R;
import me.carc.stolpersteine.common.utils.AndroidUtils;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class SectionsRecyclerAdapter extends RecyclerView.Adapter<SectionsRecyclerAdapter.ViewHolder> {

    private final ArrayList<SectionsCard> list;


    public SectionsRecyclerAdapter(ArrayList<SectionsCard> tours) {
        this.list = tours;
    }

    @Override
    @NonNull public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.section_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int pos) {

        Context ctx = holder.infoText.getContext();
        if(list.get(pos).getIcon() != -1) {
            holder.infoText.setCompoundDrawablesRelativeWithIntrinsicBounds(list.get(pos).getIcon(), 0, 0, 0);
            holder.infoText.setCompoundDrawablePadding(AndroidUtils.getPixelsFromDPs(ctx.getResources(), 16));
        } else {
            int padding = AndroidUtils.getPixelsFromDPs(ctx.getResources(), 32);
            holder.infoText.setPadding(padding, 0, 0, 0);
        }

        switch (list.get(pos).getType()) {
            case WEB:
                holder.infoText.setAutoLinkMask(Linkify.ALL);
                holder.infoText.setPaintFlags(holder.infoText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.infoText.setTextColor(ContextCompat.getColor(holder.infoText.getContext(), R.color.md_red_600));
                break;

            default:
        }

        holder.infoText.setText(list.get(pos).getDisplay());

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView infoText;

        ViewHolder(View v) {
            super(v);

            infoText = v.findViewById(R.id.infoText);
        }
    }
}
