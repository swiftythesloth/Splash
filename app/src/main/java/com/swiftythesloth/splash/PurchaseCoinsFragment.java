package com.swiftythesloth.splash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Sam on 15/08/2015.
 */
public class PurchaseCoinsFragment extends DialogFragment {

    private OnCoinsPurchasedListener mListener;

    private long mLastClickTime = 0;

    public interface OnCoinsPurchasedListener {
        void onCoinsPurchased(int numCoins);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_purchase_coins, null);
        builder.setView(view);

        TextView purchase10000 = (TextView) view.findViewById(R.id.purchase_coins_10000);
        TextView purchase50000 = (TextView) view.findViewById(R.id.purchase_coins_50000);
        TextView purchase100000 = (TextView) view.findViewById(R.id.purchase_coins_100000);
        TextView purchase200000 = (TextView) view.findViewById(R.id.purchase_coins_200000);
        TextView purchase500000 = (TextView) view.findViewById(R.id.purchase_coins_500000);
        TextView purchase2500000 = (TextView) view.findViewById(R.id.purchase_coins_2500000);
        TextView ok = (TextView) view.findViewById(R.id.purchase_coins_ok);

        String price10000 = getArguments().getString(Constants.BUNDLE_PRICE_10000_COINS);
        String price50000 = getArguments().getString(Constants.BUNDLE_PRICE_50000_COINS);
        String price100000 = getArguments().getString(Constants.BUNDLE_PRICE_100000_COINS);
        String price200000 = getArguments().getString(Constants.BUNDLE_PRICE_200000_COINS);
        String price500000 = getArguments().getString(Constants.BUNDLE_PRICE_500000_COINS);
        String price2500000 = getArguments().getString(Constants.BUNDLE_PRICE_2500000_COINS);

        final String PURCHASE = "Purchase";

        if (price10000 == null || price10000.equals("")) {
            purchase10000.setText(PURCHASE);
        } else {
            purchase10000.setText(price10000);
        }

        if (price50000 == null || price50000.equals("")) {
            purchase50000.setText(PURCHASE);
        } else {
            purchase50000.setText(price50000);
        }

        if (price100000 == null || price100000.equals("")) {
            purchase100000.setText(PURCHASE);
        } else {
            purchase100000.setText(price100000);
        }

        if (price200000 == null || price200000.equals("")) {
            purchase200000.setText(PURCHASE);
        } else {
            purchase200000.setText(price200000);
        }

        if (price500000 == null || price500000.equals("")) {
            purchase500000.setText(PURCHASE);
        } else {
            purchase500000.setText(price500000);
        }

        if (price2500000 == null || price2500000.equals("")) {
            purchase2500000.setText(PURCHASE);
        } else {
            purchase2500000.setText(price2500000);
        }

        purchase10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseTouched(10000);
            }
        });

        purchase50000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseTouched(50000);
            }
        });

        purchase100000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseTouched(100000);
            }
        });

        purchase200000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseTouched(200000);
            }
        });

        purchase500000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseTouched(500000);
            }
        });

        purchase2500000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseTouched(2500000);
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCoinsPurchasedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null) {
            return;
        }
    }

    private void purchaseTouched(int numCoins) {
        //prevent double click
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        mListener.onCoinsPurchased(numCoins);
    }

}
