package com.pentaware.doorish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.pentaware.doorish.model.Address;
import com.pentaware.doorish.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import static android.view.View.GONE;

public class UserOtherDetails extends AppCompatActivity {


    FusedLocationProviderClient fusedLocationProviderClient;

    private final static int REQUEST_CODE = 100;

    public LocationManager locationManager;
    Address objAddress = new Address();

    private User currentUser;
    private FirebaseAuth mAuth;
    public String m_fcmToken;

    public ProgressBar progressBarLocation;
    public ImageView imgLocationDone;

    public RelativeLayout layoutGetLocation;
    EditText txtName;
    EditText txtPhone;
    EditText txtEmail;
    RadioButton rbMale;
    EditText txtAdddressLine1;
    EditText txtAdddressLine2;
    EditText txtAdddressLine3;
    EditText txtLandmark;
    EditText txtPincode;
    EditText txtCity;
    EditText txtState;
    EditText editTextDob;

    Calendar myCalendar;
    Button btnLogout;
    private String areaPin;


    private boolean m_bAddAddress = false;
    private boolean editAddress = false;
    private Address existingAddress;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_other_details);

        getFCMToken();

        Intent iin = getIntent();
        Bundle b = iin.getExtras();

        if (b != null) {

            if (b.get("change_address") != null) {
                m_bAddAddress = (boolean) b.get("change_address");
            }

            if (b.get("edit_address") != null) {
                editAddress = (boolean) b.get("edit_address");
                existingAddress = (Address) b.get("old_address");
            }

        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
            actionBar.hide();
        }

        btnLogout = findViewById(R.id.btnLogout);

        progressBarLocation = findViewById(R.id.location_progress_bar);
        imgLocationDone = findViewById(R.id.img_location_done);

        layoutGetLocation = findViewById(R.id.layout_get_location);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        txtName = findViewById(R.id.txtOhterDetailsName);
        if (!CommonVariables.UserName.equals("")) {
            txtName.setText(CommonVariables.UserName);
        }

        txtPhone = findViewById(R.id.txtOtherDetailsPhone);
        if (!CommonVariables.Phone.equals("")) {
            txtPhone.setText(CommonVariables.Phone);
        }

        txtEmail = findViewById(R.id.txtotherDetailsEmail);
        if (!CommonVariables.Email.equals("")) {
            txtEmail.setText(CommonVariables.Email);
        }

        editTextDob = findViewById(R.id.edit_text_dob);

        mAuth = FirebaseAuth.getInstance();

        rbMale = findViewById(R.id.rbGenderMale);
        txtAdddressLine1 = findViewById(R.id.txtAddresLine1);
        txtAdddressLine2 = findViewById(R.id.txtAddresLine2);
        txtAdddressLine3 = findViewById(R.id.txtAddresLine3);
        txtLandmark = findViewById(R.id.txtLandmark);
        txtPincode = findViewById(R.id.txtPInCode);
        txtCity = findViewById(R.id.txtCity);
        txtState = findViewById(R.id.txtState);
        Button btnSaveDetails = findViewById(R.id.btnSaveUserDetails);

        myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        editTextDob.setOnClickListener(v -> {
            new DatePickerDialog(UserOtherDetails.this, R.style.DatePickerTheme, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        if (editAddress) {
            m_bAddAddress = true;
            if (existingAddress != null) {
                txtName.setText(existingAddress.Name);
                txtPhone.setText(existingAddress.Phone);
                txtAdddressLine1.setText(existingAddress.AddressLine1);
                txtAdddressLine2.setText(existingAddress.AddressLine2);
                txtAdddressLine3.setText(existingAddress.AddressLine3);
                txtLandmark.setText(existingAddress.Landmark);
                txtPincode.setText(existingAddress.Pincode);
                if (existingAddress.dob != null) {
                    editTextDob.setText(existingAddress.dob);
                }
                txtCity.setText(existingAddress.City);
                txtState.setText(existingAddress.State);
                btnSaveDetails.setText("Update Address");
            }
        }


        if (m_bAddAddress) {
            btnLogout.setVisibility(GONE);
            txtEmail.setVisibility(GONE);

//            LinearLayout layoutGender = (LinearLayout)findViewById(R.id.layoutGender);
//            layoutGender.setVisibility(GONE);
        }


        //getting user location
        layoutGetLocation.setOnClickListener(view -> {
            Log.d("current_loc", "clicked");
            getUserLocation();
        });


    }

    private void getUserLocation() {

        progressBarLocation.setVisibility(View.VISIBLE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                    @NonNull
                    @Override
                    public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                        return null;
                    }

                    @Override
                    public boolean isCancellationRequested() {
                        return false;
                    }
                }).addOnSuccessListener(location -> {
                    if (location != null) {
                        objAddress.user_loc_long = location.getLongitude();
                        objAddress.user_loc_lat = location.getLatitude();
                        Log.d("current_loc", String.valueOf(location.getLatitude()) + " " + location.getLongitude());
                        progressBarLocation.setVisibility(GONE);
                        imgLocationDone.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(e -> {
                    imgLocationDone.setVisibility(GONE);
                    progressBarLocation.setVisibility(GONE);
                });
            } else {
                showGPSDisabledAlertToUser();
            }

//            fusedLocationProviderClient.getLastLocation()
//                    .addOnSuccessListener(new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            if (location != null){
//                                Log.d("current_loc", String.valueOf(location.getLatitude()) +" "+ location.getLongitude());
//                            }
//                        }
//                    });


        } else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(UserOtherDetails.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();

            } else {
                Toast.makeText(UserOtherDetails.this, "Please provide the required permission", Toast.LENGTH_SHORT).show();
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Location service is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> {
                            Intent callGPSSettingIntent = new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(callGPSSettingIntent);
                            imgLocationDone.setVisibility(GONE);
                            progressBarLocation.setVisibility(GONE);
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    private void getFCMToken() {
        //Get Firebase FCM token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                m_fcmToken = instanceIdResult.getToken();
            }
        });
    }

    public void onDateBtn_Click(View view) {
    }

    public boolean validateInput() {
        boolean bErrorFound = false;
        if (txtName.getText().toString().trim().equals("")) {
            txtName.setError("Name can't be empty");
            bErrorFound = true;
        }

        if (objAddress.user_loc_lat == 0 || objAddress.user_loc_long == 0) {
            bErrorFound = true;
            Toast.makeText(this, "User location is required", Toast.LENGTH_SHORT).show();
        }


        if (txtPhone.getText().toString().trim().equals("")) {
            txtPhone.setError("Phone number cannot be empty");
            bErrorFound = true;
        }

        if (txtPhone.getText().toString().trim().length() != 10) {
            txtPhone.setError("Phone number has to be 10 digits number");
            bErrorFound = true;
        }

        if (!m_bAddAddress) {
            if (txtEmail.getText().toString().trim().equals("")) {
                txtEmail.setError("Email address cannot be empty");
                bErrorFound = true;
            }
        }

        if (editTextDob.getText().toString().trim().equals("")) {
            editTextDob.setError("Date of Birth cannot be empty");
            bErrorFound = true;
        }

        if (txtAdddressLine1.getText().toString().trim().equals("")) {
            txtAdddressLine1.setError("Address Line1 cannot be empty");
            bErrorFound = true;
        }

//        if (txtAdddressLine2.getText().toString().trim().equals("")) {
//            txtAdddressLine2.setError("Address Line2 cannot be empty");
//            bErrorFound = true;
//        }
//
//        if (txtAdddressLine3.getText().toString().trim().equals("")) {
//            txtAdddressLine3.setError("Address Line3 cannot be empty");
//            bErrorFound = true;
//        }

        if (txtPincode.getText().toString().trim().equals("")) {
            txtPincode.setError("Pincode cannot be empty");
            bErrorFound = true;
        }

        if (txtCity.getText().toString().trim().equals("")) {
            txtCity.setError("City cannot be empty");
            bErrorFound = true;
        }


        if (txtLandmark.getText().toString().trim().equals("")) {
            txtLandmark.setError("Landmark cannot be empty");
            bErrorFound = true;
        }

        if (txtState.getText().toString().trim().equals("")) {
            txtState.setError("State cannot be empty");
            bErrorFound = true;
        }

        return bErrorFound;
    }

    public void onSaveOtherDetails_click(View view) {

        boolean errorFound = validateInput();

        if (errorFound) {
            return;
        }

        String userId = mAuth.getUid();


        if (!m_bAddAddress) {
            objAddress.AddressId = UUID.randomUUID().toString();

            User user = new User();

            String sGender = "Male";

            user.Active = true;
            user.Name = txtName.getText().toString();
            user.Phone = txtPhone.getText().toString();
            user.Email = txtEmail.getText().toString();
            user.AddressLine1 = txtAdddressLine1.getText().toString();
            user.AddressLine2 = txtAdddressLine2.getText().toString();
            user.AddressLine3 = txtAdddressLine3.getText().toString();
            user.Pincode = txtPincode.getText().toString();
            user.buyer_area_pin = user.Pincode.substring(0, 3);
            user.City = txtCity.getText().toString();
            user.Landmark = txtLandmark.getText().toString();
            user.State = txtState.getText().toString();
            user.dob = editTextDob.getText().toString();
            user.AddressId = objAddress.AddressId;
            user.points = 0;
            user.customer_id = userId;
            user.fcm = m_fcmToken;
            user.status = "approved";
            user.last_order_date = null;
            user.last_payment_status = null;
            user.referral_customer_id = null;
            user.first_order_id = null;
            user.referral_claimed = false;
            user.referee_fcm = null;


            user.referral_code = Long.toString(CommonMethods.getEpochTime());
            //   user.Active = true;

            if (rbMale.isChecked() == false) {
                sGender = "Female";
            }
            user.Gender = sGender;

            db.collection("users").document(userId).set(user);
            //   user.Active = true;

            if (rbMale.isChecked() == false) {
                sGender = "Female";
            }
            user.Gender = sGender;

            db.collection("users").document(userId).set(user);


            objAddress.Name = user.Name;
            objAddress.AddressLine1 = user.AddressLine1;
            objAddress.AddressLine2 = user.AddressLine2;
            objAddress.AddressLine3 = user.AddressLine3;
            objAddress.City = user.City;
            objAddress.Pincode = user.Pincode;
            objAddress.buyer_area_pin = user.Pincode.substring(0, 3);
            objAddress.Phone = user.Phone;
            objAddress.dob = user.dob;
            objAddress.State = user.State;
            objAddress.Landmark = user.Landmark;

            CommonVariables.deliveryAddress = objAddress;
            CommonVariables.loggedInUserDetails = user;
            CommonVariables.m_sFirebaseUserId = userId;

            db.collection("users").document(userId).collection("Addresses").document(objAddress.AddressId).set(objAddress);


            LaunchLandingPage();
        } else {

            btnLogout.setVisibility(GONE);

            objAddress.Name = txtName.getText().toString();
            objAddress.Phone = txtPhone.getText().toString();
            objAddress.AddressLine1 = txtAdddressLine1.getText().toString();
            objAddress.AddressLine2 = txtAdddressLine2.getText().toString();
            objAddress.AddressLine3 = txtAdddressLine3.getText().toString();
            objAddress.Pincode = txtPincode.getText().toString();
            objAddress.buyer_area_pin = objAddress.Pincode.substring(0, 3);
            objAddress.City = txtCity.getText().toString();
            objAddress.Landmark = txtLandmark.getText().toString();
            objAddress.State = txtState.getText().toString();
            objAddress.dob = editTextDob.getText().toString();
            objAddress.AddressId = UUID.randomUUID().toString();

            String addressId = objAddress.AddressId;

            if (editAddress) {
                addressId = existingAddress.AddressId;
                objAddress.AddressId = addressId;
            }

            db.collection("users").document(userId).collection("Addresses").document(addressId).set(objAddress);
            CommonVariables.loggedInUserDetails.AddressList.add(objAddress);
            if (editAddress) {
                Toast.makeText(this, "Address updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "New address added", Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(UserOtherDetails.this, LandingPage.class);
            //prevent the user to come again to this screen in he presses back btton
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);


        }

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        editTextDob.setText(sdf.format(myCalendar.getTime()));
    }

    private void LaunchLandingPage() {
        Intent intent = new Intent(UserOtherDetails.this, ReferAndEarn.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    public void onLogout_Click(View view) {
        mAuth.signOut();
        CommonVariables.loggedInUserDetails = null;
        CommonVariables.deliveryAddress = null;

        Intent intent = new Intent(UserOtherDetails.this, Login.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

    }


}

