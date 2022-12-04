package com.pentaware.doorish.ui.mrcity;

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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.pentaware.doorish.AdapterCategories;
import com.pentaware.doorish.AdapterSubCategory;
import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.ICategory;
import com.pentaware.doorish.ICategoryOperations;
import com.pentaware.doorish.R;
import com.pentaware.doorish.ui.subcategories.SubCategories;

import java.util.Arrays;
import java.util.List;

public class MrCityCategories extends BaseFragment implements ICategory {

    private MrCityCategoriesViewModel mViewModel;
    private View mView;
    private ICategory mICat;

    public Integer[] mThumbIds = {
            R.drawable.ic_groceries_new,
            R.drawable.ic_fruits_and_vegetables,
            R.drawable.ic_sweets,
            R.drawable.ic_lets_party_2
    };

    List<Integer> categoryImg = Arrays.asList(mThumbIds);

    public String[] mLabels = {
            "Groceries",
            "Vegetables and Fruites",
            "Sweets and Savouries",
            "Let's Party"
    };


    public static MrCityCategories newInstance() {
        return new MrCityCategories();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.mr_city_categories_fragment, container, false);
        return mView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MrCityCategoriesViewModel.class);
        // TODO: Use the ViewModel
        mICat = this;

//        GridView gridview = (GridView) mView.findViewById(R.id.gridview);
//        gridview.setAdapter(new MrCityImageAdapter(mView.getContext(), mICat));
        RecyclerView recyclerViewCategories = mView.findViewById(R.id.recycler_view_categories);
        recyclerViewCategories.setHasFixedSize(true);
        recyclerViewCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        AdapterCategories adapter = new AdapterCategories(getContext(), Arrays.asList(mLabels), categoryImg, MrCityCategories.this);
        recyclerViewCategories.setAdapter(adapter);
    }


    @Override
    public void onCategoryClick(String sCategory) {

        EditText txtAreaPin = mView.findViewById(R.id.txtAreaCode);
        String sAreaPin = txtAreaPin.getText().toString();
        if (!sAreaPin.trim().equals("")) {
            if (sAreaPin.length() != 3) {
                Toast.makeText(mView.getContext(), "Area Pin has to be exact 3 characters long", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Log.d("selected_value", "clicked" + sCategory);

        Fragment newFragment = SubCategories.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Bundle bund1 = new Bundle();
        bund1.putString("category", sCategory);
        //bund1.putString("searchlocal", "mrcity");
        if (!txtAreaPin.getText().toString().equals("")) {
            bund1.putString("areapin", txtAreaPin.getText().toString());
        }


        newFragment.setArguments(bund1);
        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);

        // transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }


}