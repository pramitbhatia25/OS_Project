import com.sun.management.OperatingSystemMXBean;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.text.DecimalFormat;

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

                        String cmd = "wmic cpu get LoadPercentage";
                        String cmd2 = "wmic OS get FreePhysicalMemory";
                        String cmd3 = "wmic computersystem get TotalPhysicalMemory";
                        String cmd4 = "typeperf \"\\LogicalDisk(*)\\% Disk Read Time\" -sc 1";
                        String cmd5 = "typeperf \"Network Interface(*)\\Bytes Total/sec\" -sc 1";
                        String cmd6 = "typeperf \"Network Interface(*)\\Current Bandwidth\" -sc 1";


                        DecimalFormat df = new DecimalFormat("0.00");

                        Process process1 = Runtime.getRuntime().exec(cmd);
                        double cpuData = readData(process1);


                    systemInfo.put("cpu", cpuData);

                    // Memory Usage Percentage
                        Process process2 = Runtime.getRuntime().exec(cmd2);
                        Process process3 = Runtime.getRuntime().exec(cmd3);
                        double memFree = readData(process2);
                        double memTotal = (readData(process3))/1000;
                        double memData = Percentage((memTotal-memFree),memTotal);
                    systemInfo.put("memory", memData);

                    // Disk Usage Percentage
                        Process process4 = Runtime.getRuntime().exec(cmd4);
                        double diskData = readData(process4);
                    systemInfo.put("disk", diskData);

                    // Bandwidth Usage (in bytes)
                        Process process5 = Runtime.getRuntime().exec(cmd5);
                        Process process6 = Runtime.getRuntime().exec(cmd6);
                        double bandTotal = readData(process5);
                        double bandNow = readData(process6);
                        double bandwidthData = ((bandTotal*8)/bandNow)*100;
                    systemInfo.put("bandwidth", (double) bandwidthData);

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

    public static double Percentage(double d, double memTotal) {
		
		double percentage = (d/memTotal)*100;
		
		return percentage;
	}
	public static double readData(Process p) throws Exception {
		try {	
			Process pr = p; 

			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = in.readLine();
			int i = 0;
			String [] lines = new String [6];
			while ((line=in.readLine()) != null) {
				lines[i]= line;
				i++;
			}
			pr.waitFor();
			
			char s = lines[1].charAt(0);
			if(s =='\"') {
				String[] sdata = lines[1].split(",");
				String xrawdata = sdata[1].replaceAll("\""," ");
				String xxrawdata = xrawdata.trim();
				return Double.parseDouble(xxrawdata);
			}
			
			String rawdata = lines[1].trim();
			return Double.parseDouble(rawdata);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0; 
	}


        
}