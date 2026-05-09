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
                full += message + System.lineSeparator();

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

    public static void retr(Socket dataSocket, String fileName) throws Exception{
        FileOutputStream fos = new FileOutputStream(fileName);

        byte[] file = new byte[1500];
        
        InputStream in = dataSocket.getInputStream();

        int bytesRead;

        while((bytesRead = in.read(file)) != -1){
            fos.write(file, 0, bytesRead);
        }
        fos.close();
    }

    public static void stor (Socket dataSocket, String fileName) throws Exception {
        File file = new File(fileName);
        FileInputStream fin = new FileInputStream(file);

        OutputStream out = dataSocket.getOutputStream();

        byte[] buffer = new byte[1500];

        int bytesRead;
        while((bytesRead = fin.read(buffer)) != -1){
            out.write(buffer, 0, bytesRead);
        }

        out.flush();
        fin.close();
    }

    public static void main( String[] args )
    {
        try{
            Socket client = new Socket("test.rebex.net", 21);

            Scanner keyboard = new Scanner(System.in);

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            System.out.println(receiveCommand(in));

            sendCommand("USER demo\r\n", out);
            System.out.println(receiveCommand(in));

            sendCommand("PASS password\r\n", out);
            System.out.println(receiveCommand(in));

            String addsv = null;
            int port = 0;

            while(true){
                System.out.println("Enter a command: ");
                String command = keyboard.nextLine();

                if (command.equals("pwd")){
                    sendCommand("PWD\r\n", out);
                    System.out.println(receiveCommand(in));
                }
                
                else if (command.equals("cd")){
                    System.out.println("Enter a path:");
                    String path = keyboard.nextLine();
                    sendCommand("CWD " + path + "\r\n", out);
                    System.out.println(receiveCommand(in));
                }
                
                else if (command.equals("ls")){
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
                }
                
                else if (command.equals("get")){
                    sendCommand("TYPE I\r\n", out);
                    System.out.println(receiveCommand(in));

                    sendCommand("PASV\r\n", out);
                    String pasv = receiveCommand(in);
                    System.out.println(pasv);
                    addsv = getIp(pasv);
                    port = getPort(pasv);

                    Socket dataSocket = new Socket(addsv, port);
                    System.out.println("Enter a file name: ");
                    String fileName = keyboard.nextLine();
                    sendCommand("RETR " + fileName + "\r\n", out);
                    String response = receiveCommand(in);
                    System.out.println(response);

                    if (response.startsWith("150") || response.startsWith("125")){
                        retr(dataSocket, fileName);
                        dataSocket.close();
                        System.out.println(receiveCommand(in));
                    }else{
                        dataSocket.close();
                    }
                }

                else if (command.equals("put")){
                    sendCommand("TYPE I\r\n", out);
                    System.out.println(receiveCommand(in));

                    sendCommand("PASV\r\n", out);
                    String pasv = receiveCommand(in);
                    System.out.println(pasv);
                    addsv = getIp(pasv);
                    port = getPort(pasv);

                    Socket dataSocket = new Socket(addsv, port);
                    System.out.println("Enter a file name:");
                    String fileName = keyboard.nextLine();
                    sendCommand("STOR " + fileName + "\r\n", out);
                    String response = receiveCommand(in);
                    System.out.println(response);
                    
                    if (response.startsWith("150") || response.startsWith("125")){
                        retr(dataSocket, fileName);
                        dataSocket.close();
                        System.out.println(receiveCommand(in));
                    }else{
                        dataSocket.close();
                    }
                }

                else if (command.equals("delete")){
                    System.out.println("Enter a file name:");
                    String fileName = keyboard.nextLine();

                    sendCommand("DELE " + fileName + "\r\n", out);
                    System.out.println(receiveCommand(in));
                }

                else if (command.equals("mkdir")){
                    System.out.println("Enter a directory name:");
                    String dir = keyboard.nextLine();

                    sendCommand("MKD " + dir + "\r\n", out);
                    System.out.println(receiveCommand(in));
                }

                else if (command.equals("rmdir")){
                    System.out.println("Enter a directory name:");
                    String dir = keyboard.nextLine();

                    sendCommand("RMD " + dir + "\r\n", out);
                    System.out.println(receiveCommand(in));
                }
                else if (command.equals("quit")){
                    sendCommand("QUIT\r\n", out);
                    System.out.println(receiveCommand(in));
                    break;
                }
            }
            keyboard.close();
            client.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
