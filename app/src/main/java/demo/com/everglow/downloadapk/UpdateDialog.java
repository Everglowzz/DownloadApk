package demo.com.everglow.downloadapk;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by EverGlow on 2019/1/11 16:07
 */

public class UpdateDialog extends BaseDialog implements View.OnClickListener {


    TextView mUpdateContext;
    Button mUpdateCancel;
    Button mUpdateSubmit;
    UpdateDialogCallBack callBack;

    public UpdateDialog(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setContentView(R.layout.update_dialog);
        mUpdateContext = findViewById(R.id.update_context);
        mUpdateCancel = findViewById(R.id.update_cancel);
        mUpdateSubmit = findViewById(R.id.update_submit);
        mUpdateSubmit.setOnClickListener(this);
        mUpdateCancel.setOnClickListener(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }


    public void setUpdateDialogCallBack(UpdateDialogCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_cancel:
                dismiss();
                break;
            case R.id.update_submit:
                callBack.onUpdate();
                dismiss();
                break;
        }
    }

    public interface UpdateDialogCallBack {
        void onUpdate();
    }
    public void showDialog(String message){
        mUpdateContext.setText(message);
        show();
    }

    
    public Button getCancelButton() {
        return mUpdateCancel;
    }
}
