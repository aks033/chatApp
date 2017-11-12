import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame{
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;

    //constructor
    public Server(){
        setTitle("The Most Amazing Messenger");
        userText= new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendMessage(e.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add (new JScrollPane(chatWindow));
        setSize(300,150);
        setVisible(true);
    }

    //set up and run the server

    public void serverStart(){
        try{
            server = new ServerSocket(5001  , 100);// backlog: how many people can wait to use this
            while(true){
                try{
                    connWait();
                    setupStreams();
                    whileChatting();
                }catch(EOFException eof){
                    showMessage("\n Server ended the connection");
                }finally{
                    closeConn();
                }
            }
        }catch(IOException io){
            io.printStackTrace();
        }
    }

    private void connWait() throws IOException{
        showMessage("Waiting for Someone to Connect\n");
        connection = server.accept();
        showMessage("Now connected to " + connection.getInetAddress().getHostName());
    }
    //streams to send or receive data
    private void setupStreams()throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        //showMessage("conn streams setup");
    }

    private void whileChatting()throws IOException{
        // during the chat conversation
        String message = "You are now connected! ";
        sendMessage(message);
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n" + message);
                //showMessage("Don't know what's happening!!!");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }while(!message.equals("CLIENT_END"));
    }

    private void closeConn(){
        showMessage("\n Bye Bye Server's out!! ");
        ableToType(false);

        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException io){
            io.printStackTrace();
        }
    }

    private void sendMessage(String message){

        try{
            output.writeObject("SERVER -" + message);
            output.flush();
            showMessage("\nServer - " + message);
        }catch(IOException io){
            chatWindow.append("\n ERROR: cant't send this message");
        }

    }

    private void showMessage(final String text){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        chatWindow.append(text);
                    }

                }
        );
    }

    private void ableToType(final boolean tof){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        userText.setEditable(tof);
                    }

                }
        );
    }
}

