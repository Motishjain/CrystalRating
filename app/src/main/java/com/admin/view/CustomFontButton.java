package com.admin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

import com.admin.freddyspeaks.R;

/**
 * Created by Admin on 3/21/2016.
 */
public class CustomFontButton extends Button {


    private String fontFamilyFile;
    Drawable originalBg;
    Drawable disabledBg;

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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(originalBg==null || disabledBg==null)
        {
            setColors();
        }
        if(enabled) {
            setBackgroundDrawable(originalBg);
            setTextColor((getResources().getColor(R.color.white)));
        }
        else {
            setBackgroundDrawable(disabledBg);
        }
    }

    /**
     *
     * Mutates and applies a filter that converts the given drawable to a Gray/corresponding shaded
     * image.
     * @return a mutated version of the given drawable with a color filter applied.
     */
    public Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null)
            return null;

        Drawable res = drawable.mutate();
        res.setColorFilter(Color.parseColor("#ad9390"), PorterDuff.Mode.SRC_IN);
        setTextColor(getResources().getColor(R.color.disabled_button_color));
        return res;
    }

    private void setColors() {
        originalBg = getBackground();
        disabledBg = convertDrawableToGrayScale(getBackground().getConstantState().newDrawable());
    }

    @Override
    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        setColors();
        setEnabled(isEnabled());
    }
}
