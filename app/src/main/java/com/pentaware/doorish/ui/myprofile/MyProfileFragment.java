package com.pentaware.doorish.ui.myprofile;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.CapturePaymentTask;
import com.pentaware.doorish.CommonMethods;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.Popup;
import com.pentaware.doorish.R;
import com.pentaware.doorish.ShopFragment;
import com.pentaware.doorish.StorageAtFirebase;
import com.pentaware.doorish.model.CapturePayment;
import com.pentaware.doorish.ui.address.AddressFragment;
import com.pentaware.doorish.ui.subcategories.SubCategories;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;

public class MyProfileFragment extends BaseFragment{

    private MyProfileViewModel mViewModel;
    private View mView;
    private Uri selectedImage;
    private ImageView imgUser;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String m_sDOB = null;
    String m_sAnniversary = null;
    String m_sChildDOB = null;
    public static MyProfileFragment newInstance() {
        return new MyProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.my_profile_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {



        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MyProfileViewModel.class);

        FrameLayout frameLayout = mView.findViewById(R.id.frameFragment);
        if(CommonVariables.loggedInUserDetails == null){
            frameLayout.setVisibility(View.GONE);
            showPopup();
            return;
        }
        // TODO: Use the ViewModel
        TextView txtProfileDetails = mView.findViewById(R.id.txtProfileDetails);
        final Button btnSaveChanges = mView.findViewById(R.id.btnSaveChanges);

//        ImageView imgDOB = (ImageView) mView.findViewById(R.id.imgMyDOB);
//        final TextView txtMyDOB = (TextView) mView.findViewById(R.id.txtMyDOB);
//        if(CommonVariables.loggedInUserDetails.dob == null){
//            txtMyDOB.setText("Not Specified");
//        }
//        else{
//            m_sDOB = CommonVariables.loggedInUserDetails.dob;
//            txtMyDOB.setText(CommonVariables.loggedInUserDetails.dob);
//        }

//        imgDOB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // Get Current Date
//                final Calendar c = Calendar.getInstance();
//                int year = c.get(Calendar.YEAR);
//                int month = c.get(Calendar.MONTH);
//                int day = c.get(Calendar.DAY_OF_MONTH);
//
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(mView.getContext(),
//                        new DatePickerDialog.OnDateSetListener() {
//
//                            @Override
//                            public void onDateSet(DatePicker view, int year,
//                                                  int monthOfYear, int dayOfMonth) {
//
//                                String sDayOfMonth = Integer.toString(dayOfMonth);
//                                if(dayOfMonth < 10){
//                                    sDayOfMonth = "0" + sDayOfMonth;
//                                }
//
//                                String sMonth = CommonMethods.getMonthName(monthOfYear + 1);
//                                String sYear = Integer.toString(year);
//                                m_sDOB = sDayOfMonth + "-" + sMonth + "-" + sYear;
//                                txtMyDOB.setText(m_sDOB);
//                                btnSaveChanges.setVisibility(View.VISIBLE);
//
//                            }
//                        }, year, month, day);
//                datePickerDialog.show();
//            }
//        });

//        ImageView imgAnniversary = (ImageView) mView.findViewById(R.id.imgMyAnniv);
//        final TextView txtAnniv = (TextView) mView.findViewById(R.id.txtMyAnniversary);
//        if(CommonVariables.loggedInUserDetails.anniversary == null){
//            txtAnniv.setText("Not Specified");
//        }
//        else{
//            m_sAnniversary = CommonVariables.loggedInUserDetails.anniversary;
//            txtAnniv.setText(CommonVariables.loggedInUserDetails.anniversary);
//        }

//        imgAnniversary.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // Get Current Date
//                final Calendar c = Calendar.getInstance();
//                int year = c.get(Calendar.YEAR);
//                int month = c.get(Calendar.MONTH);
//                int day = c.get(Calendar.DAY_OF_MONTH);
//
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(mView.getContext(),
//                        new DatePickerDialog.OnDateSetListener() {
//
//                            @Override
//                            public void onDateSet(DatePicker view, int year,
//                                                  int monthOfYear, int dayOfMonth) {
//
//                                String sDayOfMonth = Integer.toString(dayOfMonth);
//                                if(dayOfMonth < 10){
//                                    sDayOfMonth = "0" + sDayOfMonth;
//                                }
//
//                                String sMonth = CommonMethods.getMonthName(monthOfYear + 1);
//                                String sYear = Integer.toString(year);
//                                m_sAnniversary = sDayOfMonth + "-" + sMonth + "-" + sYear;
//                                txtAnniv.setText(m_sAnniversary);
//                                btnSaveChanges.setVisibility(View.VISIBLE);
//
//                            }
//                        }, year, month, day);
//                datePickerDialog.show();
//            }
//        });

//        ImageView imgChildBirthday = (ImageView) mView.findViewById(R.id.imgChildBirthday);
//        final TextView txtChildBirthday = (TextView) mView.findViewById(R.id.txtChildBirthday);
//        if(CommonVariables.loggedInUserDetails.child_birthday == null){
//            txtChildBirthday.setText("Not Specified");
//        }
//        else{
//            m_sChildDOB = CommonVariables.loggedInUserDetails.child_birthday;
//            txtChildBirthday.setText(CommonVariables.loggedInUserDetails.child_birthday);
//        }

//        imgChildBirthday.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // Get Current Date
//                final Calendar c = Calendar.getInstance();
//                int year = c.get(Calendar.YEAR);
//                int month = c.get(Calendar.MONTH);
//                int day = c.get(Calendar.DAY_OF_MONTH);
//
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(mView.getContext(),
//                        new DatePickerDialog.OnDateSetListener() {
//
//                            @Override
//                            public void onDateSet(DatePicker view, int year,
//                                                  int monthOfYear, int dayOfMonth) {
//
//                                String sDayOfMonth = Integer.toString(dayOfMonth);
//                                if(dayOfMonth < 10){
//                                    sDayOfMonth = "0" + sDayOfMonth;
//                                }
//
//                                String sMonth = CommonMethods.getMonthName(monthOfYear + 1);
//                                String sYear = Integer.toString(year);
//                                m_sChildDOB = sDayOfMonth + "-" + sMonth + "-" + sYear;
//                                txtChildBirthday.setText(m_sChildDOB);
//                                btnSaveChanges.setVisibility(View.VISIBLE);
//
//                            }
//                        }, year, month, day);
//                datePickerDialog.show();
//            }
//        });

        DecimalFormat numberFormat = new DecimalFormat("#.00");
//        String walletBalance = numberFormat.format(CommonVariables.loggedInUserDetails.points);
        String walletBalance = String.format("%.0f", CommonVariables.loggedInUserDetails.points);

        String sData = "";
        sData += "Name: " + CommonVariables.loggedInUserDetails.Name + "<br />"
                 + "Wallet Balance: " + CommonVariables.rupeeSymbol + " " + walletBalance + "<br />"
                + "Phone Number: " + CommonVariables.loggedInUserDetails.Phone + "<br />"
                + "Email: " + CommonVariables.loggedInUserDetails.Email + "<br/> <br />"
                + "Address: <br/> " + CommonVariables.loggedInUserDetails.AddressLine1 + "<br />" + CommonVariables.loggedInUserDetails.AddressLine2 + "<br />" + CommonVariables.loggedInUserDetails.AddressLine3 + "<br/>"
                + "City: " + CommonVariables.loggedInUserDetails.City + "<br />"
                + "State: " + CommonVariables.loggedInUserDetails.State + "<br />"
                + "Pincode: " + CommonVariables.loggedInUserDetails.Pincode;


        txtProfileDetails.setText((Html.fromHtml(sData)));

//        Button btnFavouriteShops = (Button)mView.findViewById(R.id.btnFavouriteShops);
        final Fragment frag = this;
//        btnFavouriteShops.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Fragment newFragment = ShopFragment.newInstance();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//                Bundle bund1 = new Bundle();
//                bund1.putString("category", "favorite");
//
//                newFragment.setArguments(bund1);
//                transaction.hide(frag);
//                transaction.add(R.id.nav_host_fragment, newFragment);
//
//                // transaction.replace(R.id.nav_host_fragment, newFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//
//            }
//        });

//        Button btnAddresses = (Button)mView.findViewById(R.id.btnAddresses);
//        btnAddresses.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Fragment newFragment = AddressFragment.newInstance();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.hide(frag);
//                transaction.add(R.id.nav_host_fragment, newFragment);
//
//                // transaction.replace(R.id.nav_host_fragment, newFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//
//            }
//        });

        imgUser = mView.findViewById(R.id.imgUser);
        if(CommonVariables.loggedInUserDetails.img_url != null){

            Glide.with(mView.getContext())
                    .load(CommonVariables.loggedInUserDetails.img_url) // the uri you got from Firebase
                    .into(imgUser); //Your imageView variable
        }


        Button btnUploadImage = mView.findViewById(R.id.btnUploadImage);
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = CropImage.activity()
                        .getIntent(getContext());

                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSaveChanges.setVisibility(View.GONE);
                saveChanges();

            }
        });

    }

    public void saveChanges() {


        DocumentReference docOrder = db.collection("users").document(CommonVariables.m_sFirebaseUserId);

        docOrder
                .update(
                        "dob", m_sDOB,
                        "anniversary", m_sAnniversary,
                        "child_birthday", m_sChildDOB
                        )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully updated!");
                        // capturePayment(sPaymentId);
                        Toast.makeText(mView.getContext(), "Changes Saved Successfully", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error updating document", e);
                        Toast.makeText(mView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == -1){
                selectedImage = result.getUri();

                String sourceFile = getFileName(selectedImage);
                StorageAtFirebase storage = new StorageAtFirebase(mView.getContext());


                Uri destination = Uri.fromFile(new File(sourceFile));
                uploadFileToFirebase(destination);

                imgUser.setImageURI(selectedImage);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e = result.getError();
                Toast.makeText(mView.getContext(), "Possible Error is :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = mView.getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
//            int cut = result.lastIndexOf('/');
//            if (cut != -1) {
//                result = result.substring(cut + 1);
//            }
        }
        return result;
    }

    public void uploadFileToFirebase(Uri fileName){
        StorageReference storageRef = storage.getReference();

        //  Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        StorageReference riversRef = storageRef.child( "User_Images/" + CommonVariables.m_sFirebaseUserId + "/" + fileName.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(fileName);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        updateDb(downloadUrl);
//                       Toast.makeText(m_ctx, "Url - " + downloadUrl, Toast.LENGTH_LONG)
//                               .show();
                    }
                });
//                Toast.makeText(m_ctx, "Upload successful", Toast.LENGTH_LONG)
//                        .show();
            }
        });
    }

    private void updateDb(String url){
        DocumentReference docUser = db.collection("users").document(CommonVariables.m_sFirebaseUserId);

        docUser
                .update("img_url", url)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mView.getContext(), "Image Upload Successful", Toast.LENGTH_SHORT).show();
                        CommonVariables.loggedInUserDetails.img_url = url;
                        refreshImage();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error updating document", e);
                        Toast.makeText(mView.getContext(), "Image could not be uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void refreshImage() {
        Log.d("value_here", CommonVariables.loggedInUserDetails.img_url);
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        ImageView imgUser = (ImageView) hView.findViewById(R.id.imgUser);
        Glide.with(getActivity())
                .load(CommonVariables.loggedInUserDetails.img_url) // the uri you got from Firebase
                .into(imgUser); //Your imageView variable
    }

    private void showPopup(){
        Intent intent = new Intent(getActivity(), Popup.class);
        startActivity(intent);
    }


}