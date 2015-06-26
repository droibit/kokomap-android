package com.droibit.kokomap.view;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import java.util.List;

/**
 * Snackbar使用時にfamが隠れないようにするための実装。
 * BehaviorはデザインライブラリのFloatingActionButton.Behavior を修正して使用している。
 *
 * Created by kumagai on 2015/06/26.
 */
@CoordinatorLayout.DefaultBehavior(FloatingActionMenu.Behavior.class)
public class FloatingActionMenu extends com.github.clans.fab.FloatingActionMenu {

    public FloatingActionMenu(Context context) {
        super(context);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static class Behavior extends CoordinatorLayout.Behavior<FloatingActionMenu> {
        private static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();
        private static final boolean SNACKBAR_BEHAVIOR_ENABLED;
        private float mTranslationY;

        static {
            SNACKBAR_BEHAVIOR_ENABLED = Build.VERSION.SDK_INT >= 11;
        }

        public Behavior() {
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
            return SNACKBAR_BEHAVIOR_ENABLED && dependency instanceof Snackbar.SnackbarLayout;
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
            if(dependency instanceof Snackbar.SnackbarLayout) {
                this.updateFamTranslationForSnackbar(parent, child, dependency);
            }
            return false;
        }

        private void updateFamTranslationForSnackbar(CoordinatorLayout parent, FloatingActionMenu fam, View snackbar) {
            float translationY = this.getFamTranslationYForSnackbar(parent, fam);
            if(translationY != this.mTranslationY) {
                ViewCompat.animate(fam).cancel();
                if(Math.abs(translationY - this.mTranslationY) == (float)snackbar.getHeight()) {
                    ViewCompat.animate(fam)
                              .translationY(translationY)
                              .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                              .setListener(null);
                } else {
                    ViewCompat.setTranslationY(fam, translationY);
                }
                this.mTranslationY = translationY;
            }
        }

        private float getFamTranslationYForSnackbar(CoordinatorLayout parent, FloatingActionMenu fam) {
            float minOffset = 0.0F;
            List<View> dependencies = parent.getDependencies(fam);
            for(int i = 0, size = dependencies.size(); i < size; ++i) {
                View view = dependencies.get(i);
                if(view instanceof Snackbar.SnackbarLayout && parent.doViewsOverlap(fam, view)) {
                    minOffset = Math.min(minOffset, ViewCompat.getTranslationY(view) - (float)view.getHeight());
                }
            }
            return minOffset;
        }
    }
}
