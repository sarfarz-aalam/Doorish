package com.pentaware.doorish.ui.subcategories;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pentaware.doorish.AdapterSubCategory;
import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.ICategoryOperations;
import com.pentaware.doorish.ISubCategories;
import com.pentaware.doorish.R;
import com.pentaware.doorish.model.subcategories;
import com.pentaware.doorish.ui.products.ProductFragment;
import com.pentaware.doorish.ui.products.ProductFragmentArgs;

import java.util.HashMap;
import java.util.List;

public class SubCategories extends BaseFragment implements ISubCategories, ICategoryOperations {

    private SubCategoriesViewModel mViewModel;
    private View mView;
    private ISubCategories msubCategories;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static SubCategories newInstance() {
        return new SubCategories();
    }

    private HashMap<String, List<String>> subCategoriesMap = new HashMap<>();
    private String mCategory;
    private String mSearchLocal;
    private boolean m_bMrCity = false;
    private String mAreaPin;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.sub_categories_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SubCategoriesViewModel.class);
        msubCategories = this;
        // TODO: Use the ViewModel

//        List<String> lstSubCategories = new ArrayList<>();
//        lstSubCategories.add("Fridge");
//        lstSubCategories.add("TV");
//        lstSubCategories.add("Washing Machine");
//        lstSubCategories.add("Earphones");
//        lstSubCategories.add("Headphones");
//
//        subCategoriesMap.put("Electronics and Appliances", lstSubCategories);

        if(getArguments() != null)
        {
            String sCategory = ProductFragmentArgs.fromBundle(getArguments()).getCategory();
            mCategory = sCategory;
            getSubCategory(sCategory);

//            mSearchLocal = ProductFragmentArgs.fromBundle(getArguments()).getSearchlocal();
//            if(mSearchLocal.equals("mrcity")){
//                m_bMrCity = true;
//            }
            m_bMrCity = false;
            mAreaPin = CommonVariables.deliveryAddress.buyer_area_pin;

//            mAreaPin = ProductFragmentArgs.fromBundle(getArguments()).getAreapin();
//            if(mAreaPin == null){
//                mAreaPin = CommonVariables.deliveryAddress.buyer_area_pin;
//            }

        }



    }

    public void getSubCategory(String sCategory){

        DocumentReference docRef = db.collection("categories").document(sCategory);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        subcategories sc = document.toObject(subcategories.class);
                        List<String>lstCategories = sc.sub_categories;
                        List<String>lstUrl = sc.img_url;

                        RecyclerView recyclerView = mView.findViewById(R.id.sub_category_recycler);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        AdapterSubCategory adapter = new AdapterSubCategory(getContext(), lstCategories, lstUrl, SubCategories.this);
                        recyclerView.setAdapter(adapter);



//                        GridView gridview = (GridView) mView.findViewById(R.id.gridview);
//                        gridview.setAdapter(new ImageAdapter(mView.getContext(), lstCategories, lstUrl, msubCategories, mCategory));


                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onSubCategoryClick(String sCategory, String sSubCategory) {

//        if(m_bMrCity)
//        {
//            Fragment newFragment = ShopFragment.newInstance();
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//            Bundle bund1 = new Bundle();
//            bund1.putString("category", sCategory + "|" + sSubCategory + "|" + mAreaPin);
//
//            newFragment.setArguments(bund1);
//            transaction.hide(this);
//            transaction.add(R.id.nav_host_fragment, newFragment);
//
//            // transaction.replace(R.id.nav_host_fragment, newFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
//
//        }
//        else {
//            Fragment newFragment = ProductFragment.newInstance();
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//            Bundle bund1 = new Bundle();
//            bund1.putString("category", "subcategory|" + mCategory + "|" + sSubCategory + "|" + mSearchLocal);
//
//            newFragment.setArguments(bund1);
//            transaction.hide(this);
//            transaction.add(R.id.nav_host_fragment, newFragment);
//
//            // transaction.replace(R.id.nav_host_fragment, newFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
//        }
    }

    @Override
    public void getProductsByCategory(String category) {
        Log.d("category_clicked", "category_clicked");
        Fragment newFragment = ProductFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bund1 = new Bundle();
        bund1.putString("category", "subcategory|" + mCategory + "|" + category + "|" + mSearchLocal);

        newFragment.setArguments(bund1);
        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);

        // transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
