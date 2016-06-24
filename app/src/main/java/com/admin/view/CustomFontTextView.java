package com.admin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.admin.freddyspeaks.R;

/**
 * Created by Admin on 3/19/2016.
 */
public class CustomFontTextView extends TextView {

    private String fontFamilyFile;
    private AttributeSet attrs;

    public CustomFontTextView(Context context) {
        super(context);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        TypedArray a = context.obtainStyledAttributes(this.attrs, R.styleable.custom_font);
        CharSequence s = a.getString(R.styleable.custom_font_fontFamilyFile);
        if (s != null) {
            this.setFontFamilyFile("fonts/"+s.toString());
            init();
        }
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
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
