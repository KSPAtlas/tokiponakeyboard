package uk.co.cocosquid.tokiponakeyboard;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import androidx.preference.PreferenceManager;


public class TokiPonaIME extends InputMethodService {

    private boolean emojiMode;
    private boolean teloMode;
    private MyKeyboard keyboard;
    private MyKeyboardEmoji keyboardEmoji;
    private MyKeyboardTelo keyboardTelo;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateInputView() {
        updatePreferences();

        keyboardTelo = getLayoutInflater().inflate(R.layout.keyboard_wrapper_telo, null).findViewById(R.id.keyboard_telo);
        keyboardEmoji = getLayoutInflater().inflate(R.layout.keyboard_wrapper_emoji, null).findViewById(R.id.keyboard_emoji);
        keyboard = getLayoutInflater().inflate(R.layout.keyboard_wrapper, null).findViewById(R.id.keyboard);

        if (teloMode) {
            return keyboardTelo;
        } else if (emojiMode) {
            return keyboardEmoji;
        }
        return keyboard;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInput(info, restarting);

        InputConnection ic = getCurrentInputConnection();
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);

        updatePreferences();

        keyboardTelo.setIMS(this);
        keyboardTelo.loadPreferences();
        keyboardTelo.setEditorInfo(info);
        keyboardTelo.setInputConnection(ic);
        keyboardTelo.setIMM(imm);
        keyboardTelo.updateCurrentState();

        keyboardEmoji.setIMS(this);
        keyboardEmoji.loadPreferences();
        keyboardEmoji.setEditorInfo(info);
        keyboardEmoji.setInputConnection(ic);
        keyboardEmoji.setIMM(imm);
        keyboardEmoji.updateCurrentState();

        keyboard.setIMS(this);
        keyboard.loadPreferences();
        keyboard.setEditorInfo(info);
        keyboard.setInputConnection(ic);
        keyboard.setIMM(imm);
        keyboard.updateCurrentState();
    }

    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
        if (teloMode) {
            keyboardTelo.updateCurrentState();
        } else if (emojiMode) {
            keyboardEmoji.updateCurrentState();
        } else {
            keyboard.updateCurrentState();
        }
    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();

        keyboardTelo.finishAction("finish");
        keyboardEmoji.finishAction("finish");
        keyboard.finishAction("finish");
    }

    public void setTeloMode(boolean newTeloMode) {
        if (newTeloMode) {
            teloMode = true;
            setInputView(keyboardTelo);
            keyboardTelo.updateCurrentState();
        } else {
            teloMode = false;
            setInputView(keyboard);
            keyboard.updateCurrentState();
        }
    }

    public void setEmojiMode(boolean newEmojiMode) {
        if (newEmojiMode) {
            emojiMode = true;
            setInputView(keyboardEmoji);
            keyboardEmoji.updateCurrentState();
        } else {
            emojiMode = false;
            setInputView(keyboard);
            keyboard.updateCurrentState();
        }
    }

    private void updatePreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //boolean previousEmojiMode = emojiMode;
        teloMode = sharedPreferences.getBoolean("telo_mode", false);
        emojiMode = sharedPreferences.getBoolean("emoji_mode", false);

        if (keyboard != null) {
            if (teloMode) {
                setInputView(keyboardTelo);
            } else if (emojiMode) {
                setInputView(keyboardEmoji);
            } else {
                setInputView(keyboard);
            }
        }
    }

}