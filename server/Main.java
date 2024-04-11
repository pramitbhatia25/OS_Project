import com.sun.management.OperatingSystemMXBean;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
public class Main {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(8000), 0);
        server.createContext("/system/info", new SystemInfoHandler());
        server.setExecutor(null); // Use default executor
        server.start();
        System.out.println("Server started on port 8000");
    }

    static class SystemInfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                System.out.println("Server hit, returning data.");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    
                Map<String, Double> systemInfo = new HashMap<>();
                try {
                    // CPU Load Percentage
                    double cpuData = getCpuLoadPercentage();
                    systemInfo.put("cpu", cpuData);

                    // Memory Usage Percentage
                    double memData = getMemoryUsagePercentage();
                    systemInfo.put("memory", memData);

                    // Disk Usage Percentage
                    double diskData = readData("typeperf \"\\LogicalDisk(*)\\% Disk Read Time\" -sc 1");
                    systemInfo.put("disk", diskData);

                    // Set response headers
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, 0);

                    // Write JSON response
                    OutputStream os = exchange.getResponseBody();
                    os.write(systemInfo.toString().getBytes());
                    os.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method not allowed
            }
        }
    }

    public static double getCpuLoadPercentage() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osBean.getCpuLoad() * 100;
    }

    public static double getMemoryUsagePercentage() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long totalMemory = osBean.getTotalMemorySize();
        long freeMemory = osBean.getFreeMemorySize();
        return ((double) (totalMemory - freeMemory) / totalMemory) * 100;
    }


    public static double readData(String cmd) throws Exception {
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        double result = 0;
        while ((line = reader.readLine()) != null) {
            if (!line.isEmpty() && Character.isDigit(line.charAt(0))) {
                result = Double.parseDouble(line);
                break;
            }
        }
        reader.close();
        return result;
    }
}
