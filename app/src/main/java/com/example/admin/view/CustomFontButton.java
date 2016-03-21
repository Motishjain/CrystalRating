package com.example.admin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.example.admin.freddyspeaks.R;

/**
 * Created by Admin on 3/21/2016.
 */
public class CustomFontButton extends Button {


    private String fontFamilyFile;

    public CustomFontButton(Context context) {
        super(context);
    }

    public CustomFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.custom_font);
        CharSequence s = a.getString(R.styleable.custom_font_fontFamilyFile);
        if (s != null) {
            this.setFontFamilyFile("fonts/"+s.toString()+".ttf");
            init();
        }
    }

    public CustomFontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.custom_font);
        CharSequence s = a.getString(R.styleable.custom_font_fontFamilyFile);
        if (s != null) {
            this.setFontFamilyFile("fonts/"+s.toString()+".ttf");
            init();
        }
    }

    private void init() {
/*      Typeface font = Typeface.createFromAsset(getContext().getAssets(), getFontFamilyFile());
        this.setTypeface(font);*/
    }

    public String getFontFamilyFile() {
        return fontFamilyFile;
    }

    public void setFontFamilyFile(String fontFamilyFile) {
        this.fontFamilyFile = fontFamilyFile;
    }
}
