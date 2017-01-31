package com.momentum.stock.stockanalyzer.Fragments;

import android.app.Activity;
import android.app.Fragment;

/**
 * Created by TONG on 01/31/2017.
 */
public class BaseFragment extends Fragment {

    String fragmentName;

    public void setFragmentName(String name){
        this.fragmentName = name;
    }

    public String getFragmentName(){
        return this.fragmentName;
    }

    public void removeFragment(){
        removeFragment(false);
    }

    public void removeFragment(boolean shouldPopBackStack){
        try {
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
            if (shouldPopBackStack) {
                getActivity().getFragmentManager().popBackStack();
            }
            getActivity().getFragmentManager().executePendingTransactions();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void removeFragment(Activity activity) {
        try {
            activity.getFragmentManager().beginTransaction().remove(this).commit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
