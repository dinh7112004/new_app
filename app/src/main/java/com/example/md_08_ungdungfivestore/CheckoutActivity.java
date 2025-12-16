package com.example.md_08_ungdungfivestore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.models.Address;
import com.example.md_08_ungdungfivestore.models.CartItem;
import com.example.md_08_ungdungfivestore.models.CartResponse;
import com.example.md_08_ungdungfivestore.models.OrderRequest;
import com.example.md_08_ungdungfivestore.models.OrderResponse;
import com.example.md_08_ungdungfivestore.models.PaymentRequest;
import com.example.md_08_ungdungfivestore.models.PaymentResponse;
import com.example.md_08_ungdungfivestore.services.ApiClientCart;
import com.example.md_08_ungdungfivestore.services.ApiClientYeuThich;
import com.example.md_08_ungdungfivestore.services.CartService;
import com.example.md_08_ungdungfivestore.services.OrderService;
import com.example.md_08_ungdungfivestore.services.PaymentService;
import com.example.md_08_ungdungfivestore.utils.OrderManager;
import com.example.md_08_ungdungfivestore.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "CheckoutActivity";

    private EditText edtName, edtPhone, edtStreet, edtProvince, edtDistrict, edtWard;
    private Button btnPlaceOrder;
    private TextView tvSubtotal, tvShippingFee, tvTotalAmount;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCOD, rbVNPAY;

    private OrderManager orderManager;
    private CartService cartService;
    private PaymentService paymentService;
    private SessionManager sessionManager;
    private List<CartItem> cartItems = new ArrayList<>();
    private final double SHIPPING_FEE = 30000;

    private double currentTotalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        anhXa();

        sessionManager = SessionManager.getInstance(this);

        OrderService orderService = ApiClientYeuThich.getClient(this).create(OrderService.class);
        orderManager = new OrderManager(orderService);
        cartService = ApiClientCart.getCartService(this);
        paymentService = ApiClientYeuThich.getClient(this).create(PaymentService.class);

        btnPlaceOrder.setEnabled(false);
        fetchCartItems();

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void anhXa() {
        edtName = findViewById(R.id.edtReceiverName);
        edtPhone = findViewById(R.id.edtPhone);
        edtStreet = findViewById(R.id.edtStreet);
        edtProvince = findViewById(R.id.edtProvince);
        edtDistrict = findViewById(R.id.edtDistrict);
        edtWard = findViewById(R.id.edtWard);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbCOD = findViewById(R.id.rbCOD);
        rbVNPAY = findViewById(R.id.rbVNPAY);
    }

    private void fetchCartItems() {
        if (cartService == null) return;

        cartService.getCartItems().enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(@NonNull Call<CartResponse> call, @NonNull Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> newItems = response.body().getItems();

                    cartItems.clear();
                    if (newItems != null) {
                        cartItems.addAll(newItems);
                    }

                    if (cartItems.isEmpty()) {
                        Toast.makeText(CheckoutActivity.this, "Giỏ hàng trống. Đang quay lại...", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    updateSummary();
                    btnPlaceOrder.setEnabled(true);

                } else {
                    Toast.makeText(CheckoutActivity.this, "Lỗi tải dữ liệu giỏ hàng: " + response.code(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartResponse> call, @NonNull Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối mạng khi tải giỏ hàng.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void updateSummary() {
        if (cartItems.isEmpty()) {
            return;
        }

        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }

        currentTotalAmount = subtotal + SHIPPING_FEE;

        tvSubtotal.setText(String.format("%,.0f VNĐ", subtotal));
        tvShippingFee.setText(String.format("%,.0f VNĐ", SHIPPING_FEE));
        tvTotalAmount.setText(String.format("%,.0f VNĐ", currentTotalAmount));
    }

    private void placeOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống. Không thể đặt hàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentTotalAmount <= 0) {
            Toast.makeText(this, "Không thể xác định tổng tiền. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String street = edtStreet.getText().toString().trim();
        String province = edtProvince.getText().toString().trim();
        String district = edtDistrict.getText().toString().trim();
        String ward = edtWard.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || street.isEmpty() || province.isEmpty() || district.isEmpty() || ward.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đủ thông tin địa chỉ.", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentMethod;
        int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedId == R.id.rbCOD) {
            paymentMethod = "cash";
        } else if (selectedId == R.id.rbVNPAY) {
            paymentMethod = "vnpay";
        } else {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = "Giao hàng giờ hành chính";
        Address shippingAddress = new Address(name, phone, street, ward, district, province);
        OrderRequest request = new OrderRequest(
                shippingAddress,
                paymentMethod,
                note,
                cartItems,
                SHIPPING_FEE,
                currentTotalAmount
        );

        btnPlaceOrder.setEnabled(false);
        Toast.makeText(this, "Đang xử lý đơn hàng...", Toast.LENGTH_LONG).show();

        if (paymentMethod.equals("vnpay")) {
            createVnPayOrder(request);
        } else {
            createCashOrder(request);
        }
    }

    private void createCashOrder(OrderRequest request) {
        orderManager.createOrder(request, new OrderManager.OrderCallback() {
            @Override
            public void onSuccess(OrderResponse orderResponse) {
                handleOrderSuccess(orderResponse.getOrderId());
            }

            @Override
            public void onError(String error) {
                handleOrderFailure(error);
            }
        });
    }

    private void createVnPayOrder(OrderRequest request) {
        orderManager.createOnlineOrder(request, new OrderManager.OrderCallback() {
            @Override
            public void onSuccess(OrderResponse orderResponse) {
                String orderId = orderResponse.getOrderId();
                String userId = sessionManager.getUserId();

                if (userId == null || userId.isEmpty()) {
                    handleOrderFailure("User not logged in. Cannot process VNPAY payment.");
                    btnPlaceOrder.setEnabled(true);
                    return;
                }

                PaymentRequest paymentRequest = new PaymentRequest(orderId, currentTotalAmount, userId, "Thanh toan don hang " + orderId);

                paymentService.createPayment(paymentRequest).enqueue(new Callback<PaymentResponse>() {
                    @Override
                    public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PaymentResponse paymentResponse = response.body();
                            if (paymentResponse.isSuccess()) {
                                String paymentUrl = paymentResponse.getPaymentUrl();
                                if (paymentUrl != null && !paymentUrl.isEmpty()) {
                                    Log.d(TAG, "VNPAY URL received: " + paymentUrl);
                                    Intent intent = new Intent(CheckoutActivity.this, VnPayActivity.class);
                                    intent.putExtra("paymentUrl", paymentUrl);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    handleOrderFailure("VNPAY URL is empty in the successful response.");
                                }
                            } else {
                                String serverMessage = paymentResponse.getMessage();
                                handleOrderFailure("Server failed to create VNPAY URL: " + (serverMessage != null ? serverMessage : "Unknown reason."));
                            }
                        } else {
                            String errorBodyString = "No error body";
                            try {
                                if (response.errorBody() != null) {
                                    errorBodyString = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body for VNPAY", e);
                            }
                            handleOrderFailure("Failed to request VNPAY URL. Code: " + response.code() + ", Message: " + response.message() + ", Body: " + errorBodyString);
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentResponse> call, Throwable t) {
                        handleOrderFailure("Network error while requesting VNPAY URL: " + t.getMessage());
                    }
                });
            }

            @Override
            public void onError(String error) {
                handleOrderFailure(error);
            }
        });
    }

    private void handleOrderSuccess(String orderId) {
        btnPlaceOrder.setEnabled(true);
        Log.d(TAG, "Order placed successfully. ID: " + orderId);

        setResult(Activity.RESULT_OK);

        Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
        intent.putExtra("orderId", orderId);
        startActivity(intent);

        finish();
    }

    private void handleOrderFailure(String error) {
        btnPlaceOrder.setEnabled(true);
        Log.e(TAG, "Order placement failed: " + error);

        new AlertDialog.Builder(CheckoutActivity.this)
            .setTitle("Đặt hàng thất bại")
            .setMessage(error)
            .setPositiveButton(android.R.string.ok, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }
}
