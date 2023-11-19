    import java.io.BufferedReader;
    import java.io.InputStreamReader;
    import java.io.PrintWriter;
    import java.net.Socket;

    public class Client {

        public static void main(String[] args) {
            try {
                Socket socket = new Socket("localhost", 12345);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

                String userInput;
                String serverReply;
                while ((userInput = systemIn.readLine()) != null) {
                    out.println(userInput);
                    out.flush();

                    serverReply = in.readLine();
                    System.out.println("Sum: " + serverReply);
                }

                socket.shutdownOutput();

                serverReply = in.readLine();
                System.out.println("Average: " + serverReply);

                socket.shutdownInput();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
