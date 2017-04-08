package my.com.cans.cansandroid.activities;

/**
 * Created by Rfeng on 04/04/2017.
 */

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.controls.CustomButton;
import my.com.cans.cansandroid.fragments.BaseEditFragment;
import my.com.cans.cansandroid.fragments.BaseFragment;
import my.com.cans.cansandroid.fragments.interfaces.OnSubmitListener;

public class EditPageActivity extends BaseActivity implements BaseEditFragment.OnBuildModelListener {

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_edit_page;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this instanceof OnSubmitListener) {
            CustomButton doneButton = new CustomButton(this);
            doneButton.setText(getString(R.string.save));
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    validateForm((CustomButton) v, true);
                    BaseEditFragment fragment = (BaseEditFragment) getSupportFragmentManager().findFragmentById(R.id.base_edit);
                    fragment.validateForm(v, true);
                }
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            doneButton.setLayoutParams(params);

            LinearLayout buttonContainer = (LinearLayout) this.findViewById(R.id.button_container);
            buttonContainer.addView(doneButton);
        }
    }

    @Override
    protected void refresh(SwipeRefreshLayout swipeRefreshLayout) {
        super.refresh(swipeRefreshLayout);
        getEditFragment().refresh(null);
    }

    public BaseEditFragment getEditFragment() {
        return (BaseEditFragment) getSupportFragmentManager().findFragmentById(R.id.base_edit);
    }

    @Override
    public Object buildModel() {
        return null;
    }
}
