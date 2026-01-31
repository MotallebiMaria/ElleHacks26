package com.example.ellehacks26.avatar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.ellehacks26.R;
import java.util.List;

public class HairStyleAdapter extends ArrayAdapter<HairStyle> {
    private final LayoutInflater inflater;
    private int selectedPosition = -1;

    public interface HairStyleSelectionListener {
        void onHairStyleSelected(HairStyle hairStyle);
    }

    private HairStyleSelectionListener listener;

    public HairStyleAdapter(@NonNull Context context, @NonNull List<HairStyle> hairStyles) {
        super(context, 0, hairStyles);
        this.inflater = LayoutInflater.from(context);
    }

    public void setSelectionListener(HairStyleSelectionListener listener) {
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
            convertView = inflater.inflate(R.layout.item_hair_style, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.hairStyleImage);
            holder.textView = convertView.findViewById(R.id.hairStyleName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HairStyle hairStyle = getItem(position);
        if (hairStyle != null) {
            holder.imageView.setImageResource(hairStyle.getPreviewDrawableId());
            holder.textView.setText(hairStyle.getDisplayName());

            if (position == selectedPosition) {
                convertView.setBackgroundResource(R.drawable.item_selected_background);
            } else {
                convertView.setBackgroundResource(R.drawable.item_normal_background);
            }

            convertView.setOnClickListener(v -> {
                setSelectedPosition(position);
                if (listener != null) {
                    listener.onHairStyleSelected(hairStyle);
                }
            });
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}