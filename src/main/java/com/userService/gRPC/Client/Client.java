package com.userService.gRPC.Client;

import com.gRPC.swe.User;
import com.gRPC.swe.userGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 3000).usePlaintext().build();
        // generating stub from proto file
       userGrpc.userBlockingStub stub = userGrpc.newBlockingStub(channel);

       boolean input = false;
       while (!input){
           System.out.println("Enter your name(can't be empty) :\r");
           String name = scanner.nextLine();
           System.out.println("Enter your password(can't be empty) :\r");
           String pass = scanner.nextLine();
           User.LogInRequest request = User.LogInRequest.newBuilder().setUserName(name).setPassword(pass).build();
           User.APIResponse response = stub.logIn(request);
           if(response.getResponseCode() == 200){
               input = true;
           }
           System.out.println("Response : " + response.getResponseMessage());
       }

        //Registration
        System.out.println("Enter registration ID(can't be empty) :\r");
        long ID = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Enter student name(can't be empty) :\r");
        String name = scanner.nextLine();

        User.RegisterRequest registerRequest = User.RegisterRequest.newBuilder().setRegistrationID(ID).setName(name).build();
        User.APIResponse response = stub.register(registerRequest);
        System.out.println("Response : " + response.getResponseMessage());
    }
}
