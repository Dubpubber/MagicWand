/*
 * MIT License
 *
 * Copyright (c) 2017 Tyler Crowe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.loneboat.dnd.magicwand.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MagicWand extends Application {

    // Since the project uses Maven to manage resources, we need to use the class loader to get the resource.
    private URL mainWindow_URL = getClass().getClassLoader().getResource("MagicWand.fxml");

    private static boolean DEBUG = false;

    /**
     * Called when the window begins to open or a request is made to open the this particular window.
     * @param primaryStage - The stage in which the JavaFX thread will operate on.
     * @throws Exception - If the FXMLLoader fails or the MagicWand.fxml file is not found.
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        if(mainWindow_URL == null) throw new Exception("Unable to find \"MagicWand.fxml\" file. Please contact the developer ASAP.");
        Parent root = FXMLLoader.load(mainWindow_URL);
        primaryStage.setTitle("MagicWand - Loneboat Development");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    /**
     * What starts the program.
     * @param args - The arguments for the program.
     *             TODO: Possible console only mode??? [07-09-2017]
     */
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        launch(args);
    }

    public static Connection openConnection() {
        try {
            Connection conn = null;
            if(DEBUG)
                conn = DriverManager.getConnection("jdbc:sqlite:" + MagicWand.class.getClassLoader().getResource("DManagerLite.db"));
            else
                conn = DriverManager.getConnection("jdbc:sqlite:" + (new File("./resources/DManagerLite.db")).getPath());
            return conn;
        } catch (SQLException e) {
            System.out.println("Unable to open database connection:\n-----\n" + e.getMessage());
            return null;
        }
    }
}