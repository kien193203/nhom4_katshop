package FA22_PRO1121.poly.nhom4.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import FA22_PRO1121.poly.nhom4.Model.Request;
import FA22_PRO1121.poly.nhom4.OrderUserInformationActivity;
import FA22_PRO1121.poly.nhom4.R;
import FA22_PRO1121.poly.nhom4.Ultils.Common;
import FA22_PRO1121.poly.nhom4.ViewHolder.ViewHolder_Order_User;

public class WaitConfirmOrderUser_Fragment extends Fragment {

    RecyclerView recyclerView_wait_confirm_order_user;
    Query requestReference;
    DatabaseReference reference;

    FirebaseRecyclerOptions<Request> options;
    FirebaseRecyclerAdapter<Request, ViewHolder_Order_User> adapter;
    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_wait_confirm_order_user_, container, false);
        reference = FirebaseDatabase.getInstance().getReference("Request");
        requestReference = FirebaseDatabase.getInstance().getReference("Request").orderByChild("phone_status").equalTo(Common.currentUser.getPhone()+"_"+0);
        recyclerView_wait_confirm_order_user = root.findViewById(R.id.recyclerView_wait_confirm_order_user);
        recyclerView_wait_confirm_order_user.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        manager.setStackFromEnd(true);
        recyclerView_wait_confirm_order_user.setLayoutManager(manager);

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

                holder.btnCancel_Order.setOnClickListener(v -> {
                    Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_cancel_reason);
                    dialog.show();
                    EditText reason = dialog.findViewById(R.id.reason);
                    Button cancel = dialog.findViewById(R.id.btnCancel);
                    Button confirm = dialog.findViewById(R.id.btnOk);
                    cancel.setOnClickListener(v1 -> dialog.dismiss());
                    confirm.setOnClickListener(v12 -> {
                        if (reason.getText().toString().trim().isEmpty()) {
                            Toast.makeText(getActivity(), "Vui lòng nhập lý do hủy", Toast.LENGTH_SHORT).show();
                        } else {
                            Calendar calendar = Calendar.getInstance();
                            Map<String, Object> map = new HashMap<>();
                            map.put("dateCanceled", simpleDateFormat.format(calendar.getTime()));
                            map.put("status", 2);
                            map.put("phone_status", model.getPhone() + "_" + 2);
                            map.put("cancellation_reason",reason.getText().toString());

                            reference.child(adapter.getRef(holder.getAbsoluteAdapterPosition()).getKey()).updateChildren(map)
                                    .addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Bạn đã hủy đơn hàng thành công", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
                            dialog.dismiss();
                            adapter.onDataChanged();
                        }
                    });
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
        recyclerView_wait_confirm_order_user.setAdapter(adapter);
        return root;
    }
}