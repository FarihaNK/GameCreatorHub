package CreatorModel;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class DatabaseUpdater{
    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/GameCreatorApp", "root", "Thebookthief100%");
    public ArrayList<Node> nodeList = new ArrayList<>();
    public int game_id;
    public String gamename;
    public int num_of_rooms;
    public ArrayList<String> roomInfo;

    public DatabaseUpdater() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Games;");
        while (resultSet.next()) {
            game_id = Integer.parseInt(resultSet.getString("Game_id"));
        }
        resultSet.close();
        game_id++;
    }

    public void setGameName(TextField x){
        gamename = x.getText();
    }
    public void setNum_of_rooms(int x){
        num_of_rooms = x;
    }
    public void setRoomInfo(ArrayList<String> x){
        roomInfo = x;
    }
    public void updateNodeList(Node x){
        nodeList.add(x);
    }

    public void updateDatabase() throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO Games (Game_name, Number_of_rooms) VALUES ('"+gamename+"',"+num_of_rooms+");");
        ps.executeUpdate();

        PreparedStatement ps2 = connection.prepareStatement(
                "CREATE TABLE Game_"+ game_id +" (Room_id INT AUTO_INCREMENT PRIMARY KEY, Room_name VARCHAR(100), " +
                        "Room_description VARCHAR(300), Number_of_paths INT, Objects VARCHAR(300));"
        );
        ps2.executeUpdate();
        for (int i=0; i <= roomInfo.size(); i++) {
            if ((i + 1) % 4 == 0) {
                PreparedStatement ps3 = connection.prepareStatement(
                        "INSERT INTO Game_"+ game_id+" (room_name, room_description, Number_of_paths,Objects) VALUES" +
                                "('"+ roomInfo.get(i-3)+ "', '"+ roomInfo.get(i-2)+ "', "+ roomInfo.get(i-1)+",'" +roomInfo.get(i)+"');");
                ps3.executeUpdate();
            }
        }
        int r = 0;
        String di = null;
        int de = 0;
        String o = null;
        boolean b = false;
        for (Node n: nodeList) {
            if (b) {
                b = false;
                if (n instanceof TextField){
                    o = ((TextField) n).getText();
                    PreparedStatement ps5 = connection.prepareStatement(
                            "INSERT INTO Game_"+ game_id +"_Room_" +r+" (Path_direction, Path_destination, Blocked) VALUES" +
                                    "('"+ di + "', "+ de + ", '"+o+"');");
                    ps5.executeUpdate();
                }
            }
            else if (Objects.equals(n.getId(), "RoomNum")) {
                r++;
                PreparedStatement ps4 = connection.prepareStatement(
                        "CREATE TABLE Game_"+ game_id +"_Room_" +r+" (Path_id INT AUTO_INCREMENT PRIMARY KEY, Path_direction VARCHAR(100), " +
                                "Path_destination INT, Blocked VARCHAR(300));"
                );
                ps4.executeUpdate();
            }
            else if (Objects.equals(n.getId(), "Direction") && n instanceof TextField) {di = ((TextField) n).getText();}
            else if (Objects.equals(n.getId(), "Destination") && n instanceof TextField) {de = Integer.parseInt(((TextField) n).getText());}
            else if (Objects.equals(n.getId(), "blockbutton") && n instanceof Button) {
                if (Objects.equals(((Button) n).getText(), "Blocked")) {b = true;}
                else {o = null;
                    PreparedStatement ps5 = connection.prepareStatement(
                            "INSERT INTO Game_"+ game_id +"_Room_" +r+" (Path_direction, Path_destination, Blocked) VALUES" +
                                    "('"+ di + "', "+ de + ", '"+o+"');");
                    ps5.executeUpdate();
                }
            }
        }
    }
}