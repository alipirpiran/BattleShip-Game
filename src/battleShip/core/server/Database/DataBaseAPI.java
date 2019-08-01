package battleShip.core.server.Database;

import battleShip.core.App;
import battleShip.models.Message;
import battleShip.models.Result;
import battleShip.models.Member;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;

public class DataBaseAPI {
    private static final String IMAGE_PATH = "src" + File.separator +
            "battleShip" + File.separator +
            "core" + File.separator +
            "server" + File.separator +
            "Database" + File.separator +
            "images";

    public static App app;
    static Connection connection;

    static {
        connection = ConnectionForDB.getConnection();
    }

    public static Result addUserToDataBase(Member member) {
        try {
            String username = member.getUsername();
            String password = member.getPassword();
            int wins = member.getWins();
            int looses = member.getLooses();
            String fullName = member.getFullName();

            PreparedStatement preparedStatement = connection.prepareStatement("insert into members values (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, wins);
            preparedStatement.setInt(4, looses);
            preparedStatement.setString(5, fullName);

            saveImage(member);

            preparedStatement.execute();
            return Result.SUCCESS;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Result.FAIL;
    }

    public static Member getMember(String findusername) {
        Member member = new Member();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select * from members where username='" + findusername + "'");

            while (resultSet.next()) {
                String username = resultSet.getString(1);
                String password = resultSet.getString(2);
                int wins = resultSet.getInt(3);
                int looses = resultSet.getInt(4);
                String fullname = resultSet.getString(5);

                member.setUsername(username);
                member.setPassword(password);
                member.setWins(wins);
                member.setLooses(looses);
                member.setFullName(fullname);
                setImage(member);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return member;
    }

    public static ArrayList<Member> getMembers() {
        ArrayList<Member> members = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select * from members");

            while (resultSet.next()) {
                String username = resultSet.getString(1);
                String password = resultSet.getString(2);
                int wins = resultSet.getInt(3);
                int looses = resultSet.getInt(4);
                String fullname = resultSet.getString(5);

                Member member = new Member();
                member.setUsername(username);
                member.setPassword(password);
                member.setWins(wins);
                member.setLooses(looses);
                member.setFullName(fullname);
                setImage(member);


                members.add(member);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return members;
    }

    private static void setImage(Member member) {
        File imageFile = null;
        for (File file : Paths.get(IMAGE_PATH).toFile().listFiles()) {
            if (file.getName().equals(member.getUsername())) {
                imageFile = file;
                break;
            }
        }

        if (imageFile == null)
            return;

        try {
            member.setImageData(Files.readAllBytes(imageFile.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void saveImage(Member member) {
        for (File file : Paths.get(IMAGE_PATH).toFile().listFiles()) {
            if (file.getName().equals(member.getUsername()))
                file.delete();
        }

        try (FileOutputStream fos = new FileOutputStream(new File(IMAGE_PATH + File.separator + member.getUsername()))) {
            fos.write(member.getImageData());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean refreshUserData(Member member) {
        System.out.println("db : datas refreshed");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update members set password=?, wins=?, loses=?, fullname=? where username=?");
            preparedStatement.setString(1, member.getPassword());
            preparedStatement.setInt(2, member.getWins());
            preparedStatement.setInt(3, member.getLooses());
            preparedStatement.setString(4, member.getFullName());
            preparedStatement.setString(5, member.getUsername());

            return preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }


}
