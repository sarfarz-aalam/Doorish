package com.pentaware.doorish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pentaware.doorish.model.Address;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterAddressListing extends RecyclerView.Adapter<AdapterAddressListing.ViewHolder>  {

    private List<AddressListing> m_lstAddress;
    private Context m_Context;
    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    private int selectedSlot = -1;
    private boolean selectByDefault = true;


    IAddressOperation mAddressOperations;
    public AdapterAddressListing(List<AddressListing> lstAddress, Context ctx, IAddressOperation addressOperation) {
        m_lstAddress = lstAddress;
        m_Context = ctx;
        mAddressOperations = addressOperation;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_address_listing, parent, false);
        return new ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AddressListing listItem = m_lstAddress.get(position);
        Address address = listItem.getAddress();


        holder.txtName.setText(address.Name);
        holder.txtAddressLine1.setText(address.AddressLine1);
        holder.txtAddressLine2.setText(address.AddressLine2);
        holder.txtAddressLine3.setText(address.AddressLine3);
        holder.txtPincode.setText(address.Pincode);
        holder.txtContactNumber.setText(address.Phone);
        if(!address.Landmark.equals(""))
            holder.txtLandmark.setText("Landmark: " + address.Landmark);
        else
            holder.txtLandmark.setVisibility(View.GONE);

        holder.txtCity.setText(address.City);
        holder.txtState.setText(address.State);

        if(address.AddressLine2.equals("")){
            holder.txtAddressLine2.setVisibility(View.GONE);
        }

        if(address.AddressLine3.equals("")){
            holder.txtAddressLine3.setVisibility(View.GONE);
        }

        if(CommonVariables.loggedInUserDetails.AddressId.equals(address.AddressId) && selectByDefault){
            holder.layoutAddressOperations.setVisibility(View.GONE);
            holder.layoutDefaultAddressMarker.setVisibility(View.VISIBLE);
            holder.btnDeleteAddress.setVisibility(View.GONE);
            selectedSlot = position;
        }
//
//        if(holder.radioAddressSelected.isChecked()){
//            holder.layoutAddressOperations.setVisibility(View.VISIBLE);
//            selectedSlot = position;
//        }


        if (selectedSlot == position) {
            holder.radioAddressSelected.setChecked(true);
        }
        else {
            holder.radioAddressSelected.setChecked(false);
            holder.layoutAddressOperations.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return m_lstAddress.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtName;
        public TextView txtAddressLine1;
        public TextView txtAddressLine2;
        public TextView txtAddressLine3;
        public TextView txtLandmark;
        public TextView txtPincode;
        public TextView txtCity;
        public TextView txtState;
        public TextView txtContactNumber;
        public Button btnMakeDefaultAddress;
        public ImageButton btnDeleteAddress;
        public Button btnEditAddress;
        public RadioButton radioAddressSelected;
        public LinearLayout layoutAddressOperations;
        public LinearLayout layoutDefaultAddressMarker;


        private Context m_ctx;

        public ViewHolder(View itemView, Context ctx) {
            super(itemView);
            m_ctx = ctx;
            txtName = (TextView) itemView.findViewById(R.id.txtName_Address);
            txtAddressLine1 = (TextView) itemView.findViewById(R.id.txtAddresLine1_Address);
            txtAddressLine2 = (TextView) itemView.findViewById(R.id.txtAddresLine2_Address);
            txtAddressLine3 = (TextView) itemView.findViewById(R.id.txtAddresLine3_Address);
            txtLandmark = (TextView) itemView.findViewById(R.id.txtLandmark_Address);
            txtPincode = (TextView) itemView.findViewById(R.id.txtPincode_Address);
            txtCity = (TextView) itemView.findViewById(R.id.txtCity_Address);
            txtState = (TextView) itemView.findViewById(R.id.txtState_Address);
            txtContactNumber = (TextView) itemView.findViewById(R.id.txtContact_Address);
            btnMakeDefaultAddress = (Button) itemView.findViewById(R.id.btnChangeAddress);
            btnEditAddress = itemView.findViewById(R.id.btn_edit_address);
            btnDeleteAddress = itemView.findViewById(R.id.btn_delete_address);
            radioAddressSelected = itemView.findViewById(R.id.radio_select_address);
            layoutAddressOperations = itemView.findViewById(R.id.layout_address_operations);
            layoutDefaultAddressMarker = itemView.findViewById(R.id.layout_default_address_marker);

            btnMakeDefaultAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        AddressListing addressListing = m_lstAddress.get(pos);
                        Address address = addressListing.getAddress();
                        mAddressOperations.MakeAddressDefault(address);
                    }
                }
            });

            btnEditAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        AddressListing addressListing = m_lstAddress.get(pos);
                        Address address = addressListing.getAddress();
                        mAddressOperations.editAddress(address);
                    }
                }
            });

            btnDeleteAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        AddressListing addressListing = m_lstAddress.get(pos);
                        Address address = addressListing.getAddress();
                        mAddressOperations.deleteAddress(address, pos);
                    }
                }
            });

            radioAddressSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectAddress();
                }
            });


//
//            btnAddQty = (Button) itemView.findViewById(R.id.btnIncrease_Cart);
//            btnAddQty.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = getAdapterPosition();
//                    if (pos != RecyclerView.NO_POSITION) {
//                        CartListing cartListing = m_lstCart.get(pos);
//                        Cart cart = cartListing.getmCart();
//                        mCartOperatoins.DoCartOperations(Enums.CartOperations.AddQuantity, cart);
//                    }
//                }
//            });
//
//            btnDecreaseQty = (Button) itemView.findViewById(R.id.btnDecrease_Cart);
//            btnDecreaseQty.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = getAdapterPosition();
//                    if (pos != RecyclerView.NO_POSITION) {
//                        CartListing cartListing = m_lstCart.get(pos);
//                        Cart cart = cartListing.getmCart();
//                        mCartOperatoins.DoCartOperations(Enums.CartOperations.RemoveQuantity, cart);
//                    }
//                }
//            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    radioAddressSelected.setChecked(true);
                    selectAddress();
                }
            });




        }

        public void selectAddress(){
            selectedSlot = getAdapterPosition();
            if(radioAddressSelected.isChecked()){
                radioAddressSelected.setChecked(true);
                if(m_lstAddress.get(getAdapterPosition()).getAddress().AddressId.equals(CommonVariables.loggedInUserDetails.AddressId)){
                    layoutAddressOperations.setVisibility(View.GONE);
                }
                else {
                    layoutAddressOperations.setVisibility(View.VISIBLE);
                }
            }
            else{
                layoutAddressOperations.setVisibility(View.GONE);
            }

            selectByDefault = false;

            notifyDataSetChanged();
        }


        @Override
        public void onClick(View v) {

//            int pos = getAdapterPosition();
//            if (pos != RecyclerView.NO_POSITION) {
//                ProductListing productListing = m_lstProducts.get(pos);
//                Product product = productListing.getProduct();
//                mProductAdapterClickEvent.onProductClick(product);

//                Fragment newFragment = new SendFragment();
//                FragmentManager manager = ((AppCompatActivity)v.getContext()).getSupportFragmentManager();
//                Fragment fragment = manager.findFragmentById(R.id.nav_host_fragment);
//                FragmentTransaction transaction =  manager.beginTransaction();
//                transaction.replace(R.id.nav_host_fragment, newFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();

//                Intent intent = new Intent(m_ctx, EmployeeTabs.class);
//                intent.putExtra("employee", employee);
//                m_ctx.startActivity(intent);
        }
    }
}
