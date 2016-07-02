package com.admin.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.Outlet;
import com.admin.freddyspeaks.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by verona1024.
 */
public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder> {

    private Context context;
    private int layoutResourceId;
    private List<SubscriptionInfo> subscriptionInfoList;
    Dao<Outlet, Integer> outletDao;
    SubscriptionSelectionListener subscriptionSelectionListener;

    public SubscriptionAdapter(Context context, int layoutResourceId, List<SubscriptionAdapter.SubscriptionInfo> subscriptionInfoList, SubscriptionSelectionListener subscriptionSelectionListener) {
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.subscriptionInfoList = subscriptionInfoList;
        this.subscriptionSelectionListener = subscriptionSelectionListener;

        try {
            outletDao = OpenHelperManager.getHelper(context, DBHelper.class).getCustomDao("Outlet");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent,false);
        SubscriptionViewHolder subscriptionViewHolder = new SubscriptionViewHolder(view);
        return subscriptionViewHolder;
    }

    @Override
    public void onBindViewHolder(SubscriptionViewHolder holder, int position) {
        final SubscriptionInfo currentSubscriptionInfo = subscriptionInfoList.get(position);
        holder.subscriptionNameTextView.setText(currentSubscriptionInfo.getName());
        holder.subscriptionPriceTextView.setText(String.format("₹ %.0f", currentSubscriptionInfo.getPrice()));
        holder.payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscriptionSelectionListener.onSubscriptionClicked(currentSubscriptionInfo);
                payNow(currentSubscriptionInfo.price, currentSubscriptionInfo.name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subscriptionInfoList.size();
    }

    private void payNow(double price, String name) {
        Outlet currentOutlet = null;
        QueryBuilder<Outlet, Integer> outletQueryBuilder = outletDao.queryBuilder();
        try {
            currentOutlet = outletQueryBuilder.queryForFirst();
        } catch (SQLException e) {
            Log.e("OutletDetailsActivity", "Outlet details fetch error");
        }

        PayUmoneySdkInitilizer.PaymentParam.Builder builder = new PayUmoneySdkInitilizer.PaymentParam.Builder();

        builder.setMerchantId(AppConstants.MERCHANT_ID);
        builder.setKey(AppConstants.PAYU_KEY); //Put your live KEY here
        builder.setSalt(AppConstants.PAYU_SALT); //Put your live SALT here

        builder.setIsDebug(false);
        builder.setDebugMerchantId(AppConstants.TEST_MERCHANT_ID);// Debug Merchant ID
        builder.setDebugKey(AppConstants.TEST_PAYU_KEY);// Debug Key
        builder.setDebugSalt(AppConstants.TEST_PAYU_SALT);// Debug Salt

        builder.setAmount(price);

        builder.setTnxId("0nf7");

        builder.setPhone(currentOutlet.getCellNumber());
        builder.setProductName("Subscription for" + name);
        builder.setFirstName(currentOutlet.getOutletName());
        builder.setEmail(currentOutlet.getEmail());

        builder.setsUrl("https://test.payumoney.com/mobileapp/payumoney/success.php");
        builder.setfUrl("https://test.payumoney.com/mobileapp/payumoney/failure.php");
        builder.setUdf1("Outlet code - " + currentOutlet.getOutletCode());
        builder.setUdf2("");
        builder.setUdf3("");
        builder.setUdf4("");
        builder.setUdf5("");

        PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();

        PayUmoneySdkInitilizer.startPaymentActivityForResult((Activity) context, paymentParam);
    }

    public static class SubscriptionInfo {
        private String name;
        private String months;
        private double price;

        public SubscriptionInfo(String name,String months, double price) {
            this.name = name;
            this.price = price;
            this.months = months;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getMonths() {
            return months;
        }
    }

    static class SubscriptionViewHolder extends RecyclerView.ViewHolder{

        public TextView subscriptionNameTextView;
        public TextView subscriptionPriceTextView;
        public Button payNowButton;

        public SubscriptionViewHolder(View view){
            super(view);
            subscriptionNameTextView = (TextView) view.findViewById(R.id.subscriptionNameTextView);
            subscriptionPriceTextView = (TextView) view.findViewById(R.id.subscriptionPriceTextView);
            payNowButton = (Button) view.findViewById(R.id.payNowButton);
        }
    }

    public interface SubscriptionSelectionListener {
        void onSubscriptionClicked(SubscriptionInfo subscriptionInfo);
    }
}
