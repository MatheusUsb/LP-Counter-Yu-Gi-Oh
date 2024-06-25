package com.matheusgouvea.lpcounter_yu_gi_oh;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import androidx.appcompat.app.AppCompatActivity;

public class LpCalculatorActivity extends AppCompatActivity {
    private int[] lifePoints;
    private TextView[] lifePointViews;
    private Button[] addButtons, subtractButtons;
    private Button startPauseButton, resetButton;
    private TextView timerView;
    private CountDownTimer countDownTimer;
    private boolean isPaused = true;
    private long timeLeft = 180000; // 3 minutes in milliseconds
    private String[] playerNames; // New array for player names
    private TextView[] playerNameViews; // New array for TextViews displaying player names

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lp_calculator);

        // Get the number of players and the player names from the Intent
        int numPlayers = getIntent().getIntExtra("numPlayers", 4);
        playerNames = getIntent().getStringArrayExtra("playerNames");

        // Initialize the arrays
        lifePoints = new int[numPlayers];
        lifePointViews = new TextView[numPlayers];
        playerNameViews = new TextView[numPlayers]; // Initialize the array for the player name views
        addButtons = new Button[numPlayers];
        subtractButtons = new Button[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            final int finalI = i;
            lifePoints[i] = 8000;

            // Get the TextView for the player name and set the player name
            playerNameViews[i] = findViewById(getResources().getIdentifier("player" + (i + 1) + "Name", "id", getPackageName()));
            playerNameViews[i].setText(playerNames[i]);

            lifePointViews[i] = findViewById(getResources().getIdentifier("player" + (i + 1) + "LifePoints", "id", getPackageName()));
            updateLifePoints(i);

            addButtons[i] = findViewById(getResources().getIdentifier("player" + (i + 1) + "AddButton", "id", getPackageName()));
            addButtons[i].setOnClickListener(v -> openDialog(true, finalI));

            subtractButtons[i] = findViewById(getResources().getIdentifier("player" + (i + 1) + "SubtractButton", "id", getPackageName()));
            subtractButtons[i].setOnClickListener(v -> openDialog(false, finalI));

            playerNameViews[i].setVisibility(View.VISIBLE);
            lifePointViews[i].setVisibility(View.VISIBLE);
            addButtons[i].setVisibility(View.VISIBLE);
            subtractButtons[i].setVisibility(View.VISIBLE);
        }

        // Adjust visibility based on the number of players
        for (int i = numPlayers; i < 4; i++) { // Assuming a maximum of 4 players
            findViewById(getResources().getIdentifier("player" + (i + 1) + "Name", "id", getPackageName())).setVisibility(View.GONE);
            findViewById(getResources().getIdentifier("player" + (i + 1) + "LifePoints", "id", getPackageName())).setVisibility(View.GONE);
            findViewById(getResources().getIdentifier("player" + (i + 1) + "AddButton", "id", getPackageName())).setVisibility(View.GONE);
            findViewById(getResources().getIdentifier("player" + (i + 1) + "SubtractButton", "id", getPackageName())).setVisibility(View.GONE);
        }

        // Initialize the timer view and buttons
        timerView = findViewById(R.id.timerView);
        startPauseButton = findViewById(R.id.startPauseButton);
        resetButton = findViewById(R.id.resetButton);

        startPauseButton.setOnClickListener(v -> {
            if (isPaused) {
                if (countDownTimer == null) {
                    startTimer();
                    startPauseButton.setText("Pause");
                } else {
                    resumeTimer();
                    startPauseButton.setText("Pause");
                }
            } else {
                pauseTimer();
                startPauseButton.setText("Resume");
            }
        });

        resetButton.setOnClickListener(v -> resetTimer());
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
            }

            public void onFinish() {
                timerView.setText("00:00");
            }
        }.start();

        isPaused = false;
        resetButton.setEnabled(true);
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        isPaused = true;
    }

    private void resumeTimer() {
        startTimer();
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeLeft = 180000;
        updateTimer();
        isPaused = true;
        startPauseButton.setText("Start");
        resetButton.setEnabled(false);
    }

    private void updateTimer() {
        int minutes = (int) timeLeft / 60000;
        int seconds = (int) timeLeft % 60000 / 1000;

        String timeLeftText = String.format("%02d:%02d", minutes, seconds);
        timerView.setText(timeLeftText);
    }

    private void openDialog(final boolean isAdding, final int playerIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isAdding ? "Enter the gain" : "Enter the damage");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> processInputValue(input.getText().toString(), isAdding, playerIndex));
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();

        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                return true;
            }
            return false;
        });

        dialog.show();
    }

    private void processInputValue(String inputValue, boolean isAdding, int playerIndex) {
        if (!inputValue.isEmpty()) {
            int value = Integer.parseInt(inputValue);
            if (isAdding) {
                addLifePoints(playerIndex, value);
            } else {
                subtractLifePoints(playerIndex, value);
            }
        } else {
            Toast.makeText(LpCalculatorActivity.this, "Please, insert a value", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLifePoints(int playerIndex) {
        lifePointViews[playerIndex].setText(String.valueOf(lifePoints[playerIndex]));
    }

    private void addLifePoints(int playerIndex, int points) {
        lifePoints[playerIndex] += points;
        updateLifePoints(playerIndex);
    }

    private void subtractLifePoints(int playerIndex, int points) {
        lifePoints[playerIndex] -= points;
        updateLifePoints(playerIndex);
    }
}