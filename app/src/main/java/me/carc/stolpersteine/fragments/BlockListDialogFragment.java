package me.carc.stolpersteine.fragments;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import me.carc.stolpersteine.App;
import me.carc.stolpersteine.R;
import me.carc.stolpersteine.activities.viewer.BlockViewerActivity;
import me.carc.stolpersteine.common.utils.AndroidUtils;
import me.carc.stolpersteine.common.Commons;
import me.carc.stolpersteine.common.utils.MapUtils;
import me.carc.stolpersteine.common.utils.ViewUtils;
import me.carc.stolpersteine.data.db.blocks.StolpersteineViewModel;
import me.carc.stolpersteine.data.remote.model.Coordinates;
import me.carc.stolpersteine.fragments.adapters.BlockListAdapter;
import me.carc.stolpersteine.interfaces.RecyclerClickListener;
import me.carc.stolpersteine.interfaces.RecyclerTouchListener;
import me.carc.stolpersteine.ui.DividerItemDecoration;

import static android.view.View.GONE;

/**
 * Show the favorites list
 * Created by bamptonm on 31/08/2017.
 */
public class BlockListDialogFragment extends DialogFragment {

    private static final String TAG = BlockListDialogFragment.class.getName();
    public static final String ID_TAG = "MarkerListDialogFragment";

    public static final String BLOCK_POS  = "BLOCK_POS";
    public static final int RESULT_UPDATE_IMAGE = 1010;

    private static final String MY_LAT = "MY_LAT";
    private static final String MY_LNG = "MY_LNG";

    public static final int AUTOCOMPLETE_THRESHOLD = 3;

    private StolpersteineViewModel mViewModel;
    private BlockListAdapter adapter;
    private Unbinder unbinder;

//    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
//    @BindView(R.id.backdrop) ImageView imageBackDrop;
    @BindView(R.id.search_appbar) AppBarLayout search_appbar;
    @BindView(R.id.search_toolbar) Toolbar toolbar;
    @BindView(R.id.searchEditText) EditText searchEditText;
    @BindView(R.id.searchProgressBar) ProgressBar searchProgressBar;
    @BindView(R.id.clearButton) ImageButton clearButton;
    @BindView(R.id.settingButton) ImageButton settingButton;


    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindView(R.id.fabClose) FloatingActionButton fabClose;

    public static void showInstance(final Context appContext, final IGeoPoint currLocation) {

        AppCompatActivity activity = ((App) appContext).getCurrentActivity();

        try {
            Bundle bundle = new Bundle();

            if (currLocation != null) {
                bundle.putDouble(MY_LAT, currLocation.getLatitude());
                bundle.putDouble(MY_LNG, currLocation.getLongitude());
            }

            BlockListDialogFragment fragment = new BlockListDialogFragment();
            fragment.setArguments(bundle);
            fragment.show(activity.getSupportFragmentManager(), ID_TAG);

        } catch (RuntimeException ignored) { }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(STYLE_NO_FRAME, R.style.Dialog_Fullscreen);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimationSlide;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.block_list_recyclerview_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_grey_100));

        Bundle args = getArguments();
        if (args != null) {

            double lat = args.getDouble(MY_LAT, Double.NaN);
            double lng = args.getDouble(MY_LNG, Double.NaN);
            GeoPoint location = null;
            if (!Double.isNaN(lat) && !Double.isNaN(lng))
                location = new GeoPoint(lat, lng);

            configRecyclerView(location);

//            Drawable drawable = ViewUtils.changeIconColor(getContext(), R.drawable.ic_arrow_back, R.color.md_white_1000);
//            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(v -> dismiss());
        }
        return view;
    }

    private void configRecyclerView(GeoPoint location){
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);


        mViewModel = ViewModelProviders.of(this).get(StolpersteineViewModel.class);

        // Populate the paging adapter
        adapter = new BlockListAdapter(location);
        mViewModel.getPagedList().observe(this, adapter::submitList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerClickListener() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(getActivity(), BlockViewerActivity.class);
                intent.putExtra(BLOCK_POS, pos);
                intent.putExtra(BlockViewerActivity.BLOCK_DATA, adapter.getItemByBlockPos(pos));
                startActivityForResult(intent, RESULT_UPDATE_IMAGE);
            }

            @Override
            public void onLongClick(View view, int pos) {
                Log.d(TAG, "onLongClick: ");
            }
        }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_UPDATE_IMAGE:
                if(resultCode == Activity.RESULT_OK && data.hasExtra(BLOCK_POS)) {
                    int index = data.getIntExtra(BLOCK_POS, -1);
                    if(index != -1) {
                        adapter.notifyItemChanged(index);
                    }
                }
                break;
        }
    }

    private final RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0) {
                fabClose.hide();
                AndroidUtils.hideSoftKeyboard(getActivity(), searchEditText);
            } else
                fabClose.show();
        }
    };


    @OnClick(R.id.fabClose)
    void exit() {
        ViewUtils.createAlphaAnimator(fabClose, false, getResources()
                .getInteger(R.integer.gallery_alpha_duration) * 2)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }).start();
    }

    public void hide() {
        getDialog().hide();
    }

    public void show() {
        getDialog().show();
    }

    public void close() {
        dismiss();
    }

    private String getdistance(Coordinates node, GeoPoint location) {
        double d = MapUtils.getDistance(location, node.getLatitude(), node.getLongitude());
        return MapUtils.getFormattedDistance(d);
    }


    @OnClick(R.id.clearButton)
    void onClear() {
        mViewModel.getPagedList().observe(this, adapter::submitList);
        //reset values
        searchEditText.setText("");
        clearButton.setVisibility(View.GONE);
        // hide the keyboard ??
        AndroidUtils.hideSoftKeyboard(getActivity(), search_appbar);
    }

    @OnTextChanged(R.id.searchEditText)
    void searchEditChange(CharSequence s) {
        String newQueryText = s.toString();
        updateClearButtonVisibility(true);

        if (!Commons.isEmpty(newQueryText) && newQueryText.length() > AUTOCOMPLETE_THRESHOLD) {
            mViewModel.search(newQueryText.concat("%")).observe(this, stolpersteines -> adapter.submitList(stolpersteines));
        }
    }

    public void updateClearButtonVisibility(boolean show) {
        if (show) clearButton.setVisibility(searchEditText.length() > 0 ? View.VISIBLE : GONE);
        else clearButton.setVisibility(View.GONE);
    }

}
