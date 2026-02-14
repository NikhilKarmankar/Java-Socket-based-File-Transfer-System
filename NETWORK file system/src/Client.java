import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_IP = "192.168.130.241"; // Server's IP Address
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("Connected to the server.");
            Scanner scanner = new Scanner(System.in);
            String command;

            while (true) {
                System.out.print("Enter command (list, upload, download, delete, exit): ");
                command = scanner.nextLine();
                out.writeUTF(command);

                switch (command.toLowerCase()) {
                    case "list":
                        receiveList(in);
                        break;
                    case "upload":
                        sendFile(out, scanner);
                        break;
                    case "download":
                        receiveFile(out, in, scanner);
                        break;
                    case "delete":
                        deleteFile(out,in, scanner);
                        break;
                    case "exit":
                        System.out.println("Exiting.");
                        return;
                    default:
                        System.out.println(in.readUTF());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receiveList(DataInputStream in) throws IOException {
        String message;
        while (!(message = in.readUTF()).equals("")) {
            System.out.println(message);
        }
    }

    private static void sendFile(DataOutputStream out, Scanner scanner) throws IOException {
        System.out.print("Enter file path to upload: ");
        String filePath = scanner.nextLine();
        File file = new File(filePath);
        out.writeUTF(file.getName());

        FileInputStream fileIn = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileIn.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        fileIn.close();
    }

    private static void receiveFile(DataOutputStream out, DataInputStream in, Scanner scanner) throws IOException {
        System.out.print("Enter file name to download: ");
        String fileName = scanner.nextLine();
        out.writeUTF(fileName);
        String response = in.readUTF();

        if (response.equals("File found")) {
            FileOutputStream fileOut = new FileOutputStream("downloaded_" + fileName);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
            }
            fileOut.close();
            System.out.println("File downloaded successfully.");
        } else {
            System.out.println(response);
        }
    }

    private static void deleteFile(DataOutputStream out,DataInputStream in, Scanner scanner) throws IOException {
        System.out.print("Enter file name to delete: ");
        String fileName = scanner.nextLine();
        out.writeUTF(fileName);
        System.out.println(in.readUTF());
    }
}