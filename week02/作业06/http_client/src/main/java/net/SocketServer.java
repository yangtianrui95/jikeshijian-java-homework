package net;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SocketServer {

    private int port;
    private IServiceWorker mIServiceWorker;

    private final HashMap<String, IServiceWorker> mRegistry = new HashMap<String, IServiceWorker>() {{
        put("oneThread", new OneThreadServiceWorker());
        put("multiThread", new MultiThreadServiceWorker());
        put("threadPool", new ThreadPoolServiceWorker());
    }};


    public static void main(String[] args) throws Exception {
        final String port = System.getProperty("port", "9999");
        final String mode = System.getProperty("mode", "oneThread");

        final SocketServer socketServer = new SocketServer();
        socketServer.setPort(Integer.parseInt(port));
        socketServer.setMode(mode);
        socketServer.start();
    }

    private void setMode(String mode) {
        mIServiceWorker = mRegistry.get(mode);
    }

    private void setPort(int parseInt) {
        port = parseInt;
    }


    public void start() throws Exception {
        System.out.println("serviceWorker :" + mIServiceWorker + " port " + port);
        Objects.requireNonNull(mIServiceWorker);
        mIServiceWorker.start();
    }

    public class OneThreadServiceWorker implements IServiceWorker {

        @Override
        public void start() throws Exception {
            final ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                final Socket socket = serverSocket.accept();
                IServiceWorker.service(socket);
            }
        }
    }

    public class ThreadPoolServiceWorker implements IServiceWorker {
        private final Executor mExecutor
                = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);

        @Override
        public void start() throws Exception {
            final ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                final Socket socket = serverSocket.accept();
                mExecutor.execute(() -> IServiceWorker.service(socket));
            }
        }
    }

    public class MultiThreadServiceWorker implements IServiceWorker {

        @Override
        public void start() throws Exception {
            final ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                final Socket socket = serverSocket.accept();
                new Thread(() -> {
                    try {
                        IServiceWorker.service(socket);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        }
    }

    public interface IServiceWorker {
        void start() throws Exception;

        static void service(Socket socket) {
            try {
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                printWriter.println("HTTP/1.1 200 OK");
                printWriter.println("Content-Type:text/html;charset=utf-8");
                String body = "hello,nio1";
                printWriter.println("Content-Length:" + body.getBytes().length);
                printWriter.println();
                printWriter.write(body);
                printWriter.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

