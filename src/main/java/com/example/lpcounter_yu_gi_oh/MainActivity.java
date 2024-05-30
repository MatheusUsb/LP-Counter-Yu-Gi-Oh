package com.example.lpcounter_yu_gi_oh;

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
import android.content.DialogInterface;

import com.example.lpcounter_yu_gi_oh.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'lpcounter_yu_gi_oh' library on application startup.
    static {
        System.loadLibrary("lpcounter_yu_gi_oh");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the TextView for displaying the prompt
        TextView textView = findViewById(R.id.textView);
        textView.setText(R.string.select_player_number);

        // Set up the spinner for selecting the number of players
        Spinner playerSpinner = findViewById(R.id.player_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE);
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

        // Set up the button for confirming the selection
        Button confirmButton = findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get the selected number of players
                int numPlayers = Integer.parseInt(playerSpinner.getSelectedItem().toString());

                // Create an array to hold the player names
                String[] playerNames = new String[numPlayers];

                // Start the process of creating dialogs for each player
                createPlayerDialog(playerNames, 0, numPlayers);
            }
        });
    }

    private void createPlayerDialog(String[] playerNames, int playerIndex, int numPlayers) {
        // Create a dialog to input the player name
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter the name of player " + (playerIndex + 1));

        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playerNames[playerIndex] = input.getText().toString();

                // If this is the last player, start the new activity
                if (playerIndex == numPlayers - 1) {
                    // Display a message
                    Toast.makeText(MainActivity.this, "Selected " + numPlayers + " players", Toast.LENGTH_SHORT).show();

                    // Start the LP Calculator Activity and pass the number of players and the player names
                    Intent intent = new Intent(MainActivity.this, LpCalculatorActivity.class);
                    intent.putExtra("numPlayers", numPlayers);
                    intent.putExtra("playerNames", playerNames);
                    startActivity(intent);
                } else {
                    // Otherwise, create the next dialog
                    createPlayerDialog(playerNames, playerIndex + 1, numPlayers);
                }
            }
        });

        builder.show();
    }

    /**
     * A native method that is implemented by the 'lpcounter_yu_gi_oh' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}