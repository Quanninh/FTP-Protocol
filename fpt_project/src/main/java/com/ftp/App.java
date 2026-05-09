package com.ftp;

import java.io.*;
import java.net.*;
import java.util.*;

public class App 
{
    public static void sendCommand(String command, BufferedWriter out) throws Exception{
        out.write(command);
        out.flush();
    }

    public static String receiveCommand(BufferedReader in) throws Exception{
        String message = in.readLine();

        if (message == null){
            return "No response!!!";
        }

        String full = "";
        if (message.length() > 3 && message.charAt(3) == '-'){
            String code = message.substring(0, 3);
            while((message = in.readLine()) != null){
                full += message + " ";

                if (message.startsWith(code + " ")){
                    break;
                }
            }
            return full;
        }
        return message;
    }

    public static String getIp(String pasv){
        int start = pasv.indexOf('(');
        int end = pasv.indexOf(')');

        String value = pasv.substring(start + 1, end);

        String parts[] = value.trim().split(",");

        String ip = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
        return ip;
    }

    public static int getPort(String pasv){
        int start = pasv.indexOf('(');
        int end = pasv.indexOf(')');

        String value = pasv.substring(start + 1, end);

        String parts[] = value.trim().split(",");

        int port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);
        return port;
    }

    public static void ls(BufferedReader listIn) throws Exception{
        String line = null;

        while((line = listIn.readLine()) != null){
            System.out.println(line);
        }

    }

    public static void retr(InputStream in, String fileName) throws Exception{
        FileOutputStream fos = new FileOutputStream(fileName);

        byte[] file = new byte[1500];

        int bytesRead;

        while((bytesRead = in.read(file)) != -1){
            fos.write(file, 0, bytesRead);
        }
        fos.close();
    }

    public static void main( String[] args )
    {
        try{
            Socket client = new Socket("test.rebex.net", 21);

            Scanner keyboard = new Scanner(System.in);

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            System.out.println(receiveCommand(in));

            sendCommand("USER anonymous\r\n", out);
            System.out.println(receiveCommand(in));

            sendCommand("PASS anything\r\n", out);
            System.out.println(receiveCommand(in));

            String addsv = null;
            int port = 0;

            while(true){
                System.out.println("Enter a command: ");
                String command = keyboard.nextLine();

                if (command.equals("pwd")){
                    sendCommand("PWD\r\n", out);
                    System.out.println(receiveCommand(in));
                }if (command.equals("cd")){
                    sendCommand("CWD\r\n", out);
                    System.out.println(receiveCommand(in));
                }if (command.equals("ls")){
                    sendCommand("PASV\r\n", out);
                    String pasv = receiveCommand(in);
                    System.out.println(pasv);
                    addsv = getIp(pasv);
                    port = getPort(pasv);

                    Socket list = new Socket(addsv, port);
                    BufferedReader listIn = new BufferedReader(new InputStreamReader(list.getInputStream()));
                    sendCommand("LIST\r\n", out);
                    System.out.println(receiveCommand(in));
                    ls(listIn);
                    listIn.close();
                    list.close();
                    System.out.println(receiveCommand(in));
                }if (command.equals("get")){
                    sendCommand("PASV\r\n", out);
                    String pasv = receiveCommand(in);
                    System.out.println(pasv);
                    addsv = getIp(pasv);
                    port = getPort(pasv);

                    Socket socket = new Socket(addsv, port);
                    InputStream fileIn = socket.getInputStream();
                    System.out.println("Enter a file name: ");
                    String fileName = keyboard.nextLine();
                    sendCommand("RETR " + fileName + "\r\n", out);
                    System.out.println(receiveCommand(in));

                    retr(fileIn, fileName);
                    socket.close();
                    System.out.println(receiveCommand(in));
                }
                if (command.equals("quit")){
                    sendCommand("QUIT\r\n", out);
                    System.out.println(receiveCommand(in));
                    break;
                }
            }
            client.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
