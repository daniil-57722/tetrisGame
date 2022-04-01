package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MyTextInputListener implements Input.TextInputListener {
    @Override
    public void input (String text) {
        Preferences preferences = Gdx.app.getPreferences("MyPrefs");
        String names = preferences.getString("name") + text +"\n";
        preferences.putString("name", names);
        preferences.flush();
    }

    @Override
    public void canceled () {
        Preferences preferences = Gdx.app.getPreferences("MyPrefs");
        String names = preferences.getString("name") + "noname\n";
        preferences.putString("name", names);
        preferences.flush();
    }
}
