package com.admin.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
public class SubscriptionAdapter extends ArrayAdapter<SubscriptionAdapter.SubscriptionInfo> {

    private Context context;
    private int textViewResourceId;
    private ArrayList<SubscriptionInfo> objects;
    Dao<Outlet, Integer> outletDao;

    public static class SubscriptionInfo {
        private String name;
        private double price;

        public SubscriptionInfo(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }

    private static class ViewHolder{
        public TextView name;
        public TextView price;
        public Button payButton;
    }

    public SubscriptionAdapter(Context context, int textViewResourceId, List<SubscriptionAdapter.SubscriptionInfo> objects) {
        super(context, textViewResourceId, objects);

        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.objects = (ArrayList<SubscriptionInfo>) objects;

        try {
            outletDao = OpenHelperManager.getHelper(getContext(), DBHelper.class).getCustomDao("Outlet");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(textViewResourceId, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView) rowView.findViewById(R.id.textViewSubscriptionName);
            holder.price = (TextView) rowView.findViewById(R.id.textViewSubscriptionPrice);
            holder.payButton = (Button) rowView.findViewById(R.id.payNowButton);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        final SubscriptionInfo currentObject = objects.get(position);

        holder.name.setText(currentObject.getName());
        holder.price.setText(String.format("Rs. %.0f", currentObject.getPrice()));
        holder.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payNow(currentObject.price, currentObject.name);
            }
        });

        return rowView;
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

        builder.setKey(""); //Put your live KEY here
        builder.setSalt(""); //Put your live SALT here
        builder.setMerchantId(AppConstants.MERCHANT_ID);


        builder.setIsDebug(true);
        builder.setDebugKey("F4Vvyz");// Debug Key
        builder.setDebugMerchantId("4828127");// Debug Merchant ID
        builder.setDebugSalt("Z6cEj6SP");// Debug Salt

        builder.setAmount(price);

        builder.setTnxId("0nf7");


        builder.setPhone(currentOutlet.getCellNumber());

        builder.setProductName("Subscription for" + name);

        builder.setFirstName(currentOutlet.getOutletName());

        builder.setEmail(currentOutlet.getEmail());

        builder.setsUrl("https://mobiletest.payumoney.com/mobileapp/payumoney/success.php");
        builder.setfUrl("https://mobiletest.payumoney.com/mobileapp/payumoney/failure.php");
        builder.setUdf1("Outlet code - "+currentOutlet.getOutletCode());
        builder.setUdf2("");
        builder.setUdf3("");
        builder.setUdf4("");
        builder.setUdf5("");

        PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();

        PayUmoneySdkInitilizer.startPaymentActivityForResult((Activity) context, paymentParam);
    }
}
