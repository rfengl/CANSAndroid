package my.com.cans.cansandroid.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import my.com.cans.cansandroid.activities.BaseActivity;

/**
 * Created by Rfeng on 14/10/2016.
 */

public class BaseFragment extends Fragment {
    protected BaseActivity mActivity;
    protected View mView;

    protected int getFragmentResourceId() {
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(getFragmentResourceId(), container, false);
        return mView;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//    }

    public void refresh(SwipeRefreshLayout swipeRefreshLayout) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof BaseActivity)
            mActivity = (BaseActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mActivity = null;
    }

    public int getPixel(int resId) {
        return mActivity.getPixel(resId);
    }
}
