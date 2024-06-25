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
import android.media.MediaPlayer;
import android.view.WindowManager;

public class LpCalculatorActivity extends AppCompatActivity {
    private int[] lifePoints;
    private TextView[] lifePointViews;
    private Button[] addButtons, subtractButtons;
    private Button startPauseButton, resetButton, resetGameButton;
    private TextView timerView;
    private CountDownTimer countDownTimer;
    private boolean isPaused = true;
    private long timeLeft = 180000; // 3 minutes in milliseconds
    private String[] playerNames;
    private TextView[] playerNameViews;
    private boolean soundPlayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lp_calculator);

        int numPlayers = getIntent().getIntExtra("numPlayers", 4);
        playerNames = getIntent().getStringArrayExtra("playerNames");

        lifePoints = new int[numPlayers];
        lifePointViews = new TextView[numPlayers];
        playerNameViews = new TextView[numPlayers];
        addButtons = new Button[numPlayers];
        subtractButtons = new Button[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            final int finalI = i;
            lifePoints[i] = 8000;

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

        for (int i = numPlayers; i < 4; i++) {
            findViewById(getResources().getIdentifier("player" + (i + 1) + "Name", "id", getPackageName())).setVisibility(View.GONE);
            findViewById(getResources().getIdentifier("player" + (i + 1) + "LifePoints", "id", getPackageName())).setVisibility(View.GONE);
            findViewById(getResources().getIdentifier("player" + (i + 1) + "AddButton", "id", getPackageName())).setVisibility(View.GONE);
            findViewById(getResources().getIdentifier("player" + (i + 1) + "SubtractButton", "id", getPackageName())).setVisibility(View.GONE);
        }

        timerView = findViewById(R.id.timerView);
        startPauseButton = findViewById(R.id.startPauseButton);
        resetButton = findViewById(R.id.resetButton);
        resetGameButton = findViewById(R.id.resetGameButton);

        startPauseButton.setOnClickListener(v -> {
            if (isPaused) {
                if (countDownTimer == null) {
                    startTimer();
                    startPauseButton.setText("Pause");
                } else {
                    resumeTimer();
                    startPauseButton.setText("Pause");
                }
                resetButton.setEnabled(true); // Habilita o botão de reset quando o temporizador é iniciado
            } else {
                pauseTimer();
                startPauseButton.setText("Resume");
            }
        });

        resetButton.setOnClickListener(v -> resetTimer());
        resetGameButton.setOnClickListener(v -> resetGame());
    }

    private void updateLifePoints(int playerIndex) {
        lifePointViews[playerIndex].setText(String.valueOf(lifePoints[playerIndex]));
    }

    private void openDialog(boolean isAdding, int playerIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isAdding ? "Add Life Points" : "Subtract Life Points");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            try {
                int value = Integer.parseInt(input.getText().toString());
                if (isAdding) {
                    lifePoints[playerIndex] += value;
                } else {
                    lifePoints[playerIndex] -= value;
                }
                updateLifePoints(playerIndex);
            } catch (NumberFormatException e) {
                Toast.makeText(LpCalculatorActivity.this, "Invalid number", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();

        // Handling the keyboard "OK" action
        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    int value = Integer.parseInt(input.getText().toString());
                    if (isAdding) {
                        lifePoints[playerIndex] += value;
                    } else {
                        lifePoints[playerIndex] -= value;
                    }
                    updateLifePoints(playerIndex);
                    dialog.dismiss(); // Dismiss the dialog after the action
                } catch (NumberFormatException e) {
                    Toast.makeText(LpCalculatorActivity.this, "Invalid number", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); // Ensure the keyboard is shown
        input.requestFocus(); // Request focus for the input

        dialog.show();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();

                if (timeLeft <= 10900 && !soundPlayed) { // 10 segundos ou menos e a música ainda não foi reproduzida
                    MediaPlayer mediaPlayer = MediaPlayer.create(LpCalculatorActivity.this, R.raw.time);
                    mediaPlayer.start();
                    soundPlayed = true; // Garante que a música não seja reproduzida mais de uma vez
                }
            }

            public void onFinish() {
                timerView.setText("00:00");
                if (!soundPlayed) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(LpCalculatorActivity.this, R.raw.time);
                    mediaPlayer.start();
                    soundPlayed = true;
                }
            }
        }.start();

        isPaused = false;
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        isPaused = true;
    }

    private void resumeTimer() {
        startTimer();
        isPaused = false;
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
        soundPlayed = false;
    }

    private void updateTimer() {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerView.setText(timeFormatted);

        if (timeLeft <= 10000) { // 10 segundos ou menos
            timerView.setTextColor(getResources().getColor(android.R.color.holo_red_dark)); // Define a cor do texto para vermelho
        } else {
            timerView.setTextColor(getResources().getColor(android.R.color.white)); // Volta para a cor padrão se maior que 10 segundos
        }
    }

    private void resetGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Game");
        builder.setMessage("Are you sure you want to reset the game?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Lógica de reset do jogo
                for (int i = 0; i < lifePoints.length; i++) {
                    lifePoints[i] = 8000;
                    updateLifePoints(i);
                }
                resetTimer();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}