package im.ehab.casesapp.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


/**
 * Created by baher on 01/03/2017.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public CustomTextView(Context context) {
        super(context);

        init(context);
    }

    public void init(Context context) {
        Typeface tf;
            tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Aljazeera.ttf");
        setTypeface(tf ,1);

    }




}
