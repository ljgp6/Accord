package uk.akane.accord.ui.components.blurview;

import android.graphics.Canvas;

// Used in edit mode and in case if no BlurController was set
public class NoOpController implements BlurController {
    @Override
    public boolean draw(Canvas canvas) {
        return true;
    }

    @Override
    public void updateBlurViewSize() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public BlurViewFacade setBlurRadius(float radius) {
        return this;
    }

    @Override
    public void setBlurAutoUpdate(boolean enabled) {
    }
}
