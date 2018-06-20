package me.carc.stolpersteine.fragments.settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.stolpersteine.BuildConfig;
import me.carc.stolpersteine.R;
import me.carc.stolpersteine.common.Commons;

public class AboutFragment extends Fragment {

    public interface BtnClickListener {
        void onDonateClick();
    }
    BtnClickListener btnClickListener;

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        try {
            btnClickListener = (BtnClickListener) ctx;
        } catch (ClassCastException e) {
            throw new ClassCastException(ctx.toString() + " must implement BtnClickListener callbacks");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            btnClickListener = (BtnClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement BtnClickListener callbacks");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        Activity activity = getActivity();

        String versionName;
        try {
            versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "?.?";
        }

        if(BuildConfig.DEBUG)
            versionName += " " + BuildConfig.BUILD_TYPE;

        // add app name and version
        TextView aboutApp = view.findViewById(R.id.nameAndVersion);
        aboutApp.setText(String.format(getResources().getString(R.string.app_name_version), versionName));

        // create real paragraphs
        // TODO: 14/06/2018 change bugtracker link after uploading to github
        TextView t = view.findViewById(R.id.aboutTextView);
        t.setText(Html.fromHtml(
                getString(R.string.aboutSettingsDescription) +
                        String.format(getString(R.string.aboutSettingsIssueReporting), getString(R.string.bugtracker))
        ));

        // make links in about text clickable
        t.setMovementMethod(LinkMovementMethod.getInstance());
//        t.setLinkTextColor(ContextCompat.getColor(activity, R.color.colorAccent));

        return view;
    }

    @OnClick(R.id.websiteButton)
    void website() {
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website)));
        startActivity(launchBrowser);
    }

    @OnClick(R.id.shareApp)
    void share() {
        if(Commons.isNotNull(getActivity())) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, getString(R.string.shared_string_share)));
        }
    }

    @OnClick(R.id.rateButton)
    void rate() {
        new SendFeedback(getActivity(), SendFeedback.TYPE_RATE, SendFeedback.SHOW_RATE_NOW);
    }

    @OnClick(R.id.feedbackButton)
    void feebback() {
        new SendFeedback(getActivity(), SendFeedback.TYPE_FEEDBACK, SendFeedback.IGNORE_VALUE);
    }

    @OnClick(R.id.donateButton)
    void donate() {
        btnClickListener.onDonateClick();
    }
}