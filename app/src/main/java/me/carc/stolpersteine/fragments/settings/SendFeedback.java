package me.carc.stolpersteine.fragments.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import me.carc.stolpersteine.R;
import me.carc.stolpersteine.common.utils.IntentUtils;
import me.carc.stolpersteine.fragments.settings.helpers.FeedbackDialog;
import me.carc.stolpersteine.fragments.settings.helpers.RatingDialog;

/**
 * Holder for sending feedback info to Firebase
 * Created by bamptonm on 29/10/2017.
 */

public class SendFeedback {
    private static final String TAG = SendFeedback.class.getName();

    public static final int TYPE_FEEDBACK = 0;
    public static final int TYPE_RATE = 1;


    public SendFeedback(Context ctx, int type) {
        if(type == TYPE_FEEDBACK) {
            feedback(ctx);
        }  else if(type == TYPE_RATE) {
            rate(ctx);
        }
    }

    private void feedback(final Context ctx) {

        FeedbackDialog.Builder builder = new FeedbackDialog.Builder(ctx);
        builder.titleTextColor(R.color.md_black_1000);

        builder.formTitle(ctx.getString(R.string.shared_string_feedback));
        builder.formHint(ctx.getString(R.string.shared_string_add_comment));
        builder.allowEmpty(false);

        // Positive button
        builder.submitBtnText(ctx.getString(R.string.shared_string_send));
        builder.positiveBtnTextColor(R.color.md_blue_700);
        builder.positiveBtnBgColor(R.drawable.rate_button_selector_positive);
        builder.onSumbitClick(
                new FeedbackDialog.Builder.FeedbackDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        sendFeedBack(ctx, feedback);
                        Toast.makeText(ctx, R.string.shared_string_thankyou, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFormCancel() {
                    }
                });
        builder.build().show();
    }


    private void rate(final Context ctx) {
        final RatingDialog ratingDialog = new RatingDialog.Builder(ctx)
                .icon(ContextCompat.getDrawable(ctx, R.mipmap.ic_launcher))
                .threshold(3)
                .title(ctx.getString(R.string.ratings_request_title))
                .titleTextColor(R.color.md_black_1000)
                .formTitle(ctx.getString(R.string.feedback_request_title))
                .formHint(ctx.getString(R.string.feedback_request_hint))
                .formSubmitText(ctx.getString(R.string.rating_dialog_submit))
                .formCancelText(ctx.getString(R.string.rating_dialog_cancel))
                .ratingBarColor(R.color.colorAccent)

                .positiveButtonTextColor(R.color.colorAccent)
                .positiveButtonBackgroundColor(R.drawable.rate_button_selector_positive)

                .negativeButtonTextColor(R.color.colorPrimaryDark)
                .negativeButtonBackgroundColor(R.drawable.rate_button_selector_negative)
                .onThresholdCleared(new RatingDialog.Builder.RatingThresholdClearedListener() {
                    @Override
                    public void onThresholdCleared(RatingDialog dlg, float rating, boolean thresholdCleared) {

                        try {
                            ctx.startActivity(IntentUtils.openPlayStore(ctx));
                        } catch (ActivityNotFoundException ex) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                            builder.setTitle(R.string.shared_string_error);
                            builder.setMessage(R.string.error_playstore_not_found);
                            dlg.show();

                        }
                        dlg.dismiss();
                    }
                })

                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        sendFeedBack(ctx, feedback);
                        Toast.makeText(ctx, R.string.shared_string_thankyou, Toast.LENGTH_SHORT).show();
                    }
                }).build();

        ratingDialog.show();
    }


    private static void sendFeedBack(Context ctx, String text) {
        String[] address = new String[1];
        address[0] = ctx.getString(R.string.feedbackEmail);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",address[0], null));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address); // String[] addresses
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, ctx.getString(R.string.feedbackEmailSubject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        ctx.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
