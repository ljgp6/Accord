package uk.akane.accord.ui.components.blurview;

public interface BlurViewFacade {

    /**
     * Can be used to stop blur auto update or resume if it was stopped before.
     * Enabled by default.
     */
    void setBlurAutoUpdate(boolean enabled);

    /**
     * @param radius sets the blur radius
     *               Default value is {@link BlurController#DEFAULT_BLUR_RADIUS}
     * @return {@link BlurViewFacade}
     */
    BlurViewFacade setBlurRadius(float radius);

}
