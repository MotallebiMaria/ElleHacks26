package com.example.ellehacks26.avatar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.ellehacks26.R;
import java.util.List;

public class ColorPaletteAdapter extends ArrayAdapter<ColorOption> {
    private final LayoutInflater inflater;
    private int selectedPosition = -1;

    public interface ColorSelectionListener {
        void onColorSelected(ColorOption colorOption);
    }

    private ColorSelectionListener listener;

    public ColorPaletteAdapter(@NonNull Context context, @NonNull List<ColorOption> colors) {
        super(context, 0, colors);
        this.inflater = LayoutInflater.from(context);
    }

    public void setSelectionListener(ColorSelectionListener listener) {
        this.listener = listener;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_color, parent, false);
            holder = new ViewHolder();
            holder.colorView = convertView.findViewById(R.id.colorView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ColorOption colorOption = getItem(position);
        if (colorOption != null) {
            // Create circular color swatch
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(Color.parseColor(colorOption.getHexColor()));
            drawable.setStroke(4, Color.BLACK);
            holder.colorView.setBackground(drawable);

            // Visual feedback for selection
            if (position == selectedPosition) {
                holder.colorView.setScaleX(1.2f);
                holder.colorView.setScaleY(1.2f);
            } else {
                holder.colorView.setScaleX(1.0f);
                holder.colorView.setScaleY(1.0f);
            }

            convertView.setOnClickListener(v -> {
                setSelectedPosition(position);
                if (listener != null) {
                    listener.onColorSelected(colorOption);
                }
            });
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView colorView;
    }
}