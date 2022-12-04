package com.pentaware.doorish;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pentaware.doorish.model.Orders;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AdapterViewOrderedProducts extends RecyclerView.Adapter<AdapterViewOrderedProducts.ViewHolder> {

    private Orders orders;
    private Context ctx;
    public IViewProductOperations iViewProductOperations;
    boolean cancel_expand = false;

    public AdapterViewOrderedProducts(Orders orders, Context context, IViewProductOperations iViewProductOperations) {
        this.orders = orders;
        this.ctx = context;
        this.iViewProductOperations = iViewProductOperations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_products_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String productImgUrl = orders.img_url_list.get(position);
        String productTitle = orders.product_titles.get(position);
        String productQty = "Qty: " + orders.product_qty_list.get(position);
        String productStatus = "";

        if (orders.product_status_list.get(position) != null) {
            if (orders.product_status_list.get(position).equals("cancelled")) {
                if (orders.cancelled_by.get(position) != null) {
                    if (orders.cancelled_by.get(position).equals("customer")) {
                        productStatus = "<font color=#ff0000>Cancelled by customer</font>";
                    } else if (orders.cancelled_by.get(position).equals("seller")) {
                        productStatus = "<font color=#ff0000>Cancelled by seller</font>";
                    } else {
                        productStatus = orders.product_status_list.get(position);
                    }
                }
            } else if (orders.product_status_list.get(position).equals("Refund Processed")) {
                productStatus = "<font color=#27AE60>Refund Processed</font>";
            } else {
                productStatus = orders.product_status_list.get(position);
            }
        }

        double totalPrice = orders.product_qty_list.get(position) * orders.product_offer_prices.get(position);

        String productPrice = "₹" + String.format("%.2f", totalPrice);

        holder.txtProductName.setText(productTitle);
        holder.txtProductQty.setText(productQty);
        holder.txtProductStatus.setText(Html.fromHtml(productStatus));
        holder.txtProductPrice.setText(productPrice);

        Glide.with(ctx)
                .load(productImgUrl)
                .into(holder.imgProduct);

        if (orders.Status.equals("Order received. Seller Confirmation pending.")) {
            holder.btnCancelProduct.setVisibility(View.VISIBLE);
        } else {
            holder.btnCancelProduct.setVisibility(View.GONE);
        }

        if (orders.product_status_list.get(position).equals("cancelled") || orders.product_status_list.get(position).equals("Refund Processed")) {
            holder.btnCancelProduct.setVisibility(View.GONE);
        }

        if (orders.product_status_list.get(position).equals("OK")) {
            holder.txtProductStatus.setVisibility(View.GONE);
        } else {
            holder.txtProductStatus.setVisibility(View.VISIBLE);
        }


        if (orders.Status.equals("Delivered") || orders.product_status_list.get(position).equals("delivered")) {
            holder.btnReviewProduct.setVisibility(View.VISIBLE);
            if (orders.delivery_date != null) {

                Date today = new Date();
                Date deliveryDate = orders.delivery_date;

                long duration = today.getTime() - deliveryDate.getTime();
                long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

                if (!(diffInDays > orders.product_returning_window_list.get(position)) && orders.product_returning_window_list.get(position) != 0) {
                    holder.radioGroupReturnReplace.setVisibility(View.VISIBLE);
                    holder.txtChooseReturnReplace.setVisibility(View.VISIBLE);
                } else {
                    holder.radioGroupReturnReplace.setVisibility(View.GONE);
                    holder.txtChooseReturnReplace.setVisibility(View.GONE);
                }
            }
        }

        if (orders.product_variants.size() > 0) {
            String sVariants = "";
            if (orders.product_variants.get(position) != null) {
                sVariants = orders.product_variants.get(position);
                String[] arrVariant = sVariants.split(":");
                String variantValue = arrVariant[1];
                productPrice = productPrice + "  (" + variantValue.trim() + ")";
                holder.txtProductPrice.setText(productPrice);
            }
        }


    }

    @Override
    public int getItemCount() {
        return orders.product_ids.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView txtProductName, txtProductStatus, txtProductQty, txtProductPrice, txtChooseReturnReplace;
        Button btnCancelProduct, btnReviewProduct, btnSubmitReturnReplace, btnSubmitCancellation;
        EditText editTextReturnReason, editTextCancellationReason;
        RadioButton radioButtonReturn, radioButtonReplace;
        RadioGroup radioGroupReturnReplace;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.img_product);
            txtProductName = itemView.findViewById(R.id.txt_product_title);
            txtProductStatus = itemView.findViewById(R.id.txt_product_status);
            txtProductQty = itemView.findViewById(R.id.txt_product_qty);
            txtProductPrice = itemView.findViewById(R.id.txt_product_price);
            btnCancelProduct = itemView.findViewById(R.id.btn_cancel_product);
            btnReviewProduct = itemView.findViewById(R.id.btn_review_product);
            radioGroupReturnReplace = itemView.findViewById(R.id.radio_group_return_replace);
            radioButtonReturn = itemView.findViewById(R.id.radio_button_return);
            radioButtonReplace = itemView.findViewById(R.id.radio_button_replacement);
            btnSubmitReturnReplace = itemView.findViewById(R.id.btn_submit_replace_return);
            editTextReturnReason = itemView.findViewById(R.id.edit_text_return_reason);
            txtChooseReturnReplace = itemView.findViewById(R.id.txt_choose_return_replace);
            editTextCancellationReason = itemView.findViewById(R.id.edit_text_cancellation_reason);
            btnSubmitCancellation = itemView.findViewById(R.id.btn_submit_cancel);

            btnCancelProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        //code for cancellation reason
//                        if (!cancel_expand) {
//                            editTextCancellationReason.setVisibility(View.VISIBLE);
//                            btnSubmitCancellation.setVisibility(View.VISIBLE);
//                            btnCancelProduct.setText("Stop Cancellation");
//                            cancel_expand = true;
//                        } else {
//                            editTextCancellationReason.setText("");
//                            editTextCancellationReason.setVisibility(View.GONE);
//                            btnSubmitCancellation.setVisibility(View.GONE);
//                            btnCancelProduct.setText("Cancel Product");
//                            cancel_expand = false;
//                        }

                        iViewProductOperations.cancelProduct(orders, pos, editTextCancellationReason.getText().toString());
                        btnCancelProduct.setVisibility(View.GONE);
                        editTextCancellationReason.setVisibility(View.GONE);
                        btnSubmitCancellation.setVisibility(View.GONE);
                    }
                }
            });

            btnSubmitCancellation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        iViewProductOperations.cancelProduct(orders, pos, editTextCancellationReason.getText().toString());
                        btnCancelProduct.setVisibility(View.GONE);
                        editTextCancellationReason.setVisibility(View.GONE);
                        btnSubmitCancellation.setVisibility(View.GONE);
                    }
                }
            });

            btnReviewProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        iViewProductOperations.reviewProduct(orders, pos);
                    }
                }
            });

            radioGroupReturnReplace.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (radioButtonReturn.isChecked()) {
                        editTextReturnReason.setVisibility(View.VISIBLE);
                        btnSubmitReturnReplace.setText("Submit Product Return");
                        btnSubmitReturnReplace.setVisibility(View.VISIBLE);
                    } else if (radioButtonReplace.isChecked()) {
                        editTextReturnReason.setVisibility(View.VISIBLE);
                        btnSubmitReturnReplace.setText("Submit Product Replacement");
                        btnSubmitReturnReplace.setVisibility(View.VISIBLE);
                    }
                }
            });

            btnSubmitReturnReplace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String returnReason = editTextReturnReason.getText().toString();
                    if (!returnReason.equals("")) {
                        if (btnSubmitReturnReplace.getText().toString().equals("Submit Product Return")) {
                            int pos = getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION) {
                                iViewProductOperations.returnProduct(orders, pos, returnReason);
                            }
                        } else if (btnSubmitReturnReplace.getText().toString().equals("Submit Product Replacement")) {
                            int pos = getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION) {
                                iViewProductOperations.replaceProduct(orders, pos, returnReason);
                            }
                        }
                    } else {
                        editTextReturnReason.setError("Enter a valid reason");
                    }

                }
            });
        }
    }

    public interface IViewProductOperations {
        void cancelProduct(Orders order, int index, String reason);

        void reviewProduct(Orders order, int index);

        void returnProduct(Orders order, int index, String reason);

        void replaceProduct(Orders order, int index, String reason);
    }
}

