package me.carc.stolpersteine.activities.map.overlays;

import android.graphics.Color;
import android.graphics.Paint;

import me.carc.stolpersteine.common.C;

/**
 * Options for SimpleFastPointOverlay.
 * Created by Miguel Porto on 25-10-2016.
 */

public class PointOverlayOptions {
    public enum RenderingAlgorithm {NO_OPTIMIZATION, MEDIUM_OPTIMIZATION, MAXIMUM_OPTIMIZATION}
    public enum Shape {CIRCLE, SQUARE}
    public enum LabelPolicy {ZOOM_THRESHOLD, DENSITY_THRESHOLD}
     Paint mPointStyle;
    Paint mSelectedPointStyle;
    Paint mTextStyle;
    float mCircleRadius = 5;
    float mSelectedCircleRadius = 13;
    boolean mClickable = true;
    int mCellSize = 10;   // the size of the grid cells in pixels.
    PointOverlayOptions.RenderingAlgorithm mAlgorithm = PointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION;
    PointOverlayOptions.Shape mSymbol = PointOverlayOptions.Shape.SQUARE;     // default is square, cause circle is a slow renderer
    PointOverlayOptions.LabelPolicy mLabelPolicy = PointOverlayOptions.LabelPolicy.ZOOM_THRESHOLD;
    int mMaxNShownLabels = 250;
    int mMinZoomShowLabels = 11;

    public PointOverlayOptions() {
        mPointStyle = new Paint();
        mPointStyle.setStyle(Paint.Style.FILL);
        mPointStyle.setColor(Color.parseColor("#d32f2f"));

        mSelectedPointStyle = new Paint();
        mSelectedPointStyle.setStrokeWidth(5);
        mSelectedPointStyle.setStyle(Paint.Style.STROKE);
        mSelectedPointStyle.setColor(Color.parseColor("#795548"));

        mTextStyle = new Paint();
        mTextStyle.setStyle(Paint.Style.FILL);
        mTextStyle.setColor(Color.parseColor("#000000"));
        mTextStyle.setTextAlign(Paint.Align.CENTER);
        mTextStyle.setTextSize(C.DENSITY * 16);
    }

    /**
     * Creates a new {@link PointOverlayOptions} object with default options.
     * @return {@link PointOverlayOptions}
     */
    public static PointOverlayOptions getDefaultStyle() {
        return new PointOverlayOptions();
    }

    /**
     * Sets the style for the point overlay, which is applied to all circles.
     * If the layer is individually styled, the individual style overrides this.
     * @param style A Paint object.
     * @return The updated {@link PointOverlayOptions}
     */
    public PointOverlayOptions setPointStyle(Paint style) {
        mPointStyle = style;
        return this;
    }

    /**
     * Sets the style for the selected point.
     * @param style A Paint object.
     * @return The updated {@link PointOverlayOptions}
     */
    public PointOverlayOptions setSelectedPointStyle(Paint style) {
        mSelectedPointStyle = style;
        return this;
    }

    /**
     * Sets the radius of the circles to be drawn.
     * @param radius Radius.
     * @return The updated {@link PointOverlayOptions}
     */
    public PointOverlayOptions setRadius(float radius) {
        mCircleRadius = radius;
        return this;
    }

    /**
     * Sets the radius of the selected point's circle.
     * @param radius Radius.
     * @return The updated {@link PointOverlayOptions}
     */
    public PointOverlayOptions setSelectedRadius(float radius) {
        mSelectedCircleRadius = radius;
        return this;
    }

    /**
     * Sets whether this overlay is clickable or not. A clickable overlay will automatically select
     * the nearest point.
     * @param clickable True or false.
     * @return The updated {@link PointOverlayOptions}
     */
    public PointOverlayOptions setIsClickable(boolean clickable) {
        mClickable = clickable;
        return this;
    }

    /**
     * Sets the grid cell size used for indexing, in pixels. Larger cells result in faster rendering
     * speed, but worse fidelity. Default is 10 pixels, for large datasets (>10k points), use 15.
     * @param cellSize The cell size in pixels.
     * @return The updated {@link PointOverlayOptions}
     */
    public PointOverlayOptions setCellSize(int cellSize) {
        mCellSize = cellSize;
        return this;
    }

    /**
     * Sets the rendering algorithm. There are three options:
     * NO_OPTIMIZATION: Slowest option. Draw all points on each draw event.
     * MEDIUM_OPTIMIZATION: Faster. Recalculates the grid index on each draw event.
     *          Not recommended for >10k points. Better UX, but may be choppier.
     * MAXIMUM_OPTIMIZATION: Fastest. Only recalculates the grid on touch up and animation end
     *          , hence much faster display on move. Recommended for >10k points.
     * @param algorithm A {@link PointOverlayOptions.RenderingAlgorithm}.
     * @return The updated {@link PointOverlayOptions}
     */
    public PointOverlayOptions setAlgorithm(PointOverlayOptions.RenderingAlgorithm algorithm) {
        mAlgorithm = algorithm;
        return this;
    }

    /**
     * Sets the symbol shape for this layer. Hint: circle shape is less performant, avoid for large N.
     * @param symbol The symbol, currently CIRCLE or SQUARE.
     * @return The updated {@link PointOverlayOptions}
     */
    public PointOverlayOptions setSymbol(PointOverlayOptions.Shape symbol) {
        mSymbol = symbol;
        return this;
    }

    /**
     * Sets the style for the labels.
     * If the layer is individually styled, the individual style overrides this.
     * @param textStyle The style.
     * @return The updated {@link PointOverlayOptions}
     */
    public PointOverlayOptions setTextStyle(Paint textStyle) {
        mTextStyle = textStyle;
        return this;
    }

    /**
     * Sets the minimum zoom level at which the labels should be drawn. This option is
     * <b>ignored</b> if LabelPolicy is DENSITY_THRESHOLD.
     * @param minZoomShowLabels The zoom level.
     * @return
     */
    public PointOverlayOptions setMinZoomShowLabels(int minZoomShowLabels) {
        mMinZoomShowLabels = minZoomShowLabels;
        return this;
    }

    /**
     * Sets the threshold (nr. of visible points) after which labels will not be drawn. <b>This
     * option only works when LabelPolicy is DENSITY_THRESHOLD and the algorithm is
     * MAXIMUM_OPTIMIZATION</b>.
     * @param maxNShownLabels The maximum number of visible points
     * @return
     */
    public PointOverlayOptions setMaxNShownLabels(int maxNShownLabels) {
        mMaxNShownLabels = maxNShownLabels;
        return this;
    }

    /**
     * Sets the policy for displaying point labels. Can be:<br/>
     *     ZOOM_THRESHOLD: Labels are not displayed is current map zoom level is lower than
     *         <code>MinZoomShowLabels</code>
     *     DENSITY_THRESHOLD: Labels are not displayed when the number of visible points is larger
     *         than <code>MaxNShownLabels</code>. <b>This only works for MAXIMUM_OPTIMIZATION</b><br/>
     * @param labelPolicy One of <code>ZOOM_THRESHOLD</code> or <code>DENSITY_THRESHOLD</code>
     * @return
     */
    public PointOverlayOptions setLabelPolicy(PointOverlayOptions.LabelPolicy labelPolicy) {
        mLabelPolicy = labelPolicy;
        return this;
    }
}
