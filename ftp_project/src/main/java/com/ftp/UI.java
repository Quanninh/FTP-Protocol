package com.ftp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;

public class UI extends Application{
    private Client ftpClient = new Client();
    private ListView<String> fileList = new ListView<>();

    private TextArea centralArea = new TextArea();
    private TextArea bottomArea = new TextArea();

    private TextField hostField = new TextField("127.0.0.1");
    private TextField portField = new TextField("21");
    private TextField userField = new TextField("anonymous");
    private PasswordField passField = new PasswordField();
    private Label currentDir = new Label("Current Directory: /");
    private Stage mainStage;
    
    public void setFont(Label label, int size){
        label.setFont(new Font(size));
    }
    
    @Override
    public void start(Stage stage) {
        ftpClient.setLogger(new Logger(){
            @Override
            public void log(String message){
                bottomArea.appendText(message + System.lineSeparator());
            }
        });

        this.mainStage = stage;
        BorderPane pane = new BorderPane();

        // Top panel
        VBox topStage = createTopPanel();
        pane.setTop(topStage);

        // Central panel
        HBox centralStage = createCentralPanel();
        pane.setCenter(centralStage);

        // Bottom panel
        pane.setBottom(bottomArea);

        Scene scene = new Scene(pane, 500, 300);
        stage.setScene(scene);
        stage.setTitle("FTP Client");
        stage.show();
    }

    public VBox createTopPanel(){
        HBox userInfo = new HBox(10);
        userInfo.setPadding(new Insets(10));

        Label hostLabel = new Label("Server Host:");
        setFont(hostLabel, 18);

        Label portLabel = new Label("Port Number:");
        setFont(portLabel, 18);

        Label userLabel = new Label("User Name:");
        setFont(userLabel, 18);

        Label passLabel = new Label("Password:");
        setFont(passLabel, 18);

        //setFont(currentDir, 18);
        Button connBtn = new Button("connect");
        connBtn.setMaxWidth(Double.MAX_VALUE);
        connBtn.setOnAction(e -> connect());

        userInfo.getChildren().addAll(hostLabel, hostField, portLabel, portField, userLabel, userField, passLabel, passField, connBtn);

        VBox topStage = new VBox(10);
        topStage.setPadding(new Insets(10));
        topStage.getChildren().addAll(userInfo, currentDir);

        return topStage;
    }

    public HBox createCentralPanel(){
        fileList.setPrefWidth(600);

        VBox buttons = new VBox(10);
        buttons.setPadding(new Insets(10));

        Button lsBtn = new Button("List");
        Button getBtn = new Button("Get");
        Button putBtn = new Button("Put");
        Button deleteBtn = new Button("Delete");
        Button mkdirBtn = new Button("Make Directory");
        Button rmdirBtn = new Button("Remove Directory");
        Button cdBtn = new Button("cd");
        Button pwdBtn = new Button("pwd");
        Button qBtn = new Button("quit");

        lsBtn.setMaxWidth(Double.MAX_VALUE);
        getBtn.setMaxWidth(Double.MAX_VALUE);
        putBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        mkdirBtn.setMaxWidth(Double.MAX_VALUE);
        rmdirBtn.setMaxWidth(Double.MAX_VALUE);
        cdBtn.setMaxWidth(Double.MAX_VALUE);
        pwdBtn.setMaxWidth(Double.MAX_VALUE);
        qBtn.setMaxWidth(Double.MAX_VALUE);

        lsBtn.setOnAction(e -> getFiles());
        getBtn.setOnAction(e -> get());
        putBtn.setOnAction(e -> put());
        deleteBtn.setOnAction(e -> delete());
        mkdirBtn.setOnAction(e -> mkdir());
        rmdirBtn.setOnAction(e -> rmdir());
        cdBtn.setOnAction(e -> cd());
        pwdBtn.setOnAction(e -> pwd());
        qBtn.setOnAction(e -> quit());

        buttons.getChildren().addAll(lsBtn, getBtn, putBtn, deleteBtn, mkdirBtn, rmdirBtn, cdBtn, pwdBtn, qBtn);

        HBox centralPanel = new HBox(10);
        centralPanel.setPadding(new Insets(10));
        centralPanel.getChildren().addAll(fileList, buttons, centralArea);

        return centralPanel;
    }

    private void quit(){
        try{
            ftpClient.quit();
            mainStage.close();
        }catch(Exception e){
            logCenter(e.getMessage());
        }
    }

    private void pwd(){
        try{
            String m = ftpClient.pwd();
            String[] messParts = m.trim().split("\\s+");
            currentDir.setText("Current Directory: "+ messParts[1].replace("\"", ""));
        }catch(Exception e){
            logCenter(e.getMessage());
        }
    }

    private void cd(){
        TextField pathField = new TextField();
        Label des = new Label("Enter a path here:");

        Stage popUp = new Stage();

        pathField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                popUp.close();
            }
        });

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.getChildren().addAll(des, pathField);

        Scene scene = new Scene(box, 250, 150);
        popUp.setScene(scene);
        popUp.setTitle("Directory Name");
        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.showAndWait();

        String path = pathField.getText();

        new Thread(() -> {
            try{
                ftpClient.cd(path);
                logCenter("cd: " + path);
            }catch(Exception e){
                logCenter(e.getMessage());
            }
        }).start();
    }

    private void rmdir(){
        TextField dirField = new TextField();
        Label des = new Label("Enter a directory name here:");

        Stage popUp = new Stage();

        dirField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                popUp.close();
            }
        });

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.getChildren().addAll(des, dirField);

        Scene scene = new Scene(box, 250, 150);
        popUp.setScene(scene);
        popUp.setTitle("Directory Name");
        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.showAndWait();

        String dir = dirField.getText();

        new Thread(() -> {
            try{
                ftpClient.rmdir(dir);
                logCenter("Removed directory: " + dir);
            }catch(Exception e){
                logCenter(e.getMessage());
            }
        }).start();
    }

    private void mkdir(){
        TextField dirField = new TextField();
        Label des = new Label("Enter a directory name here:");

        Stage popUp = new Stage();

        dirField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                popUp.close();
            }
        });

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.getChildren().addAll(des, dirField);

        Scene scene = new Scene(box, 250, 150);
        popUp.setScene(scene);
        popUp.setTitle("Directory Name");
        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.showAndWait();

        String dir = dirField.getText();

        new Thread(() -> {
            try{
                ftpClient.mkdir(dir);
                logCenter("Created directory: " + dir);
            }catch(Exception e){
                logCenter(e.getMessage());
            }
        }).start();
    }

    private void delete(){
        TextField fileField = new TextField();
        Label des = new Label("Enter a file name here:");

        Stage popUp = new Stage();

        fileField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                popUp.close();
            }
        });

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.getChildren().addAll(des, fileField);

        Scene scene = new Scene(box, 250, 150);
        popUp.setScene(scene);
        popUp.setTitle("File Name");
        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.showAndWait();

        String file = fileField.getText();

        new Thread(() -> {
            try{
                ftpClient.delete(file);
                logCenter("Deleted: " + file);
            }catch(Exception e){
                logCenter(e.getMessage());
            }
        }).start();
    }

    private void put(){
        TextField fileField = new TextField();
        Label des = new Label("Enter a file name here:");

        Stage popUp = new Stage();

        fileField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                popUp.close();
            }
        });

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.getChildren().addAll(des, fileField);

        Scene scene = new Scene(box, 250, 150);
        popUp.setScene(scene);
        popUp.setTitle("File Name");
        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.showAndWait();

        String file = fileField.getText();

        new Thread(() -> {
            try{
                ftpClient.store(file);
                logCenter("Stored: " + file);
            }catch(Exception e){
                logCenter(e.getMessage());
            }
        }).start();
    }

    private void get(){
        //String selectedFile = fileList.getSelectionModel().getSelectedItem();
        TextField fileField = new TextField();
        Label description = new Label("Enter a file name here:");

        Stage popUpStage = new Stage();

        fileField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                popUpStage.close();
            }
        });

        VBox smallBox = new VBox(10);
        smallBox.setPadding(new Insets(10));
        smallBox.getChildren().addAll(description, fileField);

        Scene popUpScene = new Scene(smallBox, 250, 150);
        popUpStage.setScene(popUpScene);
        popUpStage.setTitle("File Name");
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();

        String selectedFile = fileField.getText();

        new Thread(() -> {
            try{
                ftpClient.retrieve(selectedFile);
                logCenter("Downloaded: " + selectedFile);
            }catch(Exception e){
                logCenter(e.getMessage());
            }
        }).start();
    }

    private void connect(){
        try{ 
            ftpClient.connect(hostField.getText(), Integer.parseInt(portField.getText()));

            boolean check = ftpClient.login(userField.getText(), passField.getText());

            if(check){
                logCenter("Connected successfully.");
            }else{
                logCenter("Login failed.");
            }
        }catch(Exception e){
            logCenter(e.getMessage());
        }
    }

    private void getFiles(){
        try{
            fileList.setItems(FXCollections.observableArrayList(ftpClient.ls()));
        }catch(Exception e){
            logCenter(e.getMessage());
        }
    }

    private void logCenter(String message){
        centralArea.appendText(message + System.lineSeparator());
    }

    public static void main(String[] args){
        launch();
    }
}
