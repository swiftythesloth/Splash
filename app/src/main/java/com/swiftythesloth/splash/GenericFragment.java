package com.swiftythesloth.splash;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * Created by Sam on 07/08/2015.
 */
public class GenericFragment extends DialogFragment {

    private static final String LOG_TAG = GenericFragment.class.getSimpleName();

    private static final int INSTRUCTIONS_GRID_SIZE = 4;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView;
        TextView ok;

        switch ((Constants.DialogFragmentType) getArguments().getSerializable(Constants.DIALOG_FRAGMENT_TYPE)) {
            case PURCHASE_SUCCESSFUL:
                rootView = getActivity().getLayoutInflater()
                        .inflate(R.layout.fragment_purchase, null);
                setupPurchase(rootView, true);
                ok = (TextView) rootView.findViewById(R.id.dialog_purchase_ok);
                break;
            case PURCHASE_UNSUCCESSFUL:
                rootView = getActivity().getLayoutInflater()
                        .inflate(R.layout.fragment_purchase, null);
                setupPurchase(rootView, false);
                ok = (TextView) rootView.findViewById(R.id.dialog_purchase_ok);
                break;
            case RATING:
                rootView = getActivity().getLayoutInflater()
                        .inflate(R.layout.fragment_rating, null);
                setupRatingFragment(rootView);
                ok = (TextView) rootView.findViewById(R.id.rating_never);
                break;
            default:
                rootView = null;
                ok = null;
        }
        builder.setView(rootView);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.menu_icon_bounce));
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null) {
            return;
        }

        getDialog().getWindow().setWindowAnimations(R.style.dialog_animations);
    }

    /**
     * Re-enables immersive mode when returning to the activity
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setupPurchase(View rootView, boolean isSuccess) {
        TextView title = (TextView) rootView.findViewById(R.id.dialog_purchase_title);
        TextView reason = (TextView) rootView.findViewById(R.id.dialog_purchase_text);

        if (isSuccess) {
            title.setText(getResources().getString(R.string.dialog_purchase_successful_title));
            reason.setVisibility(View.GONE);
        } else {
            title.setText(getResources().getString(R.string.dialog_purchase_unsuccessful_title));
            reason.setText(getResources().getString(R.string.dialog_purchase_unsuccessful_reason));
        }
    }

    private void setupRatingFragment(View rootView) {
        TextView goToStore = (TextView) rootView.findViewById(R.id.rating_go_play_store);

        goToStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                        "market://details?id=com.swiftythesloth.splash")));
                dismiss();
            }
        });
    }
}
