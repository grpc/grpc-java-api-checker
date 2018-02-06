package com.example;

import io.grpc.InternalStatus;
import io.grpc.Grpc;

public class App {
  public static void main(String[] args) {
    System.out.println(InternalStatus.MESSAGE_KEY);
    System.out.println(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
  }
}
