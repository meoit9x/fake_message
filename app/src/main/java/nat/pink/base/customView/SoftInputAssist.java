package nat.pink.base.customView;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class SoftInputAssist{
    private View rootView;
    private ViewGroup contentContainer;
    private ViewTreeObserver viewTreeObserver;
    private ViewTreeObserver.OnGlobalLayoutListener listener = () -> possiblyResizeChildOfContent();
    private Rect contentAreaOfWindowBounds = new Rect();
    private FrameLayout.LayoutParams rootViewLayout;
    private int usableHeightPrevious = 0;
    private Activity activity;

    public SoftInputAssist(Activity activity) {
        this.activity = activity;
        contentContainer = (ViewGroup) activity.findViewById(android.R.id.content);
        rootView = contentContainer.getChildAt(0);
        rootViewLayout = (FrameLayout.LayoutParams) rootView.getLayoutParams();
    }

    public void onPause() {
        if (viewTreeObserver.isAlive()) {
            rootView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            viewTreeObserver.removeOnGlobalLayoutListener(listener);
        }
    }

    public void onResume() {
        if (viewTreeObserver == null || !viewTreeObserver.isAlive()) {
            viewTreeObserver = rootView.getViewTreeObserver();
        }
        viewTreeObserver.addOnGlobalLayoutListener(listener);
    }

    public void onDestroy() {
        rootView = null;
        contentContainer = null;
        viewTreeObserver = null;
    }

    private void possiblyResizeChildOfContent() {
        contentContainer.getWindowVisibleDisplayFrame(contentAreaOfWindowBounds);
        int usableHeightNow = contentAreaOfWindowBounds.height()
                + activity.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._25sdp);

        if (usableHeightNow != usableHeightPrevious) {
            setHeightWindow(usableHeightNow);
        }
    }

    private void setHeightWindow(int usableHeightNow){
        rootViewLayout.height = usableHeightNow;
        rootView.layout(0, 0, 0, contentAreaOfWindowBounds.bottom);
        rootView.requestLayout();
        usableHeightPrevious = usableHeightNow;
    }
}