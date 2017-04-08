package my.com.cans.cansandroid.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.managers.BaseViewAdapter;
import my.com.cans.cansandroid.objects.BaseFormField;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BaseViewFragment extends BaseFragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private BaseViewLabelWidthListener mLabelWidth;
    private OnListFragmentInteractionListener mListener;
    private OnListFragmentBuildModelListener mBuildModel;
    private OnListFragmentBuildItemsListener mBuildItems;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BaseViewFragment() {
    }

    public static BaseViewFragment newInstance(int columnCount) {
        BaseViewFragment fragment = new BaseViewFragment();
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

    RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baseview_list, container, false);
        if (view instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) view;
        }
        return view;
    }

    @Override
    public void refresh(SwipeRefreshLayout swipeRefreshLayout) {
        super.refresh(swipeRefreshLayout);

        if (mRecyclerView != null) {
            Context context = mRecyclerView.getContext();
            if (mColumnCount <= 1)
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            else
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

            mRecyclerView.setAdapter(new BaseViewAdapter(buildItems(), mListener, mLabelWidth));
        }
    }

    protected Object buildModel() {
        if (mBuildModel != null)
            return mBuildModel.buildModel();
        return null;
    }

    protected BaseFormField buildItem(BaseFormField field) {
        return field;
    }

    protected List<BaseFormField> buildItems() {
        if (mBuildItems != null)
            return mBuildItems.buildItems();

        Object model = buildModel();
        if (model == null)
            return new ArrayList<>();

        List<BaseFormField> builtFields = new ArrayList<>();
        if (model != null) {
            Field[] fields = model.getClass().getFields();
            for (Field field : fields) {
                if (Modifier.isPublic(field.getModifiers()) &&
                        !Modifier.isStatic(field.getModifiers()) &&
                        !Modifier.isFinal(field.getModifiers())) {
                    builtFields.add(buildItem(new BaseFormField(mActivity, field, model)));
                }
            }
        }

        BaseFormField[] fieldArray = new BaseFormField[builtFields.size()];
        builtFields.toArray(fieldArray);
        Arrays.sort(fieldArray, new Comparator<BaseFormField>() {
            @Override
            public int compare(BaseFormField field1, BaseFormField field2) {
                return field1.order.compareTo(field2.order);
            }
        });

        return Arrays.asList(fieldArray);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Fragment parentFragment = this.getParentFragment();

        if (parentFragment instanceof OnListFragmentInteractionListener)
            mListener = (OnListFragmentInteractionListener) parentFragment;
        else if (context instanceof OnListFragmentInteractionListener)
            mListener = (OnListFragmentInteractionListener) context;
        if (parentFragment instanceof BaseViewLabelWidthListener)
            mLabelWidth = (BaseViewLabelWidthListener) parentFragment;
        else if (context instanceof BaseViewLabelWidthListener)
            mLabelWidth = (BaseViewLabelWidthListener) context;
        if (parentFragment instanceof OnListFragmentBuildModelListener)
            mBuildModel = (OnListFragmentBuildModelListener) parentFragment;
        else if (context instanceof OnListFragmentBuildModelListener)
            mBuildModel = (OnListFragmentBuildModelListener) context;
        if (parentFragment instanceof OnListFragmentBuildItemsListener)
            mBuildItems = (OnListFragmentBuildItemsListener) parentFragment;
        else if (context instanceof OnListFragmentBuildItemsListener)
            mBuildItems = (OnListFragmentBuildItemsListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mBuildModel = null;
        mBuildItems = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(BaseFormField item);
    }

    public interface OnListFragmentBuildItemsListener {
        List<BaseFormField> buildItems();
    }

    public interface OnListFragmentBuildModelListener {
        Object buildModel();
    }

    public interface BaseViewLabelWidthListener {
        int getLabelWidth();
    }
}
