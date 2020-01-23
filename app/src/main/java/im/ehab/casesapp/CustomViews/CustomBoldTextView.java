package im.ehab.casesapp.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


/**
 * Created by 12 on 01/03/2017.
 */

public class CustomBoldTextView extends android.support.v7.widget.AppCompatTextView {

    public CustomBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public CustomBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public CustomBoldTextView(Context context) {
        super(context);

        init(context);
    }

    public void init(Context context) {
        Typeface tf;

            tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Aljazeera.ttf");
        setTypeface(tf, 1);

    }


}
