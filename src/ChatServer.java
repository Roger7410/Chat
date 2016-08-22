import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatServer {
    /** Map usernames to output stream for broadcasting */
    protected Map<String,PrintStream> users = new HashMap<String, PrintStream>();

    /** Which port should the server listen at? */
    protected int port;

    public static void main(String[] args) throws IOException {
        ChatServer cs = new ChatServer(8080);
        cs.startup();
    }

    public ChatServer(int port) {
        this.port = port;
    }

    public void startup() throws IOException {
        System.out.println("ChatServer listening at port "+port);
        //Socket socket = null;
        ServerSocket ss = new ServerSocket(port);
        while(true){
            //System.out.println("1");
            Socket channel = ss.accept();//accept the client
            //System.out.println("2");
            ChatClientHandler cch = new ChatClientHandler(this,channel);
            Thread t = new Thread(cch);
            t.start();
        }
        // forever do:
        // Wait for connection from a client
        // Create and launch a new client handler
    }

    /** Track that this user name has this output stream */
    public void registerClient(String user, PrintStream out) {
        // broadcast to everyone that user has "connected"
        for(Iterator it = users.keySet().iterator(); it.hasNext(); ){
            Object key = it.next();
            users.get(key).println(user+": connected");
        }
        // print "welcome user" to the client
        out.println("welcome "+user);
        // print to standard out: "user connected"
        System.out.println(user+": connected");
        // add the user,out pair to the users map
        users.put(user,out);
    }

    /** Send a string to everybody but user */
    public void broadcast(String user, String line) {
        for(Iterator it = users.keySet().iterator(); it.hasNext(); ){
            Object key = it.next();
            if(!key.toString().equals(user)){
                users.get(key).println(user+": "+line);
            }
        }
        // For each user u in users
        // print to u's output stream: "user: line" if u not user
    }

    public void disconnect(String user) {
        // remove user from users table
        users.remove(user);
        // broadcast "disconnected" message
        for(Iterator it = users.keySet().iterator(); it.hasNext(); ){
            Object key = it.next();
            users.get(key).println(user+": disconnected");
        }
    }
}