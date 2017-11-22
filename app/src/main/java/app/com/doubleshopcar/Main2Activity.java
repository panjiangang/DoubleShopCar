package app.com.doubleshopcar;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import app.com.doubleshopcar.bean.ShopBean;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main2Activity extends AppCompatActivity {

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.qx_btn)
    TextView qxBtn;
    @BindView(R.id.zongjia)
    TextView zongjia;
    @BindView(R.id.geshu)
    TextView geshu;
    @BindView(R.id.js_btn)
    TextView jsBtn;
    List<ShopBean.DataBean.ListBean> data = new ArrayList<>();
    private List<ShopBean.DataBean> shop = new ArrayList<>();
    boolean allck = false;
    private Main2Activity.rvAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        //数据
        gethttp();
    }

    private void gethttp() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://120.27.23.105/product/getCarts?uid=100").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失败
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //成功
                Gson gson = new Gson();
                shop = gson.fromJson(response.body().string(), ShopBean.class).getData();
                for (int i = 0; i < shop.size(); i++) {
                    for (int j = 0; j < shop.get(i).getList().size(); j++) {
                        //全是商品
                        data.add(shop.get(i).getList().get(j));
                    }
                }

                getShou();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        getzongshuju();

                        rv.setLayoutManager(new LinearLayoutManager(Main2Activity.this, LinearLayoutManager.VERTICAL, false));
                        rvAdapter = new rvAdapter();
                        rv.setAdapter(rvAdapter);
                    }
                });
            }
        });
    }

    ///第一个
    private void getShou() {
        data.get(0).setisshop(0);
        for (int i = 1; i < data.size(); i++) {
            if (data.get(i).getSellerid() == data.get(i - 1).getSellerid()) {
                data.get(i).setisshop(1);
            } else {
                data.get(i).setisshop(0);
            }
        }
    }

    private void getzongshuju() {
        double sum = 0;
        int zongshu = 0;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getSelected() == 1) {
                int i1 = data.get(i).getNum();
                sum = sum + data.get(i).getBargainPrice() * i1;
                zongshu = zongshu + i1;
            }
        }
        zongjia.setText("总价:" + new DecimalFormat("0.00").format(sum));
        geshu.setText("共" + zongshu + "件商品");
    }

    @OnClick(R.id.qx_btn)
    public void onViewClicked() {
        allck = !allck;
        if (allck) {
            qxBtn.setBackgroundResource(R.drawable.shopcart_selected);
            for (int i = 0; i < data.size(); i++) {
                data.get(i).setSelected(1);
                data.get(i).setShopsele(true);
            }
        } else {
            qxBtn.setBackgroundResource(R.drawable.shopcart_unselected);
            for (int i = 0; i < data.size(); i++) {
                data.get(i).setSelected(0);
                data.get(i).setShopsele(false);
            }
        }
        getzongshuju();
        rvAdapter.notifyDataSetChanged();
    }

    public class rvAdapter extends RecyclerView.Adapter<rvAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if (data.get(position).getisshop() == 0) {
                holder.linearLayout.setVisibility(View.VISIBLE);
            } else {
                holder.linearLayout.setVisibility(View.GONE);
            }

            for (int i = 0; i < shop.size(); i++) {
                if (shop.get(i).getSellerid().equals("" + data.get(position).getSellerid())) {
                    holder.shopName.setText(shop.get(i).getSellerName());
                    break;
                }
            }

            if (data.get(position).getShopsele()) {
                holder.shopCb.setBackgroundResource(R.drawable.shopcart_selected);
            } else {
                holder.shopCb.setBackgroundResource(R.drawable.shopcart_unselected);
            }

            if (data.get(position).getSelected() == 1) {
                holder.spCb.setBackgroundResource(R.drawable.shopcart_selected);
            } else if (data.get(position).getSelected() == 0) {
                holder.spCb.setBackgroundResource(R.drawable.shopcart_unselected);
            }

            String s = data.get(position).getImages().split("\\|")[0];
            ImageLoader.getInstance().displayImage(s, holder.spIma);

            holder.spName.setText(data.get(position).getTitle());
            holder.spXj.setText("现价:" + data.get(position).getBargainPrice());
            holder.spYj.setText("原价:" + data.get(position).getPrice());

            holder.cv.setCount(data.get(position).getNum(), new CountView.JJLicense() {
                @Override
                public void jjclick(int mcount) {
                    data.get(position).setNum(mcount);
                    getzongshuju();
                }
            });

            //shangpu
            holder.shopCb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data.get(position).setShopsele(!data.get(position).getShopsele());

                    //子单选
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(position).getSellerid() == data.get(i).getSellerid()) {
                            if (data.get(position).getShopsele()) {
                                data.get(i).setSelected(1);
                            } else {
                                data.get(i).setSelected(0);
                            }
                        }
                    }

                    getall();
                    getzongshuju();
                    notifyDataSetChanged();
                }
            });

            //商品
            holder.spCb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (data.get(position).getSelected() == 0) {
                        data.get(position).setSelected(1);
                        data.get(position).setShopsele(true);
                    } else {
                        data.get(position).setSelected(0);
                        data.get(position).setShopsele(false);
                    }

                    //子全选
                    for (int j = 0; j < data.size(); j++) {
                        if (data.get(j).getisshop() == 0) {
                            for (int i = 0; i < data.size(); i++) {
                                if (data.get(j).getSellerid() == data.get(i).getSellerid() && data.get(i).getSelected() == 0) {
                                    data.get(j).setShopsele(false);
                                    break;
                                } else {
                                    data.get(j).setShopsele(true);
                                }
                            }
                        }
                    }

                    getall();
                    getzongshuju();
                    notifyDataSetChanged();
                }
            });

            //shanchu
            holder.shanchu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data.remove(position);
                    getShou();
                    getzongshuju();
                    notifyDataSetChanged();
                }
            });
        }

        ///全选
        private void getall() {
            boolean all = true;
            for (int j = 0; j < data.size(); j++) {
                if (data.get(j).getSelected() == 0) {
                    all = false;
                }
            }
            if (all) {
                qxBtn.setBackgroundResource(R.drawable.shopcart_selected);
            } else {
                qxBtn.setBackgroundResource(R.drawable.shopcart_unselected);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.shop_cb)
            Button shopCb;
            @BindView(R.id.shop_name)
            TextView shopName;
            @BindView(R.id.sp_name)
            TextView spName;
            @BindView(R.id.sp_cb)
            Button spCb;
            @BindView(R.id.sp_ima)
            ImageView spIma;
            @BindView(R.id.sp_xj)
            TextView spXj;
            @BindView(R.id.sp_yj)
            TextView spYj;
            @BindView(R.id.cv)
            CountView cv;
            private final LinearLayout linearLayout;
            private final ImageView shanchu;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                linearLayout = view.findViewById(R.id.ll);
                shanchu = view.findViewById(R.id.shanchu);
            }
        }
    }
}
