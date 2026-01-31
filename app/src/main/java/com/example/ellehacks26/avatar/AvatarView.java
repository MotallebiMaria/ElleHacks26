package com.example.ellehacks26.avatar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class AvatarView extends View {
    private AvatarConfig config;
    private Paint skinPaint, hairPaint, eyePaint;
    private Drawable accessoryDrawable;
    private Drawable hairDrawable;

    // Measurements
    private int avatarSize;
    private int headRadius;
    private int bodyWidth, bodyHeight;
    private int eyeRadius;

    // Listener for avatar changes
    public interface AvatarChangeListener {
        void onAvatarChanged(AvatarConfig newConfig);
    }

    private AvatarChangeListener listener;

    public AvatarView(Context context) {
        super(context);
        init();
    }

    public AvatarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvatarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        config = new AvatarConfig();

        skinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hairPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        eyePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        updatePaints();
    }

    private void updatePaints() {
        skinPaint.setColor(Color.parseColor(config.getSkinColor()));
        hairPaint.setColor(Color.parseColor(config.getHairColor()));
        eyePaint.setColor(Color.parseColor(config.getEyeColor()));

        hairDrawable = getDrawableFromName(config.getHairStyle());
        accessoryDrawable = getDrawableFromName(config.getAccessory());
    }

    private Drawable getDrawableFromName(String drawableName) {
        int resId = getResources().getIdentifier(
                drawableName,
                "drawable",
                getContext().getPackageName()
        );
        return resId != 0 ? ContextCompat.getDrawable(getContext(), resId) : null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int size = Math.min(width, height);
        setMeasuredDimension(size, size);

        avatarSize = size;
        headRadius = size / 3;
        bodyWidth = size * 2 / 3;
        bodyHeight = size / 2;
        eyeRadius = size / 20;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int bodyTop = centerY + headRadius / 2;
        int bodyLeft = centerX - bodyWidth / 2;
        int bodyRight = centerX + bodyWidth / 2;
        int bodyBottom = bodyTop + bodyHeight;

        canvas.drawRoundRect(
                bodyLeft, bodyTop, bodyRight, bodyBottom,
                20, 20, skinPaint
        );

        canvas.drawCircle(centerX, centerY, headRadius, skinPaint);

        if (hairDrawable != null) {
            int hairLeft = centerX - headRadius;
            int hairTop = centerY - headRadius;
            int hairRight = centerX + headRadius;
            int hairBottom = centerY;

            hairDrawable.setBounds(hairLeft, hairTop, hairRight, hairBottom);
            hairDrawable.draw(canvas);
        } else {
            canvas.drawCircle(centerX, centerY - headRadius / 2, headRadius, hairPaint);
        }

        int eyeSpacing = headRadius / 2;
        canvas.drawCircle(centerX - eyeSpacing, centerY, eyeRadius, eyePaint);
        canvas.drawCircle(centerX + eyeSpacing, centerY, eyeRadius, eyePaint);

        if (accessoryDrawable != null) {
            int accSize = headRadius;
            int accLeft = centerX - accSize;
            int accTop = centerY - headRadius - accSize / 4;
            int accRight = centerX + accSize;
            int accBottom = centerY - headRadius + accSize / 4;

            accessoryDrawable.setBounds(accLeft, accTop, accRight, accBottom);
            accessoryDrawable.draw(canvas);
        }
    }

    public void setAvatarConfig(AvatarConfig config) {
        this.config = config;
        updatePaints();
        invalidate();

        if (listener != null) {
            listener.onAvatarChanged(config);
        }
    }

    public AvatarConfig getAvatarConfig() {
        return config;
    }

    public void setAvatarChangeListener(AvatarChangeListener listener) {
        this.listener = listener;
    }

    public void updateSkinColor(String colorHex) {
        config.setSkinColor(colorHex);
        updatePaints();
        invalidate();
    }

    public void updateHair(String style, String colorHex) {
        config.setHairStyle(style);
        config.setHairColor(colorHex);
        updatePaints();
        invalidate();
    }

    public void updateAccessory(String accessory) {
        config.setAccessory(accessory);
        updatePaints();
        invalidate();
    }
}
