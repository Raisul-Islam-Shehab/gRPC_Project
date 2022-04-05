package com.userService.gRPC.UserService;

import com.gRPC.swe.User;
import com.gRPC.swe.userGrpc;
import io.grpc.stub.StreamObserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class UserService extends userGrpc.userImplBase {
    //MySQL info
    String url = "jdbc:mysql://localhost:3306/student_registration";
    String user = "root";
    String pass = "";

    @Override
    public void logIn(User.LogInRequest request, StreamObserver<User.APIResponse> responseObserver) throws SQLException{
        String userName = request.getUserName();
        String password = request.getPassword();

        //Checking database
        ResultSet resultSet = checkLoginInfo(userName, password);

        //Creating response
        User.APIResponse.Builder response = User.APIResponse.newBuilder();
        if (resultSet.next()) {
            if (resultSet.getInt(1) == 1) {
                response.setResponseCode(200).setResponseMessage("Log in Successful");
            } else {
                response.setResponseCode(404).setResponseMessage("Wrong username or password");
            }
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();  // close the connection between API and client
    }

    @Override
    public void register(User.RegisterRequest request, StreamObserver<User.APIResponse> responseObserver) throws SQLException {
        long regID = request.getRegistrationID();
        String name = request.getName();
        ResultSet resultSet = checkRegInfo(regID);

        //Creating response
        User.APIResponse.Builder regResponse = User.APIResponse.newBuilder();

        if (resultSet.next()) {
            if (resultSet.getInt(1) == 1) {
                regResponse.setResponseMessage("Registration ID '" + regID + "' is already registered").setResponseCode(400);
            } else {
                Connection connection = getConnection(url, user, pass);
                //Adding new student
                PreparedStatement statement = connection.prepareStatement
                        ("INSERT INTO registration_list VALUES('" + regID + "', '" + name + "')");
                statement.executeUpdate();
                regResponse.setResponseMessage("'" + name + "' with registration ID '" + regID + "' is now registered successfully").setResponseCode(201);
            }
        }
        responseObserver.onNext(regResponse.build());
        responseObserver.onCompleted();     // close the connection between API and client
    }

    private ResultSet checkLoginInfo(String userName, String password) throws SQLException {
        //Connecting to MySQL database
        Connection connection = getConnection(url, user, pass);
        PreparedStatement statement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM login_info" +
                " WHERE username = ? && pass = ?)");
        statement.setString(1, userName);
        statement.setString(2, password);
        return statement.executeQuery();
    }

    private ResultSet checkRegInfo(long regID) throws SQLException {
        //Connecting to MySQL database
        Connection connection = getConnection(url, user, pass);
        PreparedStatement statement = connection.prepareStatement
                ("SELECT EXISTS(SELECT * FROM registration_list WHERE Reg_ID = ?)");
        statement.setInt(1, (int) regID);
        return statement.executeQuery();
    }
}
