package com.swiftythesloth.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

/**
 * Represents a slide in the instructions screen
 */
public class InstructionsFragment extends Fragment {

    private final int ANIMATION_TIME_ENTRY = 50;
    private final int ANIMATION_TIME_EXIT = 15;

    private ArrayList<View> mAnimatableViews = new ArrayList<>();
    private ViewGroup mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int pageNum = getArguments().getInt(Constants.BUNDLE_INSTRUCTIONS_PAGE_NUMBER);

        mRootView = null;

        if (pageNum == 1) {
            mRootView = (ViewGroup) inflater.inflate(R.layout.instructions_slide_1, container, false);
        } else if (pageNum == 2) {
            mRootView = (ViewGroup) inflater.inflate(R.layout.instructions_slide_2, container, false);
        } else if (pageNum == 3) {
            mRootView = (ViewGroup) inflater.inflate(R.layout.instructions_slide_3, container, false);
        } else if (pageNum == 4) {
            mRootView = (ViewGroup) inflater.inflate(R.layout.instructions_slide_4, container, false);
        }

        mAnimatableViews.clear();
        mAnimatableViews = addAllViewsToList(mRootView, mAnimatableViews);
        animateEntry(mAnimatableViews, ANIMATION_TIME_ENTRY);

        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        animateExit(mAnimatableViews, ANIMATION_TIME_EXIT);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getActivity(), MainMenuActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) * ANIMATION_TIME_EXIT);
    }

    private void animateExit(final ArrayList<View> views, int duration) {
        final Handler handler = new Handler();

        for (int i = 0; i < views.size(); i++) {
            final int j = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    views.get(j).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_to_pin));
                }
            }, i * duration);
        }
    }

    private void animateEntry(final ArrayList<View> views, int duration) {
        final Handler handler = new Handler();

        for (View v : views) {
            v.setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < views.size(); i++) {
            final int j = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    views.get(j).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_from_pin));
                    views.get(j).setVisibility(View.VISIBLE);
                }
            }, i * duration);
        }

    }

    private ArrayList<View> addAllViewsToList(ViewGroup viewGroup, ArrayList<View> views) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                addAllViewsToList((ViewGroup) viewGroup.getChildAt(i), views);
            } else {
                views.add(viewGroup.getChildAt(i));
            }
        }
        return views;
    }
}
