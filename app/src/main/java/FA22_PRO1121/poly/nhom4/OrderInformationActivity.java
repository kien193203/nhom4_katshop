package FA22_PRO1121.poly.nhom4;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import FA22_PRO1121.poly.nhom4.Adapter.InfoProductOrderAdapter;
import FA22_PRO1121.poly.nhom4.Model.Request;

public class OrderInformationActivity extends AppCompatActivity {

    int delivery = 30000;
    int sum_item = 0;
    TextView dateCreated, dateConfirm, dateCanceled, dateDelivery, dateSuccess, statusCreated, statusConfirm, statusCancel, statusDelivery, statusSuccess,
            name_user_order, phone_user_order, address_user_order, total_item, delivery_fee, total;
    Button btnConfirmOrder;
    Toolbar toolbar;
    Request request;
    EditText reson_cancel,edt_message;
    Spinner spinnerStatus;
    ConstraintLayout layoutReasonCancel;
    RecyclerView recyclerView_Order_detail;
    InfoProductOrderAdapter adapter;
    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);
        reference = FirebaseDatabase.getInstance().getReference("Request");
        edt_message = findViewById(R.id.edt_message);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        reson_cancel = findViewById(R.id.reson_cancel);
        layoutReasonCancel = findViewById(R.id.LayoutReasonCancel);
        dateCreated = findViewById(R.id.dateCreated);
        dateConfirm = findViewById(R.id.dateConfirm);
        dateCanceled = findViewById(R.id.dateCanceled);
        dateDelivery = findViewById(R.id.dateDelivery);
        dateSuccess = findViewById(R.id.dateSuccess);
        statusCreated = findViewById(R.id.statusCreated);
        statusConfirm = findViewById(R.id.statusConfirm);
        statusCancel = findViewById(R.id.statusCancel);
        statusDelivery = findViewById(R.id.statusDelivery);
        statusSuccess = findViewById(R.id.statusSuccess);
        name_user_order = findViewById(R.id.name_user_order);
        phone_user_order = findViewById(R.id.phone_user_order);
        address_user_order = findViewById(R.id.address_user_order);
        delivery_fee = findViewById(R.id.delivery_fee);
        total_item = findViewById(R.id.total_item);
        total = findViewById(R.id.total);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        recyclerView_Order_detail = findViewById(R.id.recyclerView_Order_detail);
        recyclerView_Order_detail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        request = (Request) getIntent().getBundleExtra("bundle").getSerializable("request_detail");
        String name_order = getIntent().getBundleExtra("bundle").getString("name_order");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(name_order);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        dateCreated.setText(request.getDateCreated());
        statusCreated.setText("Ch??? x??c nh???n");
        edt_message.setText(request.getMassage());
        if (!request.getDateConfirm().equals("")) { // xac nhan don hang
            dateConfirm.setText(request.getDateConfirm());
            statusConfirm.setText("X??c nh???n");
            dateConfirm.setVisibility(View.VISIBLE);
            statusConfirm.setVisibility(View.VISIBLE);
        }

        if (!request.getDateCanceled().equals("")) { // don hang bi huy
            statusCancel.setText("???? h???y");
            layoutReasonCancel.setVisibility(View.VISIBLE);
            dateCanceled.setText(request.getDateCanceled());
            reson_cancel.setText(request.getCancellation_reason());
            dateCanceled.setVisibility(View.VISIBLE);
            statusCancel.setVisibility(View.VISIBLE);
        }

        if (!request.getDateDelivery().equals("")) { // don hang ??ang giao
            statusDelivery.setText("??ang giao");
            dateDelivery.setText(request.getDateDelivery());
            statusDelivery.setVisibility(View.VISIBLE);
            dateDelivery.setVisibility(View.VISIBLE);
        }

        if (!request.getDateSuccess().equals("")) { // don hang thanh cong
            statusSuccess.setText("Th??nh c??ng");
            dateSuccess.setText(request.getDateSuccess());
            statusSuccess.setVisibility(View.VISIBLE);
            dateSuccess.setVisibility(View.VISIBLE);
            statusCancel.setVisibility(View.GONE);
            dateCanceled.setVisibility(View.GONE);
        }

        if (request.getStatus() == 0) {
            btnConfirmOrder.setVisibility(View.VISIBLE);
        } else {
            btnConfirmOrder.setVisibility(View.GONE);
        }
        adapter = new InfoProductOrderAdapter(this, request.getList());
        recyclerView_Order_detail.setAdapter(adapter);

        for (int i = 0; i < request.getList().size(); i++) {
            sum_item += request.getList().get(i).getPrice() * request.getList().get(i).getQuantity();
        }

        name_user_order.setText(request.getName());
        address_user_order.setText(request.getAddress());
        phone_user_order.setText(request.getPhone());

        total_item.setText(decimalFormat.format(sum_item) + "??");
        delivery_fee.setText(decimalFormat.format(delivery) + "??");
        total.setText(decimalFormat.format(request.getTotal()) + "??");


        btnConfirmOrder.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            Map<String, Object> map = new HashMap<>();
            map.put("dateConfirm", simpleDateFormat.format(calendar.getTime()));
            map.put("status", 1);
            map.put("phone_status",request.getPhone()+"_"+1);
            reference.child(name_order).updateChildren(map)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(OrderInformationActivity.this, "????n h??ng ???? ???????c x??c nh???n", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(OrderInformationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

}