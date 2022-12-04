package com.pentaware.doorish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterUser extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String TAG = "EmailAuth";
    private String email, password;

    EditText txtEmail, txtPwd, txtConfirmPwd;
    Button btnRegister;
    private ProgressBar gifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.txtRegisterEmail);
        txtPwd = findViewById(R.id.txtRegisterPwd);
        txtConfirmPwd = findViewById(R.id.txtConfimPwd);
        btnRegister = findViewById(R.id.btnRegisterUser);
        gifView = findViewById(R.id.progress_bar);

        txtConfirmPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                String pwd = txtPwd.getText().toString();
//                if(pwd == null || pwd.matches("")){
//                    return;
//                }
//
//                String confirmPwd = s.toString();
//                if(pwd.matches(confirmPwd)){
//                    btnRegister.setEnabled(true);
//                    btnRegister.setBackgroundResource(R.drawable.blue_button);
//                }

            }
        });


    }

    public void onRegister_Click(View view) {
        boolean errorFound  = false;
        String sEmail = txtEmail.getText().toString().trim();
        if(sEmail.equals("")){
            txtEmail.setError("Please enter email");
            errorFound = true;
        }

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!sEmail.matches(emailPattern))
        {
            txtEmail.setError("Please enter a valid email address");
            errorFound = true;
        }

        String sPwd = txtPwd.getText().toString().trim();
        String sConfirmPwd = txtConfirmPwd.getText().toString().trim();

        if(sPwd.equals("")){
            txtPwd.setError("Please enter password");
            errorFound = true;
        }

        if(sConfirmPwd.equals("")){
            txtConfirmPwd.setError("Please enter confirm password");
            errorFound = true;
        }

        if(!sPwd.equals("") && !sConfirmPwd.equals("")){
            if(!sPwd.matches(sConfirmPwd)){

                txtConfirmPwd.setError("Please enter the same password that you typed in password field");
                errorFound = true;

            }
        }



        if(errorFound)
        {
            return;
        }
        gifView.setVisibility(View.VISIBLE);
        createUser(txtEmail.getText().toString(), txtPwd.getText().toString());
    }

    private void createUser(String email, String password){
        CommonVariables.Email = email;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterUser.this, "Registration Successful.",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterUser.this, UserOtherDetails.class);
                            //prevent the user to come again to this screen in he presses back btton
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            gifView.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterUser.this, "Registration failed." + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            //  updateUI(null);
                        }

                        // ...
                    }
                });
    }
}


