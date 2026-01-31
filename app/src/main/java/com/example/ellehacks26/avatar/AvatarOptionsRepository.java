package com.example.ellehacks26.avatar;

import android.content.Context;

import com.example.ellehacks26.R;

import java.util.ArrayList;
import java.util.List;

public class AvatarOptionsRepository {
    private static AvatarOptionsRepository instance;
    private final Context context;

    private AvatarOptionsRepository(Context context) {
        this.context = context;
    }

    public static synchronized AvatarOptionsRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AvatarOptionsRepository(context);
        }
        return instance;
    }

    public List<ColorOption> getSkinColorOptions() {
        List<ColorOption> colors = new ArrayList<>();
        colors.add(new ColorOption("skin_tan", "#FFD2B48C", "Tan"));
        colors.add(new ColorOption("skin_light", "#FFF5E6D3", "Light"));
        colors.add(new ColorOption("skin_medium", "#FFC68642", "Medium"));
        colors.add(new ColorOption("skin_dark", "#FF8D5524", "Dark"));
        return colors;
    }

    public List<HairStyle> getHairStyleOptions() {
        List<HairStyle> hairStyles = new ArrayList<>();
        // TODO: make R.drawable references
//        hairStyles.add(new HairStyle("hair_short", "Short Hair",
//                R.drawable.hair_short_preview, "hair_short"));
//        hairStyles.add(new HairStyle("hair_long", "Long Hair",
//                R.drawable.hair_long_preview, "hair_long"));
//        hairStyles.add(new HairStyle("hair_curly", "Curly Hair",
//                R.drawable.hair_curly_preview, "hair_curly"));
//        hairStyles.add(new HairStyle("hair_spiky", "Spiky Hair",
//                R.drawable.hair_spiky_preview, "hair_spiky"));
        return hairStyles;
    }

    public List<ColorOption> getHairColorOptions() {
        List<ColorOption> colors = new ArrayList<>();
        colors.add(new ColorOption("hair_black", "#FF000000", "Black"));
        colors.add(new ColorOption("hair_brown", "#FF8B4513", "Brown"));
        colors.add(new ColorOption("hair_blonde", "#FFFFD700", "Blonde"));
        colors.add(new ColorOption("hair_red", "#FFFF4500", "Red"));
        return colors;
    }
}