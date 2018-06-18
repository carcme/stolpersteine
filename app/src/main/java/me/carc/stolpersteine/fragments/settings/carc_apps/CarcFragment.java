package me.carc.stolpersteine.fragments.settings.carc_apps;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.stolpersteine.R;
import me.carc.stolpersteine.common.Commons;

public class CarcFragment extends Fragment {

    private static final String TAG = CarcFragment.class.getName();
    public static final String TAG_ID = "CarcFragment";

    private final static String MARKET_REF = "&referrer=utm_source%3Dme.carc.btown";

    public interface ClickListener {
        void onClick(CarcAppsMenu item);
    }

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppThemeDark);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View rootView = localInflater.inflate(R.layout.carc_fragment, container, false);

        ButterKnife.bind(this, rootView);
        setRetainInstance(true);

        CarcAppsAdapter adapter = new CarcAppsAdapter(buildMenuItems(),
                item -> startActivity(openPlayStore(true, item.getUrlExtension())));

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    private List<CarcAppsMenu> buildMenuItems() {
        List<CarcAppsMenu> items = new LinkedList<>();
        items.add(CarcAppsMenu.BTOWN);
        items.add(CarcAppsMenu.FAKER);
        items.add(CarcAppsMenu.BBOOKS);
        items.add(CarcAppsMenu.ITIMER);
        items.add(CarcAppsMenu.AGD);

        return items;
    }

    /* PLAY STORE HELPERS */

    private String getMarketUrl(String urlExt) {
        return String.format(getString(R.string.carc_app_base_url), urlExt).concat(MARKET_REF);
    }

    @SuppressWarnings("SameParameterValue")
    private Intent openPlayStore(boolean openInBrowser, String urlExt) {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getMarketUrl(urlExt)));
        if (isIntentAvailable(marketIntent)) {
            return marketIntent;
        }
        if (openInBrowser) {
            return openLink(getMarketUrl(urlExt));
        }
        return marketIntent;
    }

    public boolean isIntentAvailable(Intent intent) {
        if(Commons.isNotNull(getActivity())) {
            PackageManager packageManager = getActivity().getPackageManager();
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }
        return false;
    }

    public Intent openLink(String url) {
        if (!TextUtils.isEmpty(url) && !url.contains("://"))
            url = "http://" + url;

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        return intent;
    }
}