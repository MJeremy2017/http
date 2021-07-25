import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

public class WebServer {
    /*
    * endpoint
    *   /status  -- get status of the server
    *   /task  -- post method to execute task
    * */

    private static final String STATUS_ENDPOINT = "/status";
    private static final String TASK_ENDPOINT = "/task";
    private static final int NUM_THREADS = 8;
    private final int port;
    private HttpServer httpServer;

    public static void main(String[] args) {
        int port = (args.length == 1) ? Integer.parseInt(args[0]) : 8080;
        WebServer webServer = new WebServer(port);
        webServer.startServer();
        System.out.println("Server started ...");
    }

    public WebServer(int serverPort) {
        port = serverPort;
    }

    public void startServer() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // bind endpoint to handler
        HttpContext statusContext = httpServer.createContext(STATUS_ENDPOINT);
        statusContext.setHandler(this::statusCheckHandler);  // double colon works as lambda expression

        HttpContext taskContext = httpServer.createContext(TASK_ENDPOINT);
        taskContext.setHandler(this::taskHandler);

        httpServer.setExecutor(Executors.newFixedThreadPool(NUM_THREADS));
        httpServer.start();
    }

    private void taskHandler(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
        Headers headers = exchange.getRequestHeaders();
        if (headers.containsKey("X-Test") && headers.get("X-Test").get(0).equalsIgnoreCase("true")) {
            String dummyResponse = "123\n";
            sendResponse(exchange, dummyResponse.getBytes());
            return;
        }

        boolean isDebugMode = headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true");

        long startTime = System.currentTimeMillis();
        byte[] requestByte = exchange.getRequestBody().readAllBytes();
        byte[] result = calculateResult(requestByte);
        long endTime = System.currentTimeMillis();
        if (isDebugMode) {
            String debugMsg = String.format("Operation took %d milliseconds", endTime - startTime);
            exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMsg));
        }
        sendResponse(exchange, result);
    }

    private byte[] calculateResult(byte[] requestByte) {
        String requestStr = new String(requestByte);
        String[] strNums = requestStr.split(",");
        BigInteger result = BigInteger.valueOf(1);
        for (String n : strNums) {
            BigInteger num = new BigInteger(n);
            result = result.multiply(num);
        }
        String responseMsg = String.format("Result is %s\n", result);
        return responseMsg.getBytes();
    }

    private void statusCheckHandler(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        String responseMsg = "Server is alive\n";
        sendResponse(exchange, responseMsg.getBytes());
    }

    private void sendResponse(HttpExchange exchange, byte[] responseBytes) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
    }

}
