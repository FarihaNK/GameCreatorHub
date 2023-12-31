package Visualizer;

import GameModel.AccessibleGame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;


/**
 * Class LoadVisualizer.
 *
 * Loads Serialized games.
 */
public class LoadVisualizer {

    private GameVisualizer gameVisualizer;
    private Label selectGameLabel;
    private Button selectGameButton;
    private Button closeWindowButton;

    private ListView<String> GameList;
    private String filename = null;

    public LoadVisualizer(GameVisualizer gameVisualizer){

        //note that the buttons in this visualizer are not accessible!!
        this.gameVisualizer = gameVisualizer;
        selectGameLabel = new Label(String.format(""));

        GameList = new ListView<>(); //to hold all the file names

        final Stage dialog = new Stage(); //dialogue box
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(gameVisualizer.stage);

        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(20, 20, 20, 20));
        dialogVbox.setStyle("-fx-background-color: #121212;");
        selectGameLabel.setId("CurrentGame"); // DO NOT MODIFY ID
        GameList.setId("GameList");  // DO NOT MODIFY ID
        GameList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        getFiles(GameList); //get files for file selector
        selectGameButton = new Button("Change Game");
        selectGameButton.setId("ChangeGame"); // DO NOT MODIFY ID
        GameVisualizer.makeButtonAccessible(selectGameButton, "select game", "This is the button to select a game", "Use this button to indicate a game file you would like to load.");

        closeWindowButton = new Button("Close Window");
        closeWindowButton.setId("closeWindowButton"); // DO NOT MODIFY ID
        closeWindowButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        closeWindowButton.setPrefSize(200, 50);
        closeWindowButton.setFont(new Font(16));
        closeWindowButton.setOnAction(e -> dialog.close());
        GameVisualizer.makeButtonAccessible(closeWindowButton, "close window", "This is a button to close the load game window", "Use this button to close the load game window.");

        //on selection, do something
        selectGameButton.setOnAction(e -> {
            try {
                selectGame(selectGameLabel, GameList);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        VBox selectGameBox = new VBox(10, selectGameLabel, GameList, selectGameButton);

        // Default styles which can be modified
        GameList.setPrefHeight(100);
        selectGameLabel.setStyle("-fx-text-fill: #e8e6e3");
        selectGameLabel.setFont(new Font(16));
        selectGameButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        selectGameButton.setPrefSize(200, 50);
        selectGameButton.setFont(new Font(16));
        selectGameBox.setAlignment(Pos.CENTER);
        dialogVbox.getChildren().add(selectGameBox);
        Scene dialogScene = new Scene(dialogVbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Get Files to display in the on screen ListView
     * Populate the listView attribute with .ser file names
     * Files will be located in the Games/Saved directory
     *
     * @param listView the ListView containing all the .ser files in the Games/Saved directory.
     */
    private void getFiles(ListView<String> listView) {
        File dir = new File("Games/Saved");
        if (!dir.isDirectory() || !dir.exists()) {
            dir.mkdirs();
        }
        File[] files = dir.listFiles();
        assert files != null;
        for (File f: files) {
            if (f.getName().endsWith(".ser")) {
                listView.getItems().add(f.getName());
            }
        }
    }

    /**
     * Select the Game
     * Try to load a game from the Games/Saved
     * If successful, stop any articulation and put the name of the loaded file in the selectGameLabel.
     * If unsuccessful, stop any articulation and start an entirely new game from scratch.
     * In this case, change the selectGameLabel to indicate a new game has been loaded.
     *
     * @param selectGameLabel the label to use to print errors and or successes to the user.
     * @param GameList the ListView to populate
     */
    private void selectGame(Label selectGameLabel, ListView<String> GameList) throws IOException {
        //saved games will be in the Games/Saved folder!
        String selected = GameList.getSelectionModel().getSelectedItem();
        gameVisualizer.stopArticulation();
        if (selected != null) {
            try{
                String gamepath = "Games/Saved/" + selected;
                AccessibleGame g = loadGame(gamepath);
                this.gameVisualizer = new GameVisualizer(g, gameVisualizer.stage);
                selectGameLabel.setText(selected);
            } catch (ClassNotFoundException e) {throw new IOException();}
        }
        else {
            selectGameLabel.setText("New Game Created");
            try{
                AccessibleGame g = loadGame("Games/TinyGame");
                this.gameVisualizer = new GameVisualizer(g, gameVisualizer.stage);
                selectGameLabel.setText("New Game Created");
            } catch (ClassNotFoundException e) {throw new IOException();}
        }
    }

    /**
     * Load the Game from a file
     *
     * @param GameFile file to load
     * @return loaded Tetris Model
     */
    public AccessibleGame loadGame(String GameFile) throws IOException, ClassNotFoundException {
        // Reading the object from a file
        FileInputStream file = null;
        ObjectInputStream in = null;
        try {
            file = new FileInputStream(GameFile);
            in = new ObjectInputStream(file);
            return (AccessibleGame) in.readObject();
        } finally {
            if (in != null) {
                in.close();
                file.close();
            }
        }
    }

}
