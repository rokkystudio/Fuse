package com.rokkystudio.fuse.diagram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

import java.io.IOException;
import java.io.InputStream;

public class DiagramView extends View
{
    private OnDiagramClickListener mOnDiagramClickListener = null;
    private String mDiagramFilename = null;
    private Drawable mDrawable = null;

    private static final float SUPER_MIN_MULTIPLIER = 0.75f;
    private static final float SUPER_MAX_MULTIPLIER = 1.75f;

    private float normalizedScale = 1;

    private enum ZoomState {
        USER_ZOOM, MIN_ZOOM, MID_ZOOM, BIG_ZOOM, MAX_ZOOM
    }

    private ZoomState mZoomState = ZoomState.MIN_ZOOM;

    private enum State {
        NONE, DRAG, ZOOM, FLING, ANIMATE_ZOOM
    }
    private State mState = State.NONE;

    private float minScale = 1;
    private float maxScale = 3;
    private float superMinScale;
    private float superMaxScale;
    private float[] m;

    private Fling mFling;

    private boolean imageRenderedAtLeastOnce;
    private boolean onDrawReady;
    private boolean mFixed;

    private ZoomVariables delayedZoomVariables;

    // Size of view and previous view size (ie before rotation)
    private int viewWidth, viewHeight, prevViewWidth, prevViewHeight;

    // Size of image when it is stretched to fit view. Before and After rotation.
    private float matchViewWidth, matchViewHeight, prevMatchViewWidth, prevMatchViewHeight;

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private final GestureDetector.OnDoubleTapListener doubleTapListener = null;
    private final OnTouchImageViewListener touchImageViewListener = null;

    private final Matrix mMatrix = new Matrix();
    private final Matrix mPrevMatrix = new Matrix();

    public DiagramView(Context context) {
        super(context);
        init(context);
    }

    public DiagramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DiagramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setClickable(true);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());
        m = new float[9];
        superMinScale = SUPER_MIN_MULTIPLIER * minScale;
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale;
        onDrawReady = false;
        setOnTouchListener(new PrivateOnTouchListener());
    }

    public void setFixed(boolean fixed) {
        mFixed = fixed;
    }

    public void setImageFromAsset(String filename)
    {
        mDiagramFilename = filename;
        AssetManager assetManager = getContext().getAssets();
        try {
            InputStream inputStream = assetManager.open(filename);
            mDrawable = BitmapDrawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        float width = mDrawable.getMinimumWidth();
        float height = mDrawable.getMinimumHeight();

        requestLayout();
        postInvalidate();
    }

    /**
     * Returns false if image is in initial, unzoomed mState. False, otherwise.
     * @return true if image is zoomed
     */
    public boolean isZoomed() {
        return normalizedScale != 1;
    }

    /**
     * Return a Rect representing the zoomed image.
     * @return rect representing zoomed image
     */
    public RectF getZoomedRect() {
        PointF topLeft = transformCoordTouchToBitmap(0, 0, true);
        PointF bottomRight = transformCoordTouchToBitmap(viewWidth, viewHeight, true);

        float w = mDrawable.getIntrinsicWidth();
        float h = mDrawable.getIntrinsicHeight();
        return new RectF(topLeft.x / w, topLeft.y / h, bottomRight.x / w, bottomRight.y / h);
    }

    /**
     * Save the current matrix and view dimensions
     * in the mPrevMatrix and prevView variables.
     */
    private void savePreviousImageValues()
    {
        if (viewHeight == 0 || viewWidth == 0) return;

        mMatrix.getValues(m);
        mPrevMatrix.setValues(m);
        prevMatchViewHeight = matchViewHeight;
        prevMatchViewWidth = matchViewWidth;
        prevViewHeight = viewHeight;
        prevViewWidth = viewWidth;
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putFloat("saveScale", normalizedScale);
        bundle.putFloat("matchViewHeight", matchViewHeight);
        bundle.putFloat("matchViewWidth", matchViewWidth);
        bundle.putInt("viewWidth", viewWidth);
        bundle.putInt("viewHeight", viewHeight);
        mMatrix.getValues(m);
        bundle.putFloatArray("matrix", m);
        bundle.putBoolean("imageRendered", imageRenderedAtLeastOnce);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle)
        {
            Bundle bundle = (Bundle) state;
            normalizedScale = bundle.getFloat("saveScale");
            m = bundle.getFloatArray("matrix");
            mPrevMatrix.setValues(m);
            prevMatchViewHeight = bundle.getFloat("matchViewHeight");
            prevMatchViewWidth = bundle.getFloat("matchViewWidth");
            prevViewHeight = bundle.getInt("viewHeight");
            prevViewWidth = bundle.getInt("viewWidth");
            imageRenderedAtLeastOnce = bundle.getBoolean("imageRendered");
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        savePreviousImageValues();
    }

    public float getMaxZoom() {
        return maxScale;
    }

    public void setMaxZoom(float max) {
        maxScale = max;
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale;
    }

    public float getMinZoom() {
        return minScale;
    }

    public float getCurrentZoom() {
        return normalizedScale;
    }

    public void setMinZoom(float min) {
        minScale = min;
        superMinScale = SUPER_MIN_MULTIPLIER * minScale;
    }

    public void resetZoom() {
        normalizedScale = 1;
        fitImageToView();
    }

    public void setZoom(float scale, float focusX, float focusY)
    {
        if (!onDrawReady) {
            delayedZoomVariables = new ZoomVariables(scale, focusX, focusY);
            return;
        }

        resetZoom();
        scaleImage(scale, viewWidth / 2f, viewHeight / 2f, true);
        mMatrix.getValues(m);
        m[Matrix.MTRANS_X] = -((focusX * getImageWidth()) - (viewWidth * 0.5f));
        m[Matrix.MTRANS_Y] = -((focusY * getImageHeight()) - (viewHeight * 0.5f));
        mMatrix.setValues(m);
        fixTrans();
        invalidate();
    }

    public void setZoom(DiagramView img) {
        PointF center = img.getScrollPosition();
        setZoom(img.getCurrentZoom(), center.x, center.y);
    }

    public PointF getScrollPosition() {
        if (mDrawable == null) {
            return null;
        }
        int drawableWidth = mDrawable.getIntrinsicWidth();
        int drawableHeight = mDrawable.getIntrinsicHeight();

        PointF point = transformCoordTouchToBitmap(viewWidth / 2f, viewHeight / 2f, true);
        point.x /= drawableWidth;
        point.y /= drawableHeight;
        return point;
    }

    public void setScrollPosition(float focusX, float focusY) {
        setZoom(normalizedScale, focusX, focusY);
    }

    /**
     * Performs boundary checking and fixes the image matrix if it
     * is out of bounds.
     */
    private void fixTrans()
    {
        mMatrix.getValues(m);
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];

        float fixTransX = getFixTrans(transX, viewWidth, getImageWidth());
        float fixTransY = getFixTrans(transY, viewHeight, getImageHeight());

        if (fixTransX != 0 || fixTransY != 0) {
            mMatrix.postTranslate(fixTransX, fixTransY);
        }
    }

    /**
     * When transitioning from zooming from focus to zoom from center (or vice versa)
     * the image can become unaligned within the view. This is apparent when zooming
     * quickly. When the content size is less than the view size, the content will often
     * be centered incorrectly within the view. fixScaleTrans first calls fixTrans() and
     * then makes sure the image is centered correctly within the view.
     */
    private void fixScaleTrans() {
        fixTrans();
        mMatrix.getValues(m);
        if (getImageWidth() < viewWidth) {
            m[Matrix.MTRANS_X] = (viewWidth - getImageWidth()) / 2;
        }

        if (getImageHeight() < viewHeight) {
            m[Matrix.MTRANS_Y] = (viewHeight - getImageHeight()) / 2;
        }
        mMatrix.setValues(m);
    }

    private float getFixTrans(float trans, float viewSize, float contentSize)
    {
        float minTrans, maxTrans;

        if (contentSize <= viewSize) {
            minTrans = 0;
            maxTrans = viewSize - contentSize;

        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        if (trans < minTrans)
            return -trans + minTrans;
        if (trans > maxTrans)
            return -trans + maxTrans;
        return 0;
    }

    private float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) return 0;
        return delta;
    }

    private float getImageWidth() {
        return matchViewWidth * normalizedScale;
    }

    private float getImageHeight() {
        return matchViewHeight * normalizedScale;
    }

    /**
     * If the normalizedScale is equal to 1, then the image is made to fit the screen. Otherwise,
     * it is made to fit the screen according to the dimensions of the previous image matrix. This
     * allows the image to maintain its zoom after rotation.
     */
    private void fitImageToView()
    {
        if (mDrawable == null) return;
        if (mDrawable.getIntrinsicWidth() == 0) return;
        if (mDrawable.getIntrinsicHeight() == 0) return;

        int drawableWidth = mDrawable.getIntrinsicWidth();
        int drawableHeight = mDrawable.getIntrinsicHeight();

        // Scale image for view
        float scaleX = (float) viewWidth / drawableWidth;
        float scaleY = (float) viewHeight / drawableHeight;
        float scale = Math.min(scaleX, scaleY);

        // Center the image
        float redundantXSpace = viewWidth - (scale * drawableWidth);
        float redundantYSpace = viewHeight - (scale * drawableHeight);
        matchViewWidth = viewWidth - redundantXSpace;
        matchViewHeight = viewHeight - redundantYSpace;

        if (!isZoomed() && !imageRenderedAtLeastOnce) {
            // Stretch and center image to fit view
            mMatrix.setScale(scale, scale);
            mMatrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);
            normalizedScale = 1;
        } else {
            // These values should never be 0 or we will set viewWidth and viewHeight
            // to NaN in translateMatrixAfterRotate. To avoid this, call savePreviousImageValues
            // to set them equal to the current values.
            if (prevMatchViewWidth == 0 || prevMatchViewHeight == 0) {
                savePreviousImageValues();
            }

            mPrevMatrix.getValues(m);

            // Rescale Matrix after rotation
            m[Matrix.MSCALE_X] = matchViewWidth / drawableWidth * normalizedScale;
            m[Matrix.MSCALE_Y] = matchViewHeight / drawableHeight * normalizedScale;

            // TransX and TransY from previous matrix
            float transX = m[Matrix.MTRANS_X];
            float transY = m[Matrix.MTRANS_Y];

            // Width
            float prevActualWidth = prevMatchViewWidth * normalizedScale;
            float actualWidth = getImageWidth();
            translateMatrixAfterRotate(Matrix.MTRANS_X, transX, prevActualWidth, actualWidth, prevViewWidth, viewWidth, drawableWidth);

            // Height
            float prevActualHeight = prevMatchViewHeight * normalizedScale;
            float actualHeight = getImageHeight();
            translateMatrixAfterRotate(Matrix.MTRANS_Y, transY, prevActualHeight, actualHeight, prevViewHeight, viewHeight, drawableHeight);

            // Set the matrix to the adjusted scale and translate values.
            mMatrix.setValues(m);
        }
        fixTrans();
        invalidate();
    }

    /**
     * Set view dimensions based on layout params
     */
    private int setViewSize(int mode, int size, int drawableWidth)
    {
        int viewSize;
        switch (mode)
        {
            case MeasureSpec.AT_MOST:
                viewSize = Math.min(drawableWidth, size);
                break;

            case MeasureSpec.UNSPECIFIED:
                viewSize = drawableWidth;
                break;

            default:
                viewSize = size;
                break;
        }
        return viewSize;
    }

    /**
     * After rotating, the matrix needs to be translated. This function finds the area of image
     * which was previously centered and adjusts translations so that is again the center, post-rotation.
     *
     * @param axis Matrix.MTRANS_X or Matrix.MTRANS_Y
     * @param trans the value of trans in that axis before the rotation
     * @param prevImageSize the width/height of the image before the rotation
     * @param imageSize width/height of the image after rotation
     * @param prevViewSize width/height of view before rotation
     * @param viewSize width/height of view after rotation
     * @param drawableSize width/height of drawable
     */
    private void translateMatrixAfterRotate(int axis, float trans, float prevImageSize, float imageSize, int prevViewSize, int viewSize, int drawableSize)
    {
        if (imageSize < viewSize) {
            // The width/height of image is less than the view's width/height. Center it.
            m[axis] = (viewSize - (drawableSize * m[Matrix.MSCALE_X])) * 0.5f;

        } else if (trans > 0) {
            // The image is larger than the view, but was not before rotation. Center it.
            m[axis] = -((imageSize - viewSize) * 0.5f);

        } else {
            // Find the area of the image which was previously centered in the view. Determine its distance
            // from the left/top side of the view as a fraction of the entire image's width/height. Use that percentage
            // to calculate the trans in the new view width/height.
            float percentage = (Math.abs(trans) + (0.5f * prevViewSize)) / prevImageSize;
            m[axis] = -((percentage * imageSize) - (viewSize * 0.5f));
        }
    }

    private void setState(State state) {
        this.mState = state;
    }

    @Override
    public boolean canScrollHorizontally(int direction)
    {
        mMatrix.getValues(m);
        float x = m[Matrix.MTRANS_X];

        if (getImageWidth() < viewWidth) return false;
        if (x >= -1 && direction < 0) return false;

        return !(Math.abs(x) + viewWidth + 1 >= getImageWidth()) || direction <= 0;
    }

    /**
     * Gesture Listener detects a single click or long click and passes that on to the view's listener.
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            if (mOnDiagramClickListener != null) {
                mOnDiagramClickListener.onDiagramClick(mDiagramFilename);
            }
            return performClick();
        }

        @Override
        public void onLongPress(MotionEvent e) {
            performLongClick();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (mFling != null) mFling.cancelFling();
            mFling = new Fling(getContext(), (int) velocityX, (int) velocityY);
            postOnAnimation(mFling);
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            boolean consumed = false;
            if (doubleTapListener != null) {
                consumed = doubleTapListener.onDoubleTap(e);
            }

            if (mState == State.NONE)
            {
                float targetZoom = minScale;

                switch (mZoomState)
                {
                    case USER_ZOOM: {
                        // Если после изменения пользователем масштаб минимальный
                        if (normalizedScale == minScale) {
                            // Увеличиваем до MID_ZOOM
                            targetZoom = maxScale / 8f + minScale;
                            mZoomState = ZoomState.MID_ZOOM;
                        } else {
                            // Иначе уменьшаем до MIN_ZOOM
                            targetZoom = minScale;
                            mZoomState = ZoomState.MIN_ZOOM;
                        }
                        break;
                    }

                    case MIN_ZOOM: {
                        targetZoom = maxScale / 8f + minScale;
                        mZoomState = ZoomState.MID_ZOOM;
                        break;
                    }

                    case MID_ZOOM: {
                        targetZoom = maxScale / 3f + minScale;
                        mZoomState = ZoomState.BIG_ZOOM;
                        break;
                    }

                    case BIG_ZOOM: {
                        targetZoom = minScale;
                        mZoomState = ZoomState.MIN_ZOOM;
                        break;
                    }
                }

                DoubleTapZoom doubleTap = new DoubleTapZoom(targetZoom, e.getX(), e.getY(), false);
                postOnAnimation(doubleTap);
                consumed = true;
            }
            return consumed;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            if(doubleTapListener != null) {
                return doubleTapListener.onDoubleTapEvent(e);
            }
            return false;
        }
    }

    public interface OnTouchImageViewListener {
        void onMove();
    }

    /**
     * Responsible for all touch events. Handles the heavy lifting of drag and also sends
     * touch events to Scale Detector and Gesture Detector.
     */
    private class PrivateOnTouchListener implements OnTouchListener
    {
        // Remember last point position for dragging
        private final PointF last = new PointF();

        @Override
        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouch(View v, MotionEvent event)
        {
            mScaleDetector.onTouchEvent(event);
            mGestureDetector.onTouchEvent(event);
            PointF curr = new PointF(event.getX(), event.getY());

            if (mState == State.NONE || mState == State.DRAG || mState == State.FLING)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        last.set(curr);
                        if (mFling != null)
                            mFling.cancelFling();
                        setState(State.DRAG);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (mState == State.DRAG) {
                            float deltaX = curr.x - last.x;
                            float deltaY = curr.y - last.y;
                            float fixTransX = getFixDragTrans(deltaX, viewWidth, getImageWidth());
                            float fixTransY = getFixDragTrans(deltaY, viewHeight, getImageHeight());
                            mMatrix.postTranslate(fixTransX, fixTransY);
                            fixTrans();
                            last.set(curr.x, curr.y);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        setState(State.NONE);
                        break;
                }
            }

            invalidate();

            // OnTouchImageViewListener is set: TouchImageView dragged by user.
            if (touchImageViewListener != null) {
                touchImageViewListener.onMove();
            }

            // indicate event was handled
            return true;
        }
    }

    /**
     * ScaleListener detects user two finger scaling and scales image.
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mZoomState = ZoomState.USER_ZOOM;
            setState(State.ZOOM);
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            scaleImage(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY(), true);

            // OnTouchImageViewListener is set: TouchImageView pinch zoomed by user.
            if (touchImageViewListener != null) {
                touchImageViewListener.onMove();
            }
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector)
        {
            super.onScaleEnd(detector);
            setState(State.NONE);
            boolean animateToZoomBoundary = false;
            float targetZoom = normalizedScale;
            if (normalizedScale > maxScale) {
                targetZoom = maxScale;
                animateToZoomBoundary = true;

            } else if (normalizedScale < minScale) {
                targetZoom = minScale;
                animateToZoomBoundary = true;
            }

            if (animateToZoomBoundary) {
                DoubleTapZoom doubleTap = new DoubleTapZoom(targetZoom, viewWidth / 2f, viewHeight / 2f, true);
                postOnAnimation(doubleTap);
            }
        }
    }

    private void scaleImage(double deltaScale, float focusX, float focusY, boolean stretchImageToSuper)
    {
        float lowerScale, upperScale;
        if (stretchImageToSuper) {
            lowerScale = superMinScale;
            upperScale = superMaxScale;
        } else {
            lowerScale = minScale;
            upperScale = maxScale;
        }

        float origScale = normalizedScale;
        normalizedScale *= deltaScale;
        if (normalizedScale > upperScale) {
            normalizedScale = upperScale;
            deltaScale = upperScale / origScale;
        } else if (normalizedScale < lowerScale) {
            normalizedScale = lowerScale;
            deltaScale = lowerScale / origScale;
        }

        mMatrix.postScale((float) deltaScale, (float) deltaScale, focusX, focusY);
        fixScaleTrans();
    }

    /**
     * DoubleTapZoom calls a series of runnables which apply
     * an animated zoom in/out graphic to the image.
     */
    private class DoubleTapZoom implements Runnable
    {
        private final long startTime;
        private static final float ZOOM_TIME = 500;
        private final float startZoom, targetZoom, bitmapX, bitmapY;
        private final boolean stretchImageToSuper;

        private final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        private final PointF startTouch, endTouch;

        DoubleTapZoom(float targetZoom, float focusX, float focusY, boolean stretchImageToSuper) {
            setState(State.ANIMATE_ZOOM);
            startTime = System.currentTimeMillis();
            this.startZoom = normalizedScale;
            this.targetZoom = targetZoom;
            this.stretchImageToSuper = stretchImageToSuper;
            PointF bitmapPoint = transformCoordTouchToBitmap(focusX, focusY, false);
            this.bitmapX = bitmapPoint.x;
            this.bitmapY = bitmapPoint.y;

            // Used for translating image during scaling
            startTouch = transformCoordBitmapToTouch(bitmapX, bitmapY);
            endTouch = new PointF(viewWidth / 2f, viewHeight / 2f);
        }

        @Override
        public void run()
        {
            float t = interpolate();
            double deltaScale = calculateDeltaScale(t);
            scaleImage(deltaScale, bitmapX, bitmapY, stretchImageToSuper);
            translateImageToCenterTouchPosition(t);
            fixScaleTrans();
            invalidate();

            // OnTouchImageViewListener is set: double tap runnable updates listener with every frame
            if (touchImageViewListener != null) {
                touchImageViewListener.onMove();
            }

            if (t < 1f) {
                // We haven't finished zooming
                postOnAnimation(this);
            } else {
                // Finished zooming
                setState(State.NONE);
            }
        }

        /**
         * Interpolate between where the image should start and end in order to translate
         * the image so that the point that is touched is what ends up centered at the end of the zoom.
         */
        private void translateImageToCenterTouchPosition(float t) {
            float targetX = startTouch.x + t * (endTouch.x - startTouch.x);
            float targetY = startTouch.y + t * (endTouch.y - startTouch.y);
            PointF curr = transformCoordBitmapToTouch(bitmapX, bitmapY);
            mMatrix.postTranslate(targetX - curr.x, targetY - curr.y);
        }

        /**
         * Use interpolator to get t
         */
        private float interpolate() {
            long currTime = System.currentTimeMillis();
            float elapsed = (currTime - startTime) / ZOOM_TIME;
            elapsed = Math.min(1f, elapsed);
            return interpolator.getInterpolation(elapsed);
        }

        /**
         * Interpolate the current targeted zoom and get the delta
         * from the current zoom.
         */
        private double calculateDeltaScale(float t) {
            double zoom = startZoom + t * (targetZoom - startZoom);
            return zoom / normalizedScale;
        }
    }

    /**
     * This function will transform the coordinates in the touch event to the coordinate
     * system of the drawable that the imageview contain
     * @param x x-coordinate of touch event
     * @param y y-coordinate of touch event
     * @param clipToBitmap Touch event may occur within view, but outside image content. True, to clip return value
     * 			to the bounds of the bitmap size.
     * @return Coordinates of the point touched, in the coordinate system of the original drawable.
     */
    private PointF transformCoordTouchToBitmap(float x, float y, boolean clipToBitmap)
    {
        mMatrix.getValues(m);
        float origW = mDrawable.getIntrinsicWidth();
        float origH = mDrawable.getIntrinsicHeight();
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];
        float finalX = ((x - transX) * origW) / getImageWidth();
        float finalY = ((y - transY) * origH) / getImageHeight();

        if (clipToBitmap) {
            finalX = Math.min(Math.max(finalX, 0), origW);
            finalY = Math.min(Math.max(finalY, 0), origH);
        }

        return new PointF(finalX , finalY);
    }

    /**
     * Inverse of transformCoordTouchToBitmap. This function will transform the coordinates in the
     * drawable's coordinate system to the view's coordinate system.
     * @param bx x-coordinate in original bitmap coordinate system
     * @param by y-coordinate in original bitmap coordinate system
     * @return Coordinates of the point in the view's coordinate system.
     */
    private PointF transformCoordBitmapToTouch(float bx, float by)
    {
        mMatrix.getValues(m);
        float origW = mDrawable.getIntrinsicWidth();
        float origH = mDrawable.getIntrinsicHeight();
        float px = bx / origW;
        float py = by / origH;
        float finalX = m[Matrix.MTRANS_X] + getImageWidth() * px;
        float finalY = m[Matrix.MTRANS_Y] + getImageHeight() * py;
        return new PointF(finalX , finalY);
    }

    private class Fling implements Runnable
    {
        private Scroller scroller;
        private int currX, currY;

        Fling(Context context, int velocityX, int velocityY)
        {
            scroller = new Scroller(context);
            mMatrix.getValues(m);

            int startX = (int) m[Matrix.MTRANS_X];
            int startY = (int) m[Matrix.MTRANS_Y];
            int minX, maxX, minY, maxY;

            if (getImageWidth() > viewWidth) {
                minX = viewWidth - (int) getImageWidth();
                maxX = 0;

            } else {
                minX = maxX = startX;
            }

            if (getImageHeight() > viewHeight) {
                minY = viewHeight - (int) getImageHeight();
                maxY = 0;

            } else {
                minY = maxY = startY;
            }

            scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            currX = startX;
            currY = startY;
        }

        void cancelFling() {
            if (scroller != null) {
                setState(State.NONE);
                scroller.forceFinished(true);
            }
        }

        @Override
        public void run()
        {
            setState(State.FLING);
            // OnTouchImageViewListener is set: TouchImageView listener has been flung by user.
            // Listener runnable updated with each frame of mFling animation.
            if (touchImageViewListener != null) {
                touchImageViewListener.onMove();
            }

            if (scroller.isFinished()) {
                scroller = null;
                return;
            }

            if (scroller.computeScrollOffset())
            {
                int newX = scroller.getCurrX();
                int newY = scroller.getCurrY();
                int transX = newX - currX;
                int transY = newY - currY;
                currX = newX;
                currY = newY;
                mMatrix.postTranslate(transX, transY);
                fixTrans();
                invalidate();
                postOnAnimation(this);
                return;
            }

            setState(State.NONE);
            invalidate();
        }
    }

    private static class ZoomVariables
    {
        float scale;
        float focusX;
        float focusY;

        ZoomVariables(float scale, float focusX, float focusY) {
            this.scale = scale;
            this.focusX = focusX;
            this.focusY = focusY;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        onDrawReady = true;
        imageRenderedAtLeastOnce = true;
        if (delayedZoomVariables != null) {
            setZoom(delayedZoomVariables.scale, delayedZoomVariables.focusX, delayedZoomVariables.focusY);
            delayedZoomVariables = null;
        }

        if (mDrawable == null) return;

        canvas.save();

        if (mMatrix != null) {
            canvas.concat(mMatrix);
        }

        // Задний план серый
        canvas.drawColor(0xFFEEEEEE);
        canvas.save();
        canvas.clipRect(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        // Фон изображения белый
        canvas.drawColor(0xFFFFFFFF);
        canvas.restore();

        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        mDrawable.draw(canvas);

        canvas.restore();
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (mDrawable == null || mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            setMeasuredDimension(0, 0);
            return;
        }

        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (mFixed) {
            viewHeight = viewWidth * mDrawable.getIntrinsicHeight() / mDrawable.getIntrinsicWidth();
        }

        // Set view dimensions
        setMeasuredDimension(viewWidth, viewHeight);

        // Fit content within view
        fitImageToView();
    }

    public void setOnDiagramClickListener(OnDiagramClickListener listener) {
        mOnDiagramClickListener = listener;
    }

    public interface OnDiagramClickListener {
        void onDiagramClick(String filename);
    }
}