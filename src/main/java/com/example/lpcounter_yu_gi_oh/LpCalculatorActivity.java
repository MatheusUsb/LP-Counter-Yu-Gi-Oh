package com.example.lpcounter_yu_gi_oh;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
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
            playerNameViews[i] = findViewById(getResources().getIdentifier("player" + (i+1) + "Name", "id", getPackageName()));
            playerNameViews[i].setText(playerNames[i]);

            lifePointViews[i] = findViewById(getResources().getIdentifier("player" + (i+1) + "LifePoints", "id", getPackageName()));
            updateLifePoints(i);

            addButtons[i] = findViewById(getResources().getIdentifier("player" + (i+1) + "AddButton", "id", getPackageName()));
            addButtons[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    openDialog(true, finalI);
                }
            });

            subtractButtons[i] = findViewById(getResources().getIdentifier("player" + (i+1) + "SubtractButton", "id", getPackageName()));
            subtractButtons[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    openDialog(false, finalI);
                }
            });
        }

        // Initialize the timer view
        timerView = findViewById(R.id.timerView);

        // Initialize the timer buttons
        startPauseButton = findViewById(R.id.startPauseButton);
        resetButton = findViewById(R.id.resetButton);

        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
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
            countDownTimer = null; // Add this line
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

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int value = Integer.parseInt(input.getText().toString());
                if (isAdding) {
                    addLifePoints(playerIndex, value);
                } else {
                    subtractLifePoints(playerIndex, value);
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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