package com.example.admin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.example.admin.freddyspeaks.R;

/**
 * Created by Admin on 3/19/2016.
 */
public class CustomFontEditText extends EditText {

    private Context context;
    private AttributeSet attrs;
    private int defStyle;

    private String fontFamilyFile;

    public CustomFontEditText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomFontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        TypedArray a = context.obtainStyledAttributes(this.attrs, R.styleable.custom_font);
        CharSequence s = a.getString(R.styleable.custom_font_fontFamilyFile);
        if (s != null) {
            this.setFontFamilyFile("fonts/"+s.toString());
            init();
        }
    }

    public CustomFontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.attrs = attrs;
        this.defStyle = defStyle;
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