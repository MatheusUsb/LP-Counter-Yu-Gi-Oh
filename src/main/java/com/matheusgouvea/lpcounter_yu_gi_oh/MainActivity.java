package com.matheusgouvea.lpcounter_yu_gi_oh;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.Button;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.content.Intent;
import android.widget.TextView;
import android.graphics.Color;
import android.view.ViewGroup;
import android.text.InputType;
import android.widget.EditText;
import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.media.MediaPlayer;

import com.matheusgouvea.lpcounter_yu_gi_oh.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Tocar a música intro.mp3 da pasta res/raw
        mediaPlayer = MediaPlayer.create(this, R.raw.intro);
        mediaPlayer.start(); // Inicia a reprodução da música

        TextView textView = findViewById(R.id.textView);
        textView.setText(R.string.select_player_number);

        Spinner playerSpinner = findViewById(R.id.player_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE);
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(getResources().getStringArray(R.array.player_numbers));
        playerSpinner.setAdapter(adapter);

        Button confirmButton = findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int numPlayers = Integer.parseInt(playerSpinner.getSelectedItem().toString());
                String[] playerNames = new String[numPlayers];
                createPlayerDialog(playerNames, 0, numPlayers);
            }
        });
    }

    private void createPlayerDialog(String[] playerNames, int playerIndex, int numPlayers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter player name " + (playerIndex + 1));

        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> handleDialogActions(playerNames, playerIndex, numPlayers, input, dialog));

            input.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(input, InputMethodManager.SHOW_FORCED);
        });

        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleDialogActions(playerNames, playerIndex, numPlayers, input, dialog);
                return true;
            }
            return false;
        });

        dialog.show();
    }

    private void handleDialogActions(String[] playerNames, int playerIndex, int numPlayers, EditText input, AlertDialog dialog) {
        playerNames[playerIndex] = input.getText().toString();
        if (playerIndex == numPlayers - 1) {
            Toast.makeText(MainActivity.this, "Selected " + numPlayers + " players", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LpCalculatorActivity.class);
            intent.putExtra("numPlayers", numPlayers);
            intent.putExtra("playerNames", playerNames);
            startActivity(intent);
        } else {
            createPlayerDialog(playerNames, playerIndex + 1, numPlayers);
        }
        dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Libera os recursos do MediaPlayer
            mediaPlayer = null;
        }
    }

    public native String stringFromJNI();
}