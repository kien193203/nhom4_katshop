package FA22_PRO1121.poly.nhom4.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import FA22_PRO1121.poly.nhom4.CheckOutActivity;
import FA22_PRO1121.poly.nhom4.Model.Request;
import FA22_PRO1121.poly.nhom4.OrderUserInformationActivity;
import FA22_PRO1121.poly.nhom4.R;
import FA22_PRO1121.poly.nhom4.Ultils.Common;
import FA22_PRO1121.poly.nhom4.ViewHolder.ViewHolder_Order_User;

public class Success_Order_User_Fragment extends Fragment {

    RecyclerView recyclerView_success_order_user;
    Query requestReference;
    FirebaseRecyclerOptions<Request> options;
    FirebaseRecyclerAdapter<Request, ViewHolder_Order_User> adapter;
    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_success__order__user_, container, false);
        requestReference = FirebaseDatabase.getInstance().getReference("Request")
                .orderByChild("phone_status").equalTo(Common.currentUser.getPhone()+"_"+4);
        recyclerView_success_order_user = root.findViewById(R.id.recyclerView_success_order_user);
        recyclerView_success_order_user.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        manager.setStackFromEnd(true);
        recyclerView_success_order_user.setLayoutManager(manager);

        options = new FirebaseRecyclerOptions.Builder<Request>().setQuery(requestReference, Request.class).build();
        adapter = new FirebaseRecyclerAdapter<Request, ViewHolder_Order_User>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Order_User holder, int position, @NonNull Request model) {
                int quantity_product = 0;
                holder.status_order.setText(Common.convertStatus(model.getStatus()));
                holder.size_product.setText("Size: " + model.getList().get(0).getSize());
                holder.color_product.setText("Color: " + model.getList().get(0).getColor());
                holder.name_product_in_order.setText(model.getList().get(0).getProductName());
                holder.price_product_order.setText(decimalFormat.format(model.getList().get(0).getPrice()) + "đ");
                Picasso.get().load(model.getList().get(0).getProductImage()).into(holder.image_product);
                holder.quantity_order.setText("x" + model.getList().get(0).getQuantity());
                for (int i = 0;i<model.getList().size();i++){
                    quantity_product += model.getList().get(i).getQuantity();
                }
                holder.btnCancel_Order.setVisibility(View.GONE);
                holder.btn_Buy_Again.setVisibility(View.VISIBLE);
                holder.total_product_order_user.setText(quantity_product + " sản phẩm");
                holder.total_order.setText(decimalFormat.format(model.getTotal()) + "đ");
                holder.setItemClickListener((view, position1, isLongClick) -> {
                    Intent i = new Intent(getActivity(), OrderUserInformationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("request_detail",adapter.getItem(holder.getAbsoluteAdapterPosition()));
                    bundle.putString("name_order",adapter.getRef(position1).getKey());
                    i.putExtra("bundle",bundle);
                    startActivity(i);
                });

                int finalQuantity_product = quantity_product;
                holder.btn_Buy_Again.setOnClickListener(v -> {
                    Intent i = new Intent(getActivity(), CheckOutActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("request_again", model);
                    bundle.putInt("quantity_again", finalQuantity_product);
                    bundle.putString("name_order", adapter.getRef(holder.getAbsoluteAdapterPosition()).getKey());
                    i.putExtra("buy_request_again", bundle);

                    startActivity(i);
                });
            }

            @NonNull
            @Override
            public ViewHolder_Order_User onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order_user, parent, false);
                return new ViewHolder_Order_User(v);
            }
        };
        adapter.startListening();
        recyclerView_success_order_user.setAdapter(adapter);
        return root;
    }
}