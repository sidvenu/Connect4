package io.github.sidvenu.connect4;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;

public class MainActivity extends AppCompatActivity {

    GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameView = findViewById(R.id.game_view);
        findViewById(R.id.undo_move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.undoMove();
            }
        });
        findViewById(R.id.restart_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.restartGame();
            }
        });
        final MaterialCheckBox checkBox = findViewById(R.id.computer_checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gameView.restartGame();
                gameView.computerPlaying = isChecked;
            }
        });

        final NumberPicker rowsPicker = findViewById(R.id.row_picker),
                colsPicker = findViewById(R.id.col_picker);
        rowsPicker.setMinValue(4);
        rowsPicker.setMaxValue(10);
        rowsPicker.setValue(6);
        colsPicker.setMinValue(4);
        colsPicker.setMaxValue(10);
        colsPicker.setValue(7);
        NumberPicker.OnValueChangeListener changeDimensionListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int rows = rowsPicker.getValue(), cols = colsPicker.getValue();
                if(rows>8 || cols>8) {
                    checkBox.setChecked(false);
                    checkBox.setEnabled(false);
                } else {
                    checkBox.setEnabled(true);
                }
                gameView.setGridDimensions(rowsPicker.getValue(), colsPicker.getValue());
            }
        };
        rowsPicker.setOnValueChangedListener(changeDimensionListener);
        colsPicker.setOnValueChangedListener(changeDimensionListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameView.cleanupResources();
    }
}
