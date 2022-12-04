package com.pentaware.doorish.ui.chat;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pentaware.doorish.R;

import java.net.URLEncoder;

public class ChatFragment extends Fragment {

    private ChatViewModel mViewModel;
    private View mView;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.chat_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        // TODO: Use the ViewModel

        final EditText txtQuery = (EditText)mView.findViewById(R.id.txtQuery);
        Button btnSend = (Button)mView.findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   openWhatsAppNew(view);
                openWhatsappContact(txtQuery.getText().toString());
               // openWhatsAppNew(view, txtQuery.getText().toString());
            }
        });


    }

    public void openWhatsApp(View view, String txtToSend){
        PackageManager pm = mView.getContext().getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = txtToSend; // Replace with your own message.

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(mView.getContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }catch(Exception e){
            // e.printStacktrace();
        }

    }

    public void openWhatsAppNew(View view, String txtToSend){
        PackageManager pm= mView.getContext().getPackageManager();
        try {


            String toNumber = "919990379499"; // Replace with mobile phone number without +Sign or leading zeros, but with country code.
            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.



            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "" + toNumber + "?body=" + "Test Message"));
            sendIntent.putExtra(Intent.EXTRA_TEXT, txtToSend);
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(mView.getContext(),"it may be you dont have whats app",Toast.LENGTH_LONG).show();

        }
    }

    void openWhatsappContact(String txtToSend) {
        PackageManager packageManager = mView.getContext().getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            String phone = "919953783009";
            String message = txtToSend;
            String url = "https://api.whatsapp.com/send?phone="+ phone +"&text=" + URLEncoder.encode(message, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                mView.getContext().startActivity(i);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}