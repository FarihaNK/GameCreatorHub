package CreatorModel;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class CreatedGamePlayer{
    Stage stage;
    int game_id;
    int number_of_rooms;
    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/GameCreatorApp", "user", "password");
    String gamename;
    public CreatedGamePlayer(Stage stage, String gamename) throws SQLException {

        PreparedStatement ps = connection.prepareStatement("SELECT Game_id FROM Games WHERE Game_name = '"+gamename+"';");
        ResultSet resultSet = ps.executeQuery();
        resultSet.next();
        int x = resultSet.getInt("Game_id");

        this.gamename = gamename;
        this.game_id = x;
        this.stage = stage;
        runUI();
    }
    public void runUI(){
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(30);
        root.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(root);

        Text text = new Text("Do you want to share your game?");
        text.setFont(Font.font("Arial", 25));
        text.setFill(Color.WHITE);
        root.getChildren().add(text);

        HBox yesnoButtons = new HBox();
        yesnoButtons.setAlignment(Pos.CENTER);
        yesnoButtons.setSpacing(20);
        root.getChildren().add(yesnoButtons);

        Button yesButton = new Button("Yes");
        yesButton.setPrefWidth(80);
        yesButton.setPrefHeight(50);
        yesButton.setTextFill(Color.WHITE);
        yesButton.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-font-size: 15px;");
        yesnoButtons.getChildren().add(yesButton);

        Button noButton = new Button("No");
        noButton.setPrefWidth(80);
        noButton.setPrefHeight(50);
        noButton.setTextFill(Color.WHITE);
        noButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 15px;");
        yesnoButtons.getChildren().add(noButton);

        Text text2 = new Text("Click to play");
        text2.setFont(Font.font("Arial", 25));
        text2.setFill(Color.WHITE);
        root.getChildren().add(text2);

        Button playButton = new Button("Play Game");
        playButton.setPrefWidth(300);
        playButton.setPrefHeight(70);
        playButton.setTextFill(Color.WHITE);
        playButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 25px;");
        root.getChildren().add(playButton);
        playgameHandler(playButton);

        stage.setScene(scene);
        stage.setTitle("PlayGame");
        stage.setWidth(600);
        stage.setHeight(600);
        stage.show();
    }

    public void playgameHandler(Button playButton){
        playButton.setOnMousePressed(event -> playButton.setStyle("-fx-background-color: orange; -fx-text-fill: white;-fx-font-size: 25px;"));
        playButton.setOnMouseReleased(event -> playButton.setStyle("-fx-background-color: green; -fx-text-fill: white;-fx-font-size: 25px;"));
        playButton.setOnAction(event -> {
            try {
                writeGame(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public ArrayList<ArrayList<?>> getObjectList(){
        ArrayList<ArrayList<?>> output = new ArrayList<>();
        //TODO: update object list with all objects in room
        return output;
    }
     public void writeGame(int game_id) throws SQLException {
         PreparedStatement ps1 = connection.prepareStatement("SELECT Number_of_rooms FROM Games WHERE Game_id = "+game_id+";");
         ResultSet resultSet1 = ps1.executeQuery();
         resultSet1.next();

         int x = resultSet1.getInt("Number_of_rooms");
        this.number_of_rooms = x;

         File gamenameFile = new File("Games/"+gamename+"/");
         gamenameFile.mkdirs();

         String roomsPath = "Games/"+gamename+"/rooms.txt";
         try {
             FileWriter fileWriter = new FileWriter(roomsPath);
             int r = 1;
             while (r <= number_of_rooms) {
                 fileWriter.write(r+ "\n");

                 PreparedStatement ps2 = connection.prepareStatement("SELECT Room_name FROM Game_"+game_id+" WHERE Room_id = "+r+";");
                 ResultSet resultSet2 = ps2.executeQuery();
                 resultSet2.next();
                 String a = resultSet2.getString("Room_name");
                 System.out.println(a);

                 fileWriter.write(a+"\n");

                 PreparedStatement ps3 = connection.prepareStatement("SELECT Room_description FROM Game_"+game_id+" WHERE Room_id = "+r+";");
                 ResultSet resultSet3 = ps3.executeQuery();
                 resultSet3.next();
                 String b = resultSet3.getString("Room_description");
                 System.out.println(b);

                 fileWriter.write(b+"\n");
                 fileWriter.write("-----\n");

                 PreparedStatement ps4 = connection.prepareStatement("SELECT Number_of_paths FROM Game_"+game_id+" WHERE Room_id = "+r+";");
                 ResultSet resultSet4 = ps4.executeQuery();
                 resultSet4.next();
                 int c = resultSet4.getInt("Number_of_paths");
                 System.out.println(c);
                 int p = 1;

                 while (p <= c){
                     PreparedStatement ps5 = connection.prepareStatement("SELECT Path_direction, Path_destination, Blocked FROM Game_"+game_id+"_Room_"+r+" WHERE Path_id = "+p+";");
                     ResultSet resultSet5 = ps5.executeQuery();
                     resultSet5.next();
                     String d = resultSet5.getString("Path_direction");
                     int e = resultSet5.getInt("Path_destination");
                     String f = resultSet5.getString("Blocked");
                     if (!Objects.equals(f, "null")){fileWriter.write(d+"     "+e+"/"+f+"\n");}
                     else {fileWriter.write(d+"     "+e+"\n");}
                     p++;
                 }

                 fileWriter.write("\n");
                 r++;
             } fileWriter.close();
         } catch (Exception e) {
             //TODO: handle exception
         }

         String objPath = "Games/"+gamename+"/objects.txt";
         try {
             int r = 1;
             FileWriter fileWriter = new FileWriter(objPath);
             while (r <= 4) {
                 fileWriter.write("objname\n");
                 fileWriter.write("objdescription\n");
                 fileWriter.write("roomnum\n\n");
                 r++;
             } fileWriter.close();
         } catch (Exception e) {
             //TODO: handle exception
         }
     }
}