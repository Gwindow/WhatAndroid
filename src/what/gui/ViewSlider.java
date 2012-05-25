package what.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;

/**
 * @author Gwindow
 * @since May 17, 2012 10:56:17 PM
 */
public class ViewSlider extends ViewFlipper implements OnGestureListener {
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_THRESHOLD_VELOCITY = 75;
	private static final int ANIMATION_SPEED = 300;
	private GestureDetector gestureDetector;

	/**
	 * @param context
	 * @param attrs
	 */
	public ViewSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		gestureDetector = new GestureDetector(this);
	}

	/**
	 * @param context
	 */
	public ViewSlider(Context context) {
		super(context);
		gestureDetector = new GestureDetector(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		try {
			if (e1.getX() > e2.getX() && Math.abs(e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				setInAnimation(inFromRightAnimation());
				setOutAnimation(outToLeftAnimation());
				showPrevious();
			} else if (e1.getX() < e2.getX() && e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				setInAnimation(inFromLeftAnimation());
				setOutAnimation(outToRightAnimation());
				showNext();
			}
		} catch (Exception e) {
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onDown(MotionEvent event) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onLongPress(MotionEvent event) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onShowPress(MotionEvent arg0) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}

	private Animation inFromRightAnimation() {
		Animation animation =
				new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
		animation.setDuration(ANIMATION_SPEED);
		animation.setInterpolator(new AccelerateInterpolator());
		return animation;
	}

	private Animation outToLeftAnimation() {
		Animation animation =
				new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
		animation.setDuration(ANIMATION_SPEED);
		animation.setInterpolator(new AccelerateInterpolator());
		return animation;
	}

	private Animation inFromLeftAnimation() {
		Animation animation =
				new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
		animation.setDuration(ANIMATION_SPEED);
		animation.setInterpolator(new AccelerateInterpolator());
		return animation;
	}

	private Animation outToRightAnimation() {
		Animation animation =
				new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
		animation.setDuration(ANIMATION_SPEED);
		animation.setInterpolator(new AccelerateInterpolator());
		return animation;
	}

}
