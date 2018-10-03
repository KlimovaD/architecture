package softwarearchs.integration;


import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import softwarearchs.facade.Facade;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Repair {
    String receiptNumber;
    String deviceSerial;
    String deviceBrand;
    String deviceModel;
    String client;
    String master;
}

public class GetRepair {
    private static Facade facade = new Facade();

    public GetRepair() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/get", new GetHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("The server is running");
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
  /*  public static void main(String[] args) throws Exception {

    }*/

    static class GetHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            StringBuilder response = new StringBuilder();
            Map<String, String> parms = GetRepair.queryToMap(httpExchange.getRequestURI().getQuery());
            try {
                Gson gson = new Gson();
                Repair repair = GetRepair.getRepair(parms.get("repair"));
                response.append(gson.toJson(repair));
            } catch (Exception e) {
                response.append(e.getMessage());
            }
            GetRepair.writeResponse(httpExchange, response.toString());
        }
    }

    public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    private static Repair getRepair(String repair) throws Exception {
        Repair rep = new Repair();
        rep.receiptNumber = repair;
        rep.deviceSerial = facade.getReceipt(repair).getDevice().getSerialNumber();
        rep.deviceBrand = facade.getReceipt(repair).getDevice().getDeviceBrand();
        rep.deviceModel = facade.getReceipt(repair).getDevice().getDeviceModel();
        rep.client = facade.getReceipt(repair).getClient().getFIO();
        rep.master = facade.getReceipt(repair).getMaster() == null ? "Master not assigned yet" :
                facade.getReceipt(repair).getMaster().getFIO();

        return rep;
    }
}
