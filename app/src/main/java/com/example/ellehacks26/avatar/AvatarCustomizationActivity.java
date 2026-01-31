package com.example.ellehacks26.avatar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.ellehacks26.BaseActivity;
import com.example.ellehacks26.R;
import java.util.List;

public class AvatarCustomizationActivity extends BaseActivity {
    private AvatarView avatarView;
    private AvatarViewModel viewModel;
    private AvatarRepository avatarRepository;
    private AvatarOptionsRepository optionsRepository;

    private HairStyleAdapter hairStyleAdapter;
    private ColorPaletteAdapter skinColorAdapter;
    private ColorPaletteAdapter hairColorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_customization);

        avatarView = findViewById(R.id.avatarView);

        avatarRepository = new AvatarRepository(this);
        optionsRepository = AvatarOptionsRepository.getInstance(this);

        viewModel = new ViewModelProvider(this).get(AvatarViewModel.class);
        viewModel.init(avatarRepository);

        viewModel.getAvatarConfig().observe(this, config -> {
            if (config != null) {
                avatarView.setAvatarConfig(config);
                updateUIWithCurrentConfig();
            }
        });

        setupUI();
        setupSaveButton();
    }

    private void setupUI() {
        setupSkinColorSection();
        setupHairStyleSection();
        setupHairColorSection();
        setupAccessorySection();
    }

    private void setupSkinColorSection() {
        TextView skinTitle = findViewById(R.id.skinColorTitle);
        GridView skinGrid = findViewById(R.id.skinColorGrid);

        List<ColorOption> skinColors = optionsRepository.getSkinColorOptions();
        skinColorAdapter = new ColorPaletteAdapter(this, skinColors);
        skinColorAdapter.setSelectionListener(colorOption -> {
            viewModel.updateSkinColor(colorOption.getHexColor());
        });
        skinGrid.setAdapter(skinColorAdapter);
    }

    private void setupHairStyleSection() {
        TextView hairStyleTitle = findViewById(R.id.hairStyleTitle);
        GridView hairStyleGrid = findViewById(R.id.hairStyleGrid);

        List<HairStyle> hairStyles = optionsRepository.getHairStyleOptions();
        hairStyleAdapter = new HairStyleAdapter(this, hairStyles);
        hairStyleAdapter.setSelectionListener(hairStyle -> {
            viewModel.updateHairStyle(hairStyle.getAvatarDrawableName());
        });
        hairStyleGrid.setAdapter(hairStyleAdapter);
    }

    private void setupHairColorSection() {
        TextView hairColorTitle = findViewById(R.id.hairColorTitle);
        GridView hairColorGrid = findViewById(R.id.hairColorGrid);

        List<ColorOption> hairColors = optionsRepository.getHairColorOptions();
        hairColorAdapter = new ColorPaletteAdapter(this, hairColors);
        hairColorAdapter.setSelectionListener(colorOption -> {
            viewModel.updateHairColor(colorOption.getHexColor());
        });
        hairColorGrid.setAdapter(hairColorAdapter);
    }

    private void setupAccessorySection() {
    }

    private void updateUIWithCurrentConfig() {
        AvatarConfig config = viewModel.getAvatarConfig().getValue();
        if (config != null) {
            List<ColorOption> skinColors = optionsRepository.getSkinColorOptions();
            for (int i = 0; i < skinColors.size(); i++) {
                if (skinColors.get(i).getHexColor().equals(config.getSkinColor())) {
                    skinColorAdapter.setSelectedPosition(i);
                    break;
                }
            }

            List<HairStyle> hairStyles = optionsRepository.getHairStyleOptions();
            for (int i = 0; i < hairStyles.size(); i++) {
                if (hairStyles.get(i).getAvatarDrawableName().equals(config.getHairStyle())) {
                    hairStyleAdapter.setSelectedPosition(i);
                    break;
                }
            }

            List<ColorOption> hairColors = optionsRepository.getHairColorOptions();
            for (int i = 0; i < hairColors.size(); i++) {
                if (hairColors.get(i).getHexColor().equals(config.getHairColor())) {
                    hairColorAdapter.setSelectedPosition(i);
                    break;
                }
            }
        }
    }

    private void setupSaveButton() {
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> finish());
    }
}