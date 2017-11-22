package app.com.myapplication;


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
    int mcount = 1;//实际初始个数
    private TextView count;//中间显示的个数
    JJLicense jjLicense;//接口

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
        //获得子布局
        View view = LayoutInflater.from(context).inflate(R.layout.countview, null);
        Button gsJian = view.findViewById(R.id.gs_jian);//减
        count = view.findViewById(R.id.count);//中间显示的个数
        Button gsJia = view.findViewById(R.id.gs_jia);//加

        gsJian.setOnClickListener(new OnClickListener() {//减号的点击事件
            @Override
            public void onClick(View view) {
                mcount--;//实际个数减 1
                if (mcount > 0) {//判断  个数>0  的时候
                    count.setText("" + mcount);//把实际个数 赋给 显示的个数
                    jjLicense.jjclick(mcount);//改变中间显示的个数
                } else {  //个数不大于0
                    mcount = 1; //实际个数就是 1
                    jjLicense.jjclick(mcount);//改变中间显示的个数
                }
            }
        });
        gsJia.setOnClickListener(new OnClickListener() {//加号的点击事件
            @Override
            public void onClick(View view) {
                mcount++;//实际个数加 1
                count.setText("" + mcount);//把实际个数 赋给 显示的个数
                jjLicense.jjclick(mcount);//改变中间显示的个数
            }
        });
        addView(view);
    }

    public CountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    interface JJLicense {//修改中间显示的个数
        //mcount中间显示的个数
        void jjclick(int mcount);
    }
}
