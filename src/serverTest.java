import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ServerThread implements Runnable {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    //assigning pool of threads
    public ServerThread(int port, int poolSize)
            throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public void run() { // run the service
        try {
            for (;;) {
                pool.execute(new Handler(serverSocket.accept()));
            }
        } catch (IOException ex) {
            pool.shutdown();
        }
    }
}

class Handler extends JFrame implements Runnable {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;



    Handler(Socket connection) { this.connection = connection; }
    public void run() {
        try {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setBounds(0,0,500,500);
            setUI();
            setupStreams();
            whileChatting();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setUI(){
        setTitle("Messenger");
        userText= new JTextField();
        userText.setFont(new Font("courier", Font.PLAIN, 25));
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
        chatWindow.setBounds(20,20, 500,500);
        chatWindow.setFont(new Font("courier", Font.PLAIN, 20));
        add (new JScrollPane(chatWindow));
        setSize(300,150);
        setVisible(true);

    }

    private void setupStreams()throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        //showMessage("conn streams setup");
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
            showMessage("\nSERVER - " + message);
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

    private void whileChatting()throws IOException{
        // during the chat conversation
        String message = "You are now connected! ";
        sendMessage(message);
        ableToType(true);
        do{
            try{
                System.out.print(connection.getRemoteSocketAddress().toString());
                message = (String) input.readObject();
                showMessage("\n" + message);
                //showMessage("Don't know what's happening!!!");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }while(!message.equals("CLIENT_END"));
    }
}

