package my.com.cans.cansandroid.fragments.interfaces;

import my.com.cans.cansandroid.managers.BaseTableAdapter;

/**
 * Created by Rfeng on 29/06/2017.
 */

public interface OnBindViewHolderListener {
    void onBindViewHolder(BaseTableAdapter.ViewHolder holder, int position);
}
