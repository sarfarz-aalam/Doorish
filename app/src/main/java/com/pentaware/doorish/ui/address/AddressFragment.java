package com.pentaware.doorish.ui.address;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.droidsonroids.gif.GifImageView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.doorish.AdapterAddressListing;
import com.pentaware.doorish.AddressListing;
import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.CommonVariables;

import com.pentaware.doorish.IAddressOperation;
import com.pentaware.doorish.Popup;
import com.pentaware.doorish.UserOtherDetails;
import com.pentaware.doorish.model.Address;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pentaware.doorish.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddressFragment extends BaseFragment implements IAddressOperation {

    private AddressViewModel mViewModel;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GifImageView gifImageView;

    private List<AddressListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    ImageButton btnEditAddress;

    private View mView;

    IAddressOperation event;

    public static AddressFragment newInstance() {
        return new AddressFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.address_fragment, container, false);
        event = this;
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        gifImageView = mView.findViewById(R.id.gifView);

        if (CommonVariables.loggedInUserDetails == null) {
            gifImageView.setVisibility(View.GONE);
            btnEditAddress.setVisibility(View.GONE);
            showPopup();
            return;
        }


        listItems = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.addressRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));


        LoadAllAddresses();

    }

    private void showPopup() {
        Intent intent = new Intent(getActivity(), Popup.class);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddressViewModel.class);
        // TODO: Use the ViewModel

        setAddress();
        Button btnAddAddress = (Button) mView.findViewById(R.id.btnAddAddress);
        btnEditAddress = mView.findViewById(R.id.btn_edit_address);
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        ImageButton btnAddNewAddress = mView.findViewById(R.id.btn_add_new_address);
        TextView txtAddNewAddress = mView.findViewById(R.id.txt_add_new_address);
        RelativeLayout layoutAddNewAddress = mView.findViewById(R.id.layout_add_new_address);

        btnAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewAddress();
            }
        });

        txtAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewAddress();
            }
        });

        layoutAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewAddress();
            }
        });

        btnEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAddress(CommonVariables.deliveryAddress);
            }
        });
    }

    public void addNewAddress() {
        Intent intent = new Intent(getActivity(), UserOtherDetails.class);
        intent.putExtra("change_address", true);
        startActivity(intent);
    }

    private void setAddress() {

        TextView txtName = (TextView) mView.findViewById(R.id.txtName_Address);
        TextView txtAddressLine1 = (TextView) mView.findViewById(R.id.txtAddresLine1_Address);
        TextView txtAddressLine2 = (TextView) mView.findViewById(R.id.txtAddresLine2_Address);
        TextView txtAddressLine3 = (TextView) mView.findViewById(R.id.txtAddresLine3_Address);
        TextView txtPincode = (TextView) mView.findViewById(R.id.txtPincode_Address);
        TextView txtCity = (TextView) mView.findViewById(R.id.txtCity_Address);
        TextView txtState = (TextView) mView.findViewById(R.id.txtState_Address);
        TextView txtPhone = (TextView) mView.findViewById(R.id.txtContact_Address);
        TextView txtLandmark = (TextView) mView.findViewById(R.id.txtLandmark_Address);
        Button btnAddAddress = (Button) mView.findViewById(R.id.btnAddAddress);
        LinearLayout layoutAddress = (LinearLayout) mView.findViewById(R.id.layoutAddress);
        if (CommonVariables.loggedInUserDetails == null) {
            txtName.setVisibility(View.GONE);
            txtAddressLine1.setVisibility(View.GONE);
            txtAddressLine2.setVisibility(View.GONE);
            txtAddressLine3.setVisibility(View.GONE);
            txtPincode.setVisibility(View.GONE);
            txtCity.setVisibility(View.GONE);
            txtState.setVisibility(View.GONE);
            txtPhone.setVisibility(View.GONE);
            txtLandmark.setVisibility(View.GONE);
            btnAddAddress.setVisibility(View.GONE);
            layoutAddress.setVisibility(View.GONE);
            return;
        }


        txtName.setText(CommonVariables.deliveryAddress.Name);
        txtAddressLine1.setText(CommonVariables.deliveryAddress.AddressLine1);
        txtAddressLine2.setText(CommonVariables.deliveryAddress.AddressLine2);
        txtAddressLine3.setText(CommonVariables.deliveryAddress.AddressLine3);
        txtPincode.setText(CommonVariables.deliveryAddress.Pincode);
        txtCity.setText(CommonVariables.deliveryAddress.City);
        txtState.setText(CommonVariables.deliveryAddress.State);
        txtPhone.setText("Contact No. " + CommonVariables.deliveryAddress.Phone);


        if (CommonVariables.deliveryAddress.Landmark.equals(""))
            txtLandmark.setVisibility(View.GONE);
        else
            txtLandmark.setText("Landmark: " + CommonVariables.deliveryAddress.Landmark);

    }


    private void LoadAddressOld() {


        for (Address address : CommonVariables.loggedInUserDetails.AddressList) {
            //if address is same as delivery address dont add it to card
            if (address.AddressId.equals(CommonVariables.deliveryAddress.AddressId))
                continue;
            AddressListing addressListing = new AddressListing(address);
            listItems.add(addressListing);
        }
        adapter = new AdapterAddressListing(listItems, mView.getContext(), event);
        recyclerView.setAdapter(adapter);

        gifImageView.setVisibility(View.GONE);
    }

    int addressSize = 0;
    int addressindex = 0;
    int defaultAddressIndex = -1;
    AddressListing defaultAddress;

    public void LoadAllAddresses() {

        addressSize = 0;
        addressindex = 0;
        db.collection("users").document(CommonVariables.loggedInUserDetails.customer_id).collection("Addresses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            addressSize = task.getResult().size();
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                addressindex++;
                                Address address = document.toObject(Address.class);
                                CommonVariables.loggedInUserDetails.AddressList.add(address);


                                AddressListing addressListing = new AddressListing(address);
                                listItems.add(addressListing);

                                if (address.AddressId.equals(CommonVariables.deliveryAddress.AddressId)) {
                                    defaultAddressIndex = addressindex;
                                    defaultAddress = addressListing;
                                    CommonVariables.selectedAddressByDefault = addressindex;
                                    Log.d("defaultAddress", String.valueOf(defaultAddressIndex));
                                }

                                if (addressindex == addressSize) {

//                                    if(defaultAddressIndex > -1){
//                                       Collections.swap(listItems, defaultAddressIndex, 0);
//
//                                    }

                                    adapter = new AdapterAddressListing(listItems, mView.getContext(), event);
                                    recyclerView.setAdapter(adapter);

                                    if (CommonVariables.selectedAddressByDefault > -1) {
                                        CommonVariables.selectedAddressByDefault--;
                                        recyclerView.scrollToPosition(CommonVariables.selectedAddressByDefault);
                                        Log.d("item_position", "index " + CommonVariables.selectedAddressByDefault);
                                    }


                                    gifImageView.setVisibility(View.GONE);

                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void MakeAddressDefault(final Address address) {


        db.collection("users").document(CommonVariables.m_sFirebaseUserId)
                .update(
                        "AddressId", address.AddressId
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonVariables.deliveryAddress = address;
                CommonVariables.loggedInUserDetails.AddressId = address.AddressId;
                Toast.makeText(getContext(), "Delivery address changed successfully", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if (CommonVariables.startedByActivity) {
                    getActivity().finish();
                }

//                setAddress();
//
//                listItems = new ArrayList<>();
//                LoadAllAddresses();
            }
        });


    }

    @Override
    public void editAddress(Address address) {
        Intent intent = new Intent(getActivity(), UserOtherDetails.class);
        intent.putExtra("edit_address", true);
        intent.putExtra("old_address", address);
        startActivity(intent);
        startActivity(intent);
    }

    @Override
    public void deleteAddress(Address address, int position) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Do you want to delete this address?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.collection("users").document(CommonVariables.loggedInUserDetails.customer_id)
                                .collection("Addresses").document(address.AddressId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                listItems.remove(position);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Address deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();



    }
}
