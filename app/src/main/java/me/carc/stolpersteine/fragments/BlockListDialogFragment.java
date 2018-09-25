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
import android.widget.TextView;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import me.carc.stolpersteine.App;
import me.carc.stolpersteine.R;
import me.carc.stolpersteine.activities.viewer.BlockViewerActivity;
import me.carc.stolpersteine.common.Commons;
import me.carc.stolpersteine.common.utils.AndroidUtils;
import me.carc.stolpersteine.common.utils.MapUtils;
import me.carc.stolpersteine.common.utils.ViewUtils;
import me.carc.stolpersteine.common.views.RecyclerViewEmptySupport;
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
@SuppressWarnings("Annotator")
public class BlockListDialogFragment extends DialogFragment {

    private static final String TAG = BlockListDialogFragment.class.getName();
    public static final String ID_TAG = "MarkerListDialogFragment";

    public static final String BLOCK_POS  = "BLOCK_POS";
    public static final int RESULT_UPDATE_IMAGE = 1010;

    private static final String BBOX = "BOUNDING_BOX";

    public static final int AUTOCOMPLETE_THRESHOLD = 3;

    private StolpersteineViewModel mViewModel;
    private BlockListAdapter adapter;
    private Unbinder unbinder;

    @BindView(R.id.search_appbar) AppBarLayout search_appbar;
    @BindView(R.id.search_toolbar) Toolbar toolbar;
    @BindView(R.id.searchEditText) EditText searchEditText;
    @BindView(R.id.searchProgressBar) ProgressBar searchProgressBar;
    @BindView(R.id.clearButton) ImageButton clearButton;
    @BindView(R.id.settingButton) ImageButton settingButton;

    @BindView(R.id.recyclerview) RecyclerViewEmptySupport recyclerView;
    @BindView(R.id.emptyRecyclerView) TextView emptyRecyclerView;

    @BindView(R.id.fabClose) FloatingActionButton fabClose;

    public static void showInstance(final Context appContext, final BoundingBox boundingBox) {

        AppCompatActivity activity = ((App) appContext).getCurrentActivity();

        try {
            Bundle bundle = new Bundle();

            if (boundingBox != null) {
                bundle.putParcelable(BBOX, boundingBox);
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
        Objects.requireNonNull(getDialog().getWindow()).getAttributes().windowAnimations = R.style.DialogAnimationSlide;
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
        view.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.md_grey_100));

        Bundle args = getArguments();
        if (args != null) {
            configRecyclerView(args.getParcelable(BBOX));
            toolbar.setNavigationOnClickListener(v -> dismiss());
        }
        return view;
    }

    private void configRecyclerView(BoundingBox bbox){
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);


        mViewModel = ViewModelProviders.of(this).get(StolpersteineViewModel.class);

        // Populate the paging adapter
        adapter = new BlockListAdapter();
        mViewModel.getLocalPagedList(bbox).observe(this, adapter::submitList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEmptyView(emptyRecyclerView);

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
                        recyclerView.smoothScrollToPosition(index);
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
                AndroidUtils.hideSoftKeyboard(Objects.requireNonNull(getActivity()), searchEditText);
            } else
                fabClose.show();
        }
    };


    @OnClick(R.id.fabClose)
    void exit() {
        ViewUtils.createAlphaAnimator(fabClose, false, getResources()
                .getInteger(R.integer.gallery_alpha_duration) * 2)
                .withEndAction(this::dismiss).start();
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
        AndroidUtils.hideSoftKeyboard(Objects.requireNonNull(getActivity()), search_appbar);
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
