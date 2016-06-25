package com.admin.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.widget.EditText;

import com.admin.freddyspeaks.R;

/**
 * Created by Admin on 3/19/2016.
 */
public class CustomFontEditText extends TextInputEditText {

    private AttributeSet attrs;

    private String fontFamilyFile;

    public CustomFontEditText(Context context) {
        super(context);
        init();
    }

    public CustomFontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        TypedArray a = context.obtainStyledAttributes(this.attrs, R.styleable.custom_font);
        CharSequence s = a.getString(R.styleable.custom_font_fontFamilyFile);
        if (s != null) {
                this.setFontFamilyFile("fonts/" + s.toString());
                init();
        }
    }

    public CustomFontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.attrs = attrs;
        init();
    }

    private void init() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), getFontFamilyFile());
        this.setTypeface(font);
    }

    public String getFontFamilyFile() {
        return fontFamilyFile;
    }

    public void setFontFamilyFile(String fontFamilyFile) {
        this.fontFamilyFile = fontFamilyFile;
    }
}