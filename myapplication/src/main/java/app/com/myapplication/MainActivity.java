package app.com.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import app.com.myapplication.bean.ShopBean;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //RecyclerView
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.qx_btn)//全选按钮
            TextView qxBtn;
    @BindView(R.id.zongjia)//总价
            TextView zongjia;
    @BindView(R.id.geshu)//个数
            TextView geshu;
    @BindView(R.id.js_btn)//去结算
            TextView jsBtn;
    private ImageView qx_img;
    private List<ShopBean.DataBean.ListBean> data = new ArrayList<>();//商品的集合
    private List<ShopBean.DataBean> shop = new ArrayList<>();//商铺的集合
    boolean allck = false;
    private MainActivity.rvAdapter rvAdapter;//适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qx_img = (ImageView) findViewById(R.id.qx_img);
        qx_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allck = !allck;//全部选中,或者全部不选中
                if (allck) { //判断   是否是全选中状态
                    //是全选中状态    赋给全选按钮的背景资源是  shopcart_selected  这个图片
                    //qxBtn.setBackgroundResource(R.drawable.shopcart_selected);
                    qx_img.setBackgroundResource(R.drawable.shopcart_selected);
                    for (int i = 0; i < data.size(); i++) {
                        //给商品的选中状态都变成 1   (就是选中状态)
                        data.get(i).setSelected(1);
                        data.get(i).setShopsele(true);//给商铺的选中状态都变成 true   (就是选中状态)
                    }
                } else {
                    //不是全选中状态    赋给全选按钮的背景资源是  shopcart_unselected  这个图片
                    // qxBtn.setBackgroundResource(R.drawable.shopcart_unselected);
                    qx_img.setBackgroundResource(R.drawable.shopcart_unselected);
                    for (int i = 0; i < data.size(); i++) {
                        data.get(i).setSelected(0);//给商品的选中状态都变成 0  (就是不选中状态)
                        data.get(i).setShopsele(false);//给商铺的选中状态都变成 false   (就是不选中状态)
                    }
                }
                getzongshuju();//计算商品个数和总价
                rvAdapter.notifyDataSetChanged();//刷新适配器
            }
        });
        ButterKnife.bind(this);
        gethttp();
    }

    private void gethttp() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://120.27.23.105/product/getCarts?uid=100").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失败
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //成功
                Gson gson = new Gson();
                //把获得的值,赋给商铺
                shop = gson.fromJson(response.body().string(), ShopBean.class).getData();
                for (int i = 0; i < shop.size(); i++) {
                    for (int j = 0; j < shop.get(i).getList().size(); j++) {
                        //把商铺里的商品  添加到商品的集合里
                        data.add(shop.get(i).getList().get(j));
                    }
                }

                //排列 商品与商铺的显示位置
                getShou();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getzongshuju(); //计算商品个数和总价
                        //显示方式  VERTICAL   false倒序
                        rv.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
                        rvAdapter = new rvAdapter();
                        rv.setAdapter(rvAdapter);//给RecyclerView赋值
                    }
                });
            }
        });
    }

    //计算商品个数和总价
    private void getzongshuju() {
        double sum = 0;//初始价格总和
        int zongshu = 0;//初始商品总数
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getSelected() == 1) {//判断  商品是否是选中状态
                //获得选中商品数量
                int i1 = data.get(i).getNum();
                //价格总和 = 价格总和 + 廉价后的价格* 选中商品数量
                sum = sum + data.get(i).getBargainPrice() * i1;
                //总个数=总个数+获得选中商品的数量
                zongshu = zongshu + i1;
            }
        }
        //将得到的总价  转换成浮点型 赋给 zongjia(总价)
        zongjia.setText("总价:" + new DecimalFormat("0.00").format(sum));
        geshu.setText("共" + zongshu + "件商品");//将得到的总个数赋给  geshu(个数)
    }

    //商品与商铺的排列
    private void getShou() {
        data.get(0).setisshop(0);//获得第一个商品的第一个商铺
        for (int i = 1; i < data.size(); i++) {
            //判断  商品的商铺id是否一样
            if (data.get(i).getSellerid() == data.get(i - 1).getSellerid()) {
                //如果是一样，直接添加到前一个商品后面
                data.get(i).setisshop(i);
            } else {
                //如果不是一样  成为新的商铺的第一个商品
                data.get(i).setisshop(0);
            }
        }
    }

    @OnClick(R.id.qx_btn)
    public void onViewClicked() {
        allck = !allck;//全部选中,或者全部不选中
        if (allck) { //判断   是否是全选中状态
            //是全选中状态    赋给全选按钮的背景资源是  shopcart_selected  这个图片
            //qxBtn.setBackgroundResource(R.drawable.shopcart_selected);
            qx_img.setBackgroundResource(R.drawable.shopcart_selected);
            for (int i = 0; i < data.size(); i++) {
                //给商品的选中状态都变成 1   (就是选中状态)
                data.get(i).setSelected(1);
                data.get(i).setShopsele(true);//给商铺的选中状态都变成 true   (就是选中状态)
            }
        } else {
            //不是全选中状态    赋给全选按钮的背景资源是  shopcart_unselected  这个图片
            // qxBtn.setBackgroundResource(R.drawable.shopcart_unselected);
            qx_img.setBackgroundResource(R.drawable.shopcart_unselected);
            for (int i = 0; i < data.size(); i++) {
                data.get(i).setSelected(0);//给商品的选中状态都变成 0  (就是不选中状态)
                data.get(i).setShopsele(false);//给商铺的选中状态都变成 false   (就是不选中状态)
            }
        }
        getzongshuju();//计算商品个数和总价
        rvAdapter.notifyDataSetChanged();//刷新适配器

    }

    class rvAdapter extends RecyclerView.Adapter<rvAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //获得子布局
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            //判断 商品位置是否是商铺第一个
            if (data.get(position).getisshop() == 0) {
                //是第一个的时候 在商品上面  显示出商铺
                holder.linearLayout.setVisibility(View.VISIBLE);
            } else {
                //在商品上面  不显示出商铺
                holder.linearLayout.setVisibility(View.GONE);
            }

            for (int i = 0; i < shop.size(); i++) {
                // 判断   商铺中的商铺id和 商品中的商铺id
                if (shop.get(i).getSellerid().equals("" + data.get(position).getSellerid())) {
                    //把获得的商铺的名字  赋给 商铺   （获得商铺的名字）
                    holder.shopName.setText(shop.get(i).getSellerName());
                    break;
                }
            }
            //判断商品中  商铺的选中状态
            if (data.get(position).getShopsele()) {//商铺的选中状态是  true
                //商铺前面的选中框  赋给选中框的背景资源是  shopcart_selected  这个图片
                holder.shopCb.setBackgroundResource(R.drawable.shopcart_selected);
            } else {//商铺的选中状态是  false
                //商铺前面的选中框  赋给选中框的背景资源是  shopcart_unselected  这个图片
                holder.shopCb.setBackgroundResource(R.drawable.shopcart_unselected);
            }
            //判断   商品的选中状态是否是 1
            if (data.get(position).getSelected() == 1) {
                //商品前面的选中框  赋给选中框的背景资源是  shopcart_selected  这个图片
                holder.spCb.setBackgroundResource(R.drawable.shopcart_selected);
            } else if (data.get(position).getSelected() == 0) {//判断   商品的选中状态是否是 0
                //商品前面的选中框  赋给选中框的背景资源是  shopcart_selected  这个图片
                holder.spCb.setBackgroundResource(R.drawable.shopcart_unselected);
            }

            //图片的字符串截取   [0]指的是取截取的第一段字符串
            String s = data.get(position).getImages().split("\\|")[0];
            ImageLoader.getInstance().displayImage(s, holder.spIma);

            holder.spName.setText(data.get(position).getTitle());
            holder.spXj.setText("现价:" + data.get(position).getBargainPrice());
            holder.spYj.setText("原价:" + data.get(position).getPrice());

            //加减按钮
            holder.cv.setCount(data.get(position).getNum(), new CountView.JJLicense() {
                @Override
                public void jjclick(int mcount) {
                    data.get(position).setNum(mcount);//把增加或者减小了的值赋给  对应的商品
                    getzongshuju();//计算商品个数和总价
                }
            });

            //商品的点击事件
            holder.shopCb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //商品的商铺状态   true变false    false变true   反选
                    data.get(position).setShopsele(!data.get(position).getShopsele());

                    //子单选
                    for (int i = 0; i < data.size(); i++) {
                        //判断   position下标的商品的商铺id  ==  i 下标的商品的商铺id
                        if (data.get(position).getSellerid() == data.get(i).getSellerid()) {
                            if (data.get(position).getShopsele()) {//商品的商铺状态为 true
                                data.get(i).setSelected(1);//商铺下  商品的选中状态是  1
                            } else {
                                data.get(i).setSelected(0);//商铺下  商品的选中状态是  0
                            }
                        }
                    }

                    getall();//检查一下是否全选
                    getzongshuju();//计算商品个数和总价
                    notifyDataSetChanged();//刷新适配器
                }
            });

            //商品的点击事件
            holder.spCb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (data.get(position).getSelected() == 0) {//判断商品的状态是否是 0
                        data.get(position).setSelected(1);//将商品的状态变为 1
                        data.get(position).setShopsele(true);//商铺的状态变为  true
                    } else {
                        data.get(position).setSelected(0);//将商品的状态变为 0
                        data.get(position).setShopsele(false);//商铺的状态变为  false
                    }

                    //子全选
                    for (int j = 0; j < data.size(); j++) {
                        if (data.get(j).getisshop() == 0) {
                            for (int i = 0; i < data.size(); i++) {
                                if (data.get(j).getSellerid() == data.get(i).getSellerid() && data.get(i).getSelected() == 0) {
                                    data.get(j).setShopsele(false);//商铺的状态是  false
                                    break;
                                } else {
                                    data.get(j).setShopsele(true);//商铺的状态是  true
                                }
                            }
                        }
                    }

                    getall();//检查一下是否全选
                    getzongshuju();//计算商品个数和总价
                    notifyDataSetChanged();//刷新适配器
                }
            });

            //删除的监听事件
            holder.shanchu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //移除下标是position的数据
                    data.remove(position);

                    //重新展示商铺和商品
                    getShou();

                    //重新计算数量和价格
                    getzongshuju();

                    //刷新适配器
                    notifyDataSetChanged();
                }
            });
        }

        //全选
        private void getall() {
            boolean all = true;
            for (int j = 0; j < data.size(); j++) {
                //循环获得数组的选中状态  判断是否存在等于0的（未选中）
                if (data.get(j).getSelected() == 0) {
                    //如果有  则全选状态是未选中
                    all = false;
                }
            }

            //全选状态是true
            if (all) {
                qxBtn.setBackgroundResource(R.drawable.shopcart_selected);
            } else {
                //全选状态是false
                qxBtn.setBackgroundResource(R.drawable.shopcart_unselected);
            }
        }

        @Override
        public int getItemCount() {
            //数组的大小长度
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            //商铺前面的选中框
            @BindView(R.id.shop_cb)
            Button shopCb;
            //商铺的名字
            @BindView(R.id.shop_name)
            TextView shopName;
            //商品的名字
            @BindView(R.id.sp_name)
            TextView spName;
            //商品前面的选中框
            @BindView(R.id.sp_cb)
            Button spCb;
            //商品的图片
            @BindView(R.id.sp_ima)
            ImageView spIma;
            //现价
            @BindView(R.id.sp_xj)
            TextView spXj;
            //原价
            @BindView(R.id.sp_yj)
            TextView spYj;
            //加减号   组合控件
            @BindView(R.id.cv)
            CountView cv;

            private final LinearLayout linearLayout;
            //删除图片
            private final ImageView shanchu;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                //设置商家的显示或者隐藏   (同一个商家只显示一次,不多次显示)
                linearLayout = view.findViewById(R.id.ll);

                //删除图片
                shanchu = view.findViewById(R.id.shanchu);
            }
        }
    }
}
