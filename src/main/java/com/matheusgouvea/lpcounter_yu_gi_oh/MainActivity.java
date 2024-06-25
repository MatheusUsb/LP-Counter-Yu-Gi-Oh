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
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import com.matheusgouvea.lpcounter_yu_gi_oh.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        builder.setTitle("Enter the name of player " + (playerIndex + 1));

        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", null); // Set null here to override later

        final AlertDialog dialog = builder.create();

        // Listener para detectar o clique no botão "Confirmar" do teclado virtual
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Simula o clique no botão "OK" do diálogo
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
                    return true; // Consumir o evento aqui
                }
                return false; // Passar o evento para o próximo listener
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Ação do botão "OK" já definida acima
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
                });
            }
        });

        dialog.show();
    }

    public native String stringFromJNI();
}