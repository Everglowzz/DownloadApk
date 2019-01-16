package demo.com.everglow.downloadapk;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;



/**
 * Created by Youga on 2015/11/4.
 */
public class BaseDialog extends Dialog {
    Context mContext;
    public BaseDialog(Context context) {
        super(context, R.style.custom_dialog);
        mContext = context;
    }

    protected void showToast(String text) {
        if (text != null && !TextUtils.isEmpty(text.trim()) && !"null".equals(text.trim()))
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int text) {
        showToast(mContext.getString(text));
    }
}
