package app.com.doubleshopcar;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author ddy
 */
public class CountView extends LinearLayout {
    int mcount = 1;
    private TextView count;
    JJLicense jjLicense;

    public int getCount() {
        return mcount;
    }

    public void setCount(int mcount, JJLicense jjLicense) {
        this.mcount = mcount;
        this.jjLicense = jjLicense;
        count.setText("" + mcount);
    }

    public CountView(Context context) {
        super(context);
    }

    public CountView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.countview, null);
        Button gsJian = view.findViewById(R.id.gs_jian);
        count = view.findViewById(R.id.count);
        Button gsJia = view.findViewById(R.id.gs_jia);

        gsJian.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mcount--;
                if (mcount > 0) {
                    count.setText("" + mcount);
                    jjLicense.jjclick(mcount);
                } else {
                    mcount = 1;
                    jjLicense.jjclick(mcount);
                }
            }
        });
        gsJia.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mcount++;
                count.setText("" + mcount);
                jjLicense.jjclick(mcount);
            }
        });
        addView(view);
    }

    public CountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    interface JJLicense {
        void jjclick(int mcount);
    }
}
