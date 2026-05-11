# FTP Client Application

A simple Java-based FTP Client with a graphical user interface built using JavaFX.  
This project implements core FTP commands manually using Java sockets and supports file transfer operations through the FTP protocol.

---

# Features

- Connect to an FTP server
- Login using username and password
- View current working directory
- Change directories
- List files and folders
- Upload files to server
- Download files from server
- Delete files
- Create directories
- Remove directories
- Disconnect from server
- Real-time FTP command logs

---

# Technologies Used

- Java
- JavaFX
- TCP Socket Programming
- FTP Protocol
- Multithreading

---

# Project Structure

```text
src/
└── com/
    └── ftp/
        ├── UI.java
        └── Client.java
```

---

# User Interface

The application contains:

## Top Panel
- Server host
- Port number
- Username
- Password
- Connect button

## Center Panel
- File list viewer
- FTP operation buttons
- Client log area

## Bottom Panel
- FTP server responses
- Command logs

---

# Supported FTP Commands

| Command | Description |
|---|---|
| USER | Send username |
| PASS | Send password |
| PWD | Print current directory |
| CWD | Change working directory |
| LIST | List files |
| RETR | Download file |
| STOR | Upload file |
| DELE | Delete file |
| MKD | Create directory |
| RMD | Remove directory |
| QUIT | Disconnect |

---

# Requirements

- Java 17 or newer
- JavaFX SDK

---

# How to Run

## 1. Clone the Repository

```bash
git clone https://github.com/your-username/FTP-Client.git
cd ftp_project
```

---

## 2. Compile

```bash
mvn compile
```

---

## 3. Run

```bash
mvn javafx:run
```

# Example Workflow

1. Enter FTP server information
2. Click **Connect**
3. Press **Refresh** to list files
4. Use:
   - **Download**
   - **Upload**
   - **Delete**
   - **Create Directory**
   - **Remove Directory**
5. Click **Disconnect** when finished

---

# Important Notes

- Passive mode (`PASV`) is used for file transfer.
- Binary mode (`TYPE I`) is used for uploads and downloads.
- File transfer operations run in separate threads to prevent UI freezing.
- FTP responses are displayed in the bottom log panel.

---

# Known Limitations

- No SSL/TLS support (FTP only)
- No drag-and-drop upload
- No progress bar for transfers
- No file chooser integration yet
- No recursive directory operations

---

# Future Improvements

- FTPS support
- SFTP support
- Progress bars
- Drag and drop uploads
- Double-click directory navigation
- File chooser for uploads/downloads
- Better file list formatting
- Connection timeout handling

---

# Screenshots
```markdown
---<img width="1438" height="842" alt="Screenshot 2026-05-11 at 11 11 54" src="https://github.com/user-attachments/assets/6669637c-64c9-4557-b833-eef9a37cd6a9" />
```

---

# Author

Developed by Quan Ninh.
