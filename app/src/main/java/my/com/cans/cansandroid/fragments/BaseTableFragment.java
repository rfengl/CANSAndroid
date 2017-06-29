package my.com.cans.cansandroid.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.fragments.interfaces.OnTableInteractionListener;
import my.com.cans.cansandroid.managers.BaseTableAdapter;
import my.com.cans.cansandroid.objects.BaseTableItem;

public class BaseTableFragment extends BaseFragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    protected OnTableInteractionListener mListener;
    protected OnTableBuildItemsListener mBuildItems;
    protected RecyclerView mRecycleView;
    ProgressDialog mProgress;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BaseTableFragment() {
    }

    public static BaseTableFragment newInstance(int columnCount) {
        BaseTableFragment fragment = new BaseTableFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basetable_list, container, false);

        this.showProgress(R.string.processing);

        if (view instanceof RecyclerView == false) {
            view = view.findViewById(R.id.list);
        }

        // Set the adapter
        if (view instanceof RecyclerView) {
            mRecycleView = (RecyclerView) view;
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1)
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            else
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        return view;
    }

    public void showProgress() {
        showProgress(null);
    }

    public void showProgress(boolean ifNotProgressing) {
        showProgress(null, ifNotProgressing);
    }

    public void showProgress(int resId) {
        showProgress(getString(resId));
    }

    public void showProgress(int resId, boolean ifNotProgressing) {
        showProgress(getString(resId), ifNotProgressing);
    }

    public void showProgress(String message) {
        showProgress(message, false);
    }

    public void showProgress(String message, boolean ifNotProgressing) {
        if (ifNotProgressing && mProgress != null) return;

        if (mProgress == null)
            mProgress = new ProgressDialog(this.getActivity());
        if (message == null)
            message = getString(R.string.processing);
        mProgress.setMessage(message);
        mProgress.setIndeterminate(false);
        mProgress.setCancelable(false);
        mProgress.show();
    }

    public void hideProgress() {
        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(null);
    }

    @Override
    public void refresh(SwipeRefreshLayout swipeRefreshLayout) {
        super.refresh(swipeRefreshLayout);
        mRecycleView.setAdapter(buildAdapter(buildItems(), mListener));
        this.hideProgress();
    }

    BaseTableAdapter mAdapter;

    protected BaseTableAdapter buildAdapter(List<BaseTableItem> items, OnTableInteractionListener listener) {
        mAdapter = new BaseTableAdapter(items, listener);
        return mAdapter;
    }

    public void addItems(List<BaseTableItem> items) {
        if (mAdapter != null) {
            mAdapter.addItems(items);
            mAdapter.notifyDataSetChanged();
        }
    }

    protected List<BaseTableItem> buildItems() {
        return mBuildItems.buildItems();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (this instanceof OnTableInteractionListener)
            mListener = (OnTableInteractionListener) this;
        else if (context instanceof OnTableInteractionListener)
            mListener = (OnTableInteractionListener) context;

        if (context instanceof OnTableBuildItemsListener)
            mBuildItems = (OnTableBuildItemsListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mBuildItems = null;
    }

    public interface OnTableBuildItemsListener {
        List<BaseTableItem> buildItems();
    }
}
