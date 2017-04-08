package my.com.cans.cansandroid.managers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.fragments.BaseViewFragment;
import my.com.cans.cansandroid.objects.BaseFormField;

public class BaseViewAdapter extends RecyclerView.Adapter<BaseViewAdapter.ViewHolder> {

    private final List<BaseFormField> mValues;
    private final BaseViewFragment.OnListFragmentInteractionListener mListener;
    private BaseViewFragment.BaseViewLabelWidthListener mLabelWidth;

    public BaseViewAdapter(List<BaseFormField> items, BaseViewFragment.OnListFragmentInteractionListener listener, BaseViewFragment.BaseViewLabelWidthListener labelWidth) {
        mValues = items;
        mListener = listener;
        mLabelWidth = labelWidth;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_baseview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(holder.mItem.placeholder);
        holder.mContentView.setText(new Convert(holder.mItem.value).to(String.class));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public BaseFormField mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);

            if (mLabelWidth != null)
                mIdView.setMinimumWidth(mLabelWidth.getLabelWidth());
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
