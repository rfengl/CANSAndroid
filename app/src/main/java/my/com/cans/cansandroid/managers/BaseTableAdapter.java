package my.com.cans.cansandroid.managers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.controls.CustomImageView;
import my.com.cans.cansandroid.fragments.interfaces.OnTableInteractionListener;
import my.com.cans.cansandroid.objects.BaseTableItem;

public class BaseTableAdapter extends RecyclerView.Adapter<BaseTableAdapter.ViewHolder> {

    protected final List<BaseTableItem> mValues;
    protected final OnTableInteractionListener mListener;

    public BaseTableAdapter(List<BaseTableItem> values, OnTableInteractionListener listener) {
        mValues = values;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_basetable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        BaseTableItem value = mValues.get(position);
        holder.mItem = value;
        holder.mTitleView.setText(value.title);

        if (holder.mContentImage != null) {
            if (value.contentImage == null) {
                holder.mContentImage.setVisibility(View.GONE);
            } else {
                holder.mContentImage.setImageURI(value.contentImage);
                holder.mContentImage.setVisibility(View.VISIBLE);
            }
        }

        if (ValidateManager.isEmptyOrNull(value.details) && value.leftImage == null) {
            if (holder.mContentLayout != null)
                holder.mContentLayout.setVisibility(View.GONE);
        } else {
            if (ValidateManager.isEmptyOrNull(value.details))
                holder.mContentView.setText("");
            else
                holder.mContentView.setText(value.details);

            if (holder.mLeftImage != null) {
                if (value.leftImage == null)
                    holder.mLeftImage.setVisibility(View.GONE);
                else {
                    holder.mLeftImage.setVisibility(View.VISIBLE);
                    holder.mLeftImage.setImageURI(value.leftImage);
                }
            }

            if (holder.mContentLayout != null)
                holder.mContentLayout.setVisibility(View.VISIBLE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onTableItemSelected(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mValues == null)
            return 0;
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final View mContentLayout;
        public final TextView mTitleView;
        public final TextView mContentView;
        public final CustomImageView mLeftImage;
        public final CustomImageView mContentImage;
        public BaseTableItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentLayout = view.findViewById(R.id.content_layout);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mContentView = (TextView) view.findViewById(R.id.content);
            mLeftImage = (CustomImageView) view.findViewById(R.id.left_image);
            mContentImage = (CustomImageView) view.findViewById(R.id.content_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
