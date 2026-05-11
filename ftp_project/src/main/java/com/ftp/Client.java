package com.ftp;

import java.io.*;
import java.net.*;
import java.util.*;

interface Logger {
    void log(String message);
}

public class Client {
    private Logger logger;
    private Socket client;
    private BufferedReader in;
    private BufferedWriter out;

    public void setLogger(Logger logger){
        this.logger = logger;
    }

    private void log(String message){
        if (logger != null){
            logger.log(message);
        }
    }

    public void connect(String ip, int port) throws Exception {
        client = new Socket(ip, port);

        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream())); 

        log(receiveCommand(in));
    }

    public void sendCommand(String command) throws Exception{
        out.write(command);
        out.flush();
    }

    public String receiveCommand(BufferedReader in) throws Exception{
        String message = in.readLine();

        if (message == null){
            return "No response!!!";
        }

        String full = message + System.lineSeparator();
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

    public String getIp(String pasv){
        int start = pasv.indexOf('(');
        int end = pasv.indexOf(')');

        String value = pasv.substring(start + 1, end);

        String parts[] = value.trim().split(",");

        String ip = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
        return ip;
    }

    public int getPort(String pasv){
        int start = pasv.indexOf('(');
        int end = pasv.indexOf(')');

        String value = pasv.substring(start + 1, end);

        String parts[] = value.trim().split(",");

        int port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);
        return port;
    }

    public boolean login(String user, String pass) throws Exception{
        sendCommand("USER " + user + "\r\n");
        log(receiveCommand(in));

        sendCommand("PASS " + pass + "\r\n");
        String response = receiveCommand(in);
        log(response);

        if (!response.startsWith("230")){
            return false;
        }
        return true;
    }

    public String pwd() throws Exception{
        sendCommand("PWD\r\n");
        String response = receiveCommand(in);
        log(response);
        return response;
    }

    public void cd(String path) throws Exception{
        sendCommand("CWD " + path + "\r\n");
        log(receiveCommand(in));
    }

    public List<String> ls() throws Exception{
        List<String> files = new ArrayList<>();
        sendCommand("PASV\r\n");
        String pasv = receiveCommand(in);
        log(pasv);

        String addsv = getIp(pasv);
        int port = getPort(pasv);

        Socket list = new Socket(addsv, port);
        BufferedReader listIn = new BufferedReader(new InputStreamReader(list.getInputStream()));
        sendCommand("LIST\r\n");
        log(receiveCommand(in));

        String line = null;

        while((line = listIn.readLine()) != null){
            // String[] fileParts = line.trim().split("\\s+", 9);
            files.add(line);
        }

        listIn.close();
        list.close();
        log(receiveCommand(in));
        return files;
    }

    public void retrieve(String fileName) throws Exception{
        sendCommand("TYPE I\r\n");
        log(receiveCommand(in));

        sendCommand("PASV\r\n");
        String pasv = receiveCommand(in);
        log(pasv);
        String addsv = getIp(pasv);
        int port = getPort(pasv);

        Socket dataSocket = new Socket(addsv, port);
        
        sendCommand("RETR " + fileName + "\r\n");
        String response = receiveCommand(in);
        log(response);

        if (response.startsWith("150") || response.startsWith("125")){
            FileOutputStream fos = new FileOutputStream(fileName);

            byte[] file = new byte[1500];
            
            InputStream instr = dataSocket.getInputStream();

            int bytesRead;

            while((bytesRead = instr.read(file)) != -1){
                fos.write(file, 0, bytesRead);
            }
            fos.close();

            dataSocket.close();
            log(receiveCommand(in));
        }else{
            dataSocket.close();
        }
    }

    public void store(String fileName) throws Exception{
        sendCommand("TYPE I\r\n");
        log(receiveCommand(in));

        sendCommand("PASV\r\n");
        String pasv = receiveCommand(in);
        log(pasv);
        String addsv = getIp(pasv);
        int port = getPort(pasv);

        Socket dataSocket = new Socket(addsv, port);
        
        sendCommand("STOR " + fileName + "\r\n");
        String response = receiveCommand(in);
        log(response);
        
        if (response.startsWith("150") || response.startsWith("125")){
            File file = new File(fileName);
            FileInputStream fin = new FileInputStream(file);

            OutputStream outstr = dataSocket.getOutputStream();

            byte[] buffer = new byte[1500];

            int bytesRead;
            while((bytesRead = fin.read(buffer)) != -1){
                outstr.write(buffer, 0, bytesRead);
            }

            outstr.flush();
            fin.close();
            dataSocket.close();
            log(receiveCommand(in));
        }else{
            dataSocket.close();
        }
    }

    public void delete(String fileName) throws Exception{
        sendCommand("DELE " + fileName + "\r\n");
        log(receiveCommand(in));
    }

    public void mkdir(String dir) throws Exception{
        sendCommand("MKD " + dir + "\r\n");
        log(receiveCommand(in));
    }

    public void rmdir(String dir) throws Exception{
        sendCommand("RMD " + dir + "\r\n");
        log(receiveCommand(in));
    }

    public void quit() throws Exception{
        sendCommand("QUIT\r\n");
        log(receiveCommand(in));
        in.close();
        out.close();
        client.close();
    }
}
