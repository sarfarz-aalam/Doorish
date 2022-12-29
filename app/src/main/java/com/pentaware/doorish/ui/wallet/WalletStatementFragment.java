package com.pentaware.doorish.ui.wallet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pentaware.doorish.AdapterViewOrderedProducts;
import com.pentaware.doorish.AdapterWalletStatement;
import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.WalletStatement;

import java.util.ArrayList;
import java.util.List;


public class WalletStatementFragment extends BaseFragment {

    private List<WalletStatement> walletList = new ArrayList<>();
    private FirebaseFirestore db;
    private RecyclerView recyclerViewWallet;
    private AdapterWalletStatement adapterWalletStatement;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet_statement, container, false);

        TextView txtWalletBalance = view.findViewById(R.id.txt_wallet_balance);
        recyclerViewWallet = view.findViewById(R.id.recycler_view_wallet);

        db = FirebaseFirestore.getInstance();


        String walletBalance = CommonVariables.rupeeSymbol + String.format("%.2f", CommonVariables.loggedInUserDetails.points);
        txtWalletBalance.setText(walletBalance);

        fetchWalletStatement();


        return view;
    }

    private void fetchWalletStatement() {
        db.collection("walletStatement").document(CommonVariables.loggedInUserDetails.customer_id).collection("statement").orderBy("date", Query.Direction.DESCENDING).
                get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(task.getResult() != null){
                            QuerySnapshot snapshot = task.getResult();

                            for(DocumentSnapshot st: snapshot){
                                WalletStatement statement = st.toObject(WalletStatement.class);
                                walletList.add(statement);
                            }
                        }
                        setUpRecyclerView();
                    }
                });
    }

    private void setUpRecyclerView() {
        recyclerViewWallet.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapterWalletStatement = new AdapterWalletStatement(getContext(), walletList);
        recyclerViewWallet.setAdapter(adapterWalletStatement);
    }
}