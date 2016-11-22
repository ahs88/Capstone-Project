package shopon.com.shopon.datamodel;

import android.content.Intent;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Created by Akshath on 10-11-2016.
 */
public class DetailEntry {
    private String key;
    private String value;
    private boolean errStatus;
    private String errorMsg;
    private boolean isEditable;
    private Method clickEventAction;
    private boolean isFocusable;
    private int inputType;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.key+":"+value;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }


    public boolean isErrStatus() {
        return errStatus;
    }

    public void setErrStatus(boolean errStatus) {
        this.errStatus = errStatus;
    }


    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }


    public Method getClickEventAction() {
        return clickEventAction;
    }

    public void setClickEventAction(Method clickEventAction) {
        this.clickEventAction = clickEventAction;
    }

    public boolean isFocusable() {
        return isFocusable;
    }

    public void setFocusable(boolean focusable) {
        isFocusable = focusable;
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }
}
