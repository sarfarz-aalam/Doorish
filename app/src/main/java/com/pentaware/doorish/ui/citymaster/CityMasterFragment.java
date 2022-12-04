package com.pentaware.doorish.ui.citymaster;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.CommonVariables;
import com.pentaware.doorish.ShopFragment;
import com.pentaware.doorish.R;
import com.pentaware.doorish.ui.orders.OrderDetailFragment;

public class CityMasterFragment extends Fragment {

    private CityMasterViewModel mViewModel;
    private View mView;
    private TextView txtNoItemToDisplay;
    private GifImageView gifImageView;
    private RecyclerView shopRecycler;

    public static CityMasterFragment newInstance() {
        return new CityMasterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.city_master_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CityMasterViewModel.class);
        // TODO: Use the ViewModel

        txtNoItemToDisplay = mView.findViewById(R.id.noItemTodisplay);
        gifImageView = mView.findViewById(R.id.gifView);
        shopRecycler = mView.findViewById(R.id.shopRecycler);



        CommonVariables.selectedProduct = null;

        BaseFragment newFragment = ShopFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();


    }

    private void loadLocalShops(){
        //CommonVariables.deliveryAddress.City
    }

}
