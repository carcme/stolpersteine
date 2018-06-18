package me.carc.stolpersteine.interfaces;

import android.view.View;

/**
 * Created by bamptonm on 05/09/2017.
 */

public interface RecyclerClickListener {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}
