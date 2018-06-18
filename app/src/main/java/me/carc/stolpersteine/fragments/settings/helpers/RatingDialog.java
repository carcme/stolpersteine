package me.carc.stolpersteine.fragments.settings.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import me.carc.stolpersteine.R;

/**
 * com.codemybrainsout.rating:ratingdialog
 * <p>
 * Created by ahulr on 24-10-2016.
 */

public class RatingDialog extends AppCompatDialog implements RatingBar.OnRatingBarChangeListener, View.OnClickListener {

    private static final String SESSION_COUNT = "session_count";
    private static final String SHOW_NEVER = "show_never";
    private String MyPrefs = "RatingDialog";
    private SharedPreferences sharedpreferences;

    private Context context;
    private Builder builder;
    private TextView tvTitle, tvNegative, tvPositive, tvFeedback, tvSubmit, tvCancel;
    private RatingBar ratingBar;
    private ImageView ivIcon;
    private EditText etFeedback;
    private LinearLayout ratingButtons, feedbackButtons;

    private float threshold;
    private int session;

    RatingDialog(Context context, Builder builder) {
        super(context);

        if(getWindow() != null)
            getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        this.context = context;
        this.builder = builder;

        this.session = builder.session;
        this.threshold = builder.threshold;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(getWindow() != null)
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.rating_dialog_rate);

        tvTitle = findViewById(R.id.dialog_rating_title);
        tvNegative = findViewById(R.id.dialog_rating_button_negative);
        tvPositive = findViewById(R.id.dialog_rating_button_positive);
        tvFeedback = findViewById(R.id.dialog_rating_feedback_title);
        tvSubmit = findViewById(R.id.dialog_rating_button_feedback_submit);
        tvCancel = findViewById(R.id.dialog_rating_button_feedback_cancel);
        ratingBar = findViewById(R.id.dialog_rating_rating_bar);
        ivIcon = findViewById(R.id.dialog_rating_icon);
        etFeedback = findViewById(R.id.dialog_rating_feedback);
        ratingButtons = findViewById(R.id.dialog_rating_buttons);
        feedbackButtons = findViewById(R.id.dialog_rating_feedback_buttons);

        init();
    }

    private void init() {

        tvTitle.setText(builder.title);
        tvPositive.setText(builder.positiveText);
        tvNegative.setText(builder.negativeText);

        tvFeedback.setText(builder.formTitle);
        tvSubmit.setText(builder.submitText);
        tvCancel.setText(builder.cancelText);
        etFeedback.setHint(builder.feedbackFormHint);

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        int color = typedValue.data;

        tvTitle.setTextColor(builder.titleTextColor != 0 ? ContextCompat.getColor(context, builder.titleTextColor) : ContextCompat.getColor(context, android.R.color.black));
        tvPositive.setTextColor(builder.positiveTextColor != 0 ? ContextCompat.getColor(context, builder.positiveTextColor) : color);
        tvNegative.setTextColor(builder.negativeTextColor != 0 ? ContextCompat.getColor(context, builder.negativeTextColor) : ContextCompat.getColor(context, R.color.md_grey_500));

        tvFeedback.setTextColor(builder.titleTextColor != 0 ? ContextCompat.getColor(context, builder.titleTextColor) : ContextCompat.getColor(context, android.R.color.black));
        tvSubmit.setTextColor(builder.positiveTextColor != 0 ? ContextCompat.getColor(context, builder.positiveTextColor) : color);
        tvCancel.setTextColor(builder.negativeTextColor != 0 ? ContextCompat.getColor(context, builder.negativeTextColor) : ContextCompat.getColor(context, R.color.md_blue_grey_500));

        if (builder.feedBackTextColor != 0) {
            etFeedback.setTextColor(ContextCompat.getColor(context, builder.feedBackTextColor));
        }

        if (builder.positiveBackgroundColor != 0) {
            tvPositive.setBackgroundResource(builder.positiveBackgroundColor);
            tvSubmit.setBackgroundResource(builder.positiveBackgroundColor);

        }
        if (builder.negativeBackgroundColor != 0) {
            tvNegative.setBackgroundResource(builder.negativeBackgroundColor);
            tvCancel.setBackgroundResource(builder.negativeBackgroundColor);
        }

        if (builder.ratingBarColor != 0) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(ContextCompat.getColor(context, builder.ratingBarColor), PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(1).setColorFilter(ContextCompat.getColor(context, builder.ratingBarColor), PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(0).setColorFilter(ContextCompat.getColor(context, R.color.md_grey_200), PorterDuff.Mode.SRC_ATOP);
            } else {
                Drawable stars = ratingBar.getProgressDrawable();
                DrawableCompat.setTint(stars, ContextCompat.getColor(context, builder.ratingBarColor));
            }
        }

        Drawable d = context.getPackageManager().getApplicationIcon(context.getApplicationInfo());
        ivIcon.setImageDrawable(builder.drawable != null ? builder.drawable : d);

        ratingBar.setOnRatingBarChangeListener(this);
        tvPositive.setOnClickListener(this);
        tvNegative.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        if (session == 1) {
            tvNegative.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.dialog_rating_button_negative) {

            dismiss();
            showNever();

        } else if (view.getId() == R.id.dialog_rating_button_positive) {

            dismiss();

        } else if (view.getId() == R.id.dialog_rating_button_feedback_submit) {

            String feedback = etFeedback.getText().toString().trim();
            if (TextUtils.isEmpty(feedback)) {

                Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
                etFeedback.startAnimation(shake);
                return;
            }

            if (builder.ratingDialogFormListener != null) {
                builder.ratingDialogFormListener.onFormSubmitted(feedback);
            }

            dismiss();
            showNever();

        } else if (view.getId() == R.id.dialog_rating_button_feedback_cancel) {

            dismiss();

        }

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

        boolean thresholdPassed = true;

        if (ratingBar.getRating() >= threshold) {
            thresholdPassed = true;

            if (builder.ratingThresholdClearedListener == null) {
                setRatingThresholdClearedListener();
            }
            builder.ratingThresholdClearedListener.onThresholdCleared(this, ratingBar.getRating(), thresholdPassed);

        } else {
            thresholdPassed = false;

            if (builder.ratingThresholdFailedListener == null) {
                setRatingThresholdFailedListener();
            }
            builder.ratingThresholdFailedListener.onThresholdFailed(this, ratingBar.getRating(), thresholdPassed);
        }

        if (builder.ratingDialogListener != null) {
            builder.ratingDialogListener.onRatingSelected(ratingBar.getRating(), thresholdPassed);
        }
        showNever();
    }

    private void setRatingThresholdClearedListener() {
        builder.ratingThresholdClearedListener = new Builder.RatingThresholdClearedListener() {
            @Override
            public void onThresholdCleared(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                openPlaystore(context);
                dismiss();
            }
        };
    }

    private void setRatingThresholdFailedListener() {
        builder.ratingThresholdFailedListener = new Builder.RatingThresholdFailedListener() {
            @Override
            public void onThresholdFailed(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                openForm();
            }
        };
    }

    private void openForm() {
        ratingBar.setVisibility(View.GONE);
        ratingButtons.setVisibility(View.GONE);
        ivIcon.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        tvFeedback.setVisibility(View.VISIBLE);
        etFeedback.setVisibility(View.VISIBLE);
        feedbackButtons.setVisibility(View.VISIBLE);
    }

    private void openPlaystore(Context context) {
        final Uri marketUri = Uri.parse("market://details?id=" + context.getPackageName());
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Couldn't find PlayStore on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("unused")
    public TextView getTitleTextView() {
        return tvTitle;
    }

    @SuppressWarnings("unused")
    public TextView getPositiveButtonTextView() {
        return tvPositive;
    }

    @SuppressWarnings("unused")
    public TextView getNegativeButtonTextView() {
        return tvNegative;
    }

    @SuppressWarnings("unused")
    public TextView getFormTitleTextView() {
        return tvFeedback;
    }

    @SuppressWarnings("unused")
    public TextView getFormSumbitTextView() {
        return tvSubmit;
    }

    @SuppressWarnings("unused")
    public TextView getFormCancelTextView() {
        return tvCancel;
    }

    @SuppressWarnings("unused")
    public ImageView getIconImageView() {
        return ivIcon;
    }

    @SuppressWarnings("unused")
    public RatingBar getRatingBarView() {
        return ratingBar;
    }

    @Override
    public void show() {

        if (checkIfSessionMatches(session)) {
            super.show();
        }
    }

    private boolean checkIfSessionMatches(int session) {

        if (session == 1) {
            return true;
        }

        sharedpreferences = context.getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);

        if (sharedpreferences.getBoolean(SHOW_NEVER, false)) {
            return false;
        }

        int count = sharedpreferences.getInt(SESSION_COUNT, 1);

        if (session == count) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(SESSION_COUNT, 1);
            editor.apply();
            return true;
        } else if (session > count) {
            count++;
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(SESSION_COUNT, count);
            editor.apply();
            return false;
        } else {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(SESSION_COUNT, 2);
            editor.apply();
            return false;
        }
    }

    private void showNever() {
        sharedpreferences = context.getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(SHOW_NEVER, true);
        editor.apply();
    }

    public static class Builder {

        private final Context context;
        private String title, positiveText, negativeText;
        private String formTitle, submitText, cancelText, feedbackFormHint;
        private int positiveTextColor, negativeTextColor, titleTextColor, ratingBarColor, feedBackTextColor;
        private int positiveBackgroundColor, negativeBackgroundColor;
        private RatingThresholdClearedListener ratingThresholdClearedListener;
        private RatingThresholdFailedListener ratingThresholdFailedListener;
        private RatingDialogFormListener ratingDialogFormListener;
        private RatingDialogListener ratingDialogListener;
        private Drawable drawable;

        private int session = 1;
        private float threshold = 1;

        public interface RatingThresholdClearedListener {
            void onThresholdCleared(RatingDialog ratingDialog, float rating, boolean thresholdCleared);
        }

        public interface RatingThresholdFailedListener {
            void onThresholdFailed(RatingDialog ratingDialog, float rating, boolean thresholdCleared);
        }

        public interface RatingDialogFormListener {
            void onFormSubmitted(String feedback);
        }

        public interface RatingDialogListener {
            void onRatingSelected(float rating, boolean thresholdCleared);
        }

        public Builder(Context context) {
            this.context = context;
            initText();
        }

        private void initText() {
            title = context.getString(R.string.rating_dialog_experience);
            positiveText = context.getString(R.string.rating_dialog_maybe_later);
            negativeText = context.getString(R.string.rating_dialog_never);
            formTitle = context.getString(R.string.rating_dialog_feedback_title);
            submitText = context.getString(R.string.rating_dialog_submit);
            cancelText = context.getString(R.string.rating_dialog_cancel);
            feedbackFormHint = context.getString(R.string.rating_dialog_suggestions);
        }

        @SuppressWarnings("unused")
        public Builder session(int session) {
            this.session = session;
            return this;
        }

        public Builder threshold(float threshold) {
            this.threshold = threshold;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /*public Builder icon(int icon) {
            this.icon = icon;
            return this;
        }*/

        public Builder icon(Drawable drawable) {
            this.drawable = drawable;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder positiveButtonText(String positiveText) {
            this.positiveText = positiveText;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder negativeButtonText(String negativeText) {
            this.negativeText = negativeText;
            return this;
        }

        public Builder titleTextColor(int titleTextColor) {
            this.titleTextColor = titleTextColor;
            return this;
        }

        public Builder positiveButtonTextColor(int positiveTextColor) {
            this.positiveTextColor = positiveTextColor;
            return this;
        }

        public Builder negativeButtonTextColor(int negativeTextColor) {
            this.negativeTextColor = negativeTextColor;
            return this;
        }

        public Builder positiveButtonBackgroundColor(int positiveBackgroundColor) {
            this.positiveBackgroundColor = positiveBackgroundColor;
            return this;
        }

        public Builder negativeButtonBackgroundColor(int negativeBackgroundColor) {
            this.negativeBackgroundColor = negativeBackgroundColor;
            return this;
        }

        public Builder onThresholdCleared(RatingThresholdClearedListener ratingThresholdClearedListener) {
            this.ratingThresholdClearedListener = ratingThresholdClearedListener;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder onThresholdFailed(RatingThresholdFailedListener ratingThresholdFailedListener) {
            this.ratingThresholdFailedListener = ratingThresholdFailedListener;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder onRatingChanged(RatingDialogListener ratingDialogListener) {
            this.ratingDialogListener = ratingDialogListener;
            return this;
        }

        public Builder onRatingBarFormSumbit(RatingDialogFormListener ratingDialogFormListener) {
            this.ratingDialogFormListener = ratingDialogFormListener;
            return this;
        }

        public Builder formTitle(String formTitle) {
            this.formTitle = formTitle;
            return this;
        }

        public Builder formHint(String formHint) {
            this.feedbackFormHint = formHint;
            return this;
        }

        public Builder formSubmitText(String submitText) {
            this.submitText = submitText;
            return this;
        }

        public Builder formCancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public Builder ratingBarColor(int ratingBarColor) {
            this.ratingBarColor = ratingBarColor;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder feedbackTextColor(int feedBackTextColor) {
            this.feedBackTextColor = feedBackTextColor;
            return this;
        }

        public RatingDialog build() {
            return new RatingDialog(context, this);
        }
    }
}
