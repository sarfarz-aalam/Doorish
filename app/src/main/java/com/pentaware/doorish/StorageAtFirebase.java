package com.pentaware.doorish;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pentaware.doorish.model.CapturePayment;

import java.io.File;
import java.io.IOException;

public class StorageAtFirebase {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context m_ctx;

    StorageReference mPathReference;

    public  StorageAtFirebase(Context ctx) {
        m_ctx = ctx;
    }



//    public void setImage(ImageView img, final EmployeeMaster employee){
//
//        mEmployee = employee;
//        // Reference to an image file in Cloud Storage
//        String sCompanyId = CommonVariables.loggedInUserDetails.CompanyId;
//        String userId = sCompanyId + "_" + employee.EmpId + ".jpg";
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        String sChildNode = sCompanyId + "/images/" + userId;
//        mPathReference = storageReference.child(sChildNode);
//        mimgView = img;
//
//        mPathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Glide
//                        .with(m_ctx)
//                        .load(uri) // the uri you got from Firebase
//                        .into(mimgView); //Your imageView variable
//                // Got the download URL for 'users/me/profile.png'
//
//                String imgFileName = commonMethods.getImageFileName(mEmployee, m_ctx);
//                File file = new File(imgFileName);
//                if(!file.exists()) {
//                    try {
//                        file.createNewFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                mPathReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        // Local temp file has been created
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle any errors
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//                //Toast.makeText(m_ctx, "Error showing image " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
}
