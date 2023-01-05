package com.zybooks.dotty;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DotsGame mGame;
    private DotsView mDotsView;
    private TextView mMovesRemaining;
    private TextView mScore;
    private SoundEffects mSoundEffects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovesRemaining = findViewById(R.id.moves_remaining_text_view);
        mScore = findViewById(R.id.score_text_view);
        mDotsView = findViewById(R.id.dots_view);
        mDotsView.setGridListener(mGridListener);

        findViewById(R.id.new_game_button).setOnClickListener(this::newGameClick);

        mGame = DotsGame.getInstance();
        startNewGame();
        mSoundEffects = SoundEffects.getInstance(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundEffects.release();
    }

    private final DotsView.DotsGridListener mGridListener = new DotsView.DotsGridListener() {

        @Override
        public void onDotSelected(Dot dot, DotsView.DotSelectionStatus selectionStatus) {

            // Ignore selections when game is over
            if (mGame.isGameOver()) return;

            // Add/remove dot to/from selected dots *
            if (selectionStatus == DotsView.DotSelectionStatus.First) {
                // Play first tone when first dot is selected
                mSoundEffects.resetTones();
            }

            // Select the dot and play the right tone
            DotsGame.DotStatus addStatus = mGame.processDot(dot);
            if (addStatus == DotsGame.DotStatus.Added) {
                mSoundEffects.playTone(true);
            }
            else if (addStatus == DotsGame.DotStatus.Removed) {
                mSoundEffects.playTone(false);
            }

            // If done selecting dots then replace selected dots and display new moves and score
            if (selectionStatus == DotsView.DotSelectionStatus.Last) {
                if (mGame.getSelectedDots().size() > 1) {
                    mDotsView.animateDots();

                    // These methods must be called AFTER the animation completes
//                    mGame.finishMove();
//                    updateMovesAndScore();
                }
                else {
                    mGame.clearSelectedDots();
                }
            }
            // Display changes to the game
            mDotsView.invalidate();

        }

        @Override
        public void onAnimationFinished() {
            mGame.finishMove();
            mDotsView.invalidate();
            updateMovesAndScore();

            if (mGame.isGameOver()) {
                mSoundEffects.playGameOver();
            }
        }
    };

    private void newGameClick(View view) {

        // Animate down off screen
        int screenHeight = this.getWindow().getDecorView().getHeight();
        ObjectAnimator moveBoardOff = ObjectAnimator.ofFloat(mDotsView,
                "translationY", screenHeight);
        moveBoardOff.setDuration(700);
        moveBoardOff.start();

        moveBoardOff.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                startNewGame();

                // Animate from above the screen down to default location
                ObjectAnimator moveBoardOn = ObjectAnimator.ofFloat(mDotsView,
                        "translationY", -screenHeight, 0);
                moveBoardOn.setDuration(700);
                moveBoardOn.start();
            }
        });
    }

    private void startNewGame() {
        mGame.newGame();
        mDotsView.invalidate();
        updateMovesAndScore();
    }

    private void updateMovesAndScore() {
        mMovesRemaining.setText(String.format(Locale.getDefault(), "%d", mGame.getMovesLeft()));
        mScore.setText(String.format(Locale.getDefault(), "%d", mGame.getScore()));
    }
}