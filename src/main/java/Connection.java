import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connection {

    public String put (String id, String xml, String daServer) throws IOException {
        String responseString = new String();
        try {
            URL url = new URL("http://" + daServer + ":8581/rest/vendorpriorities/" + id);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            httpCon.setRequestProperty("Content-Type", "application/xml");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(xml);
            out.close();

            InputStream is = httpCon.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println("PUT response for Metric Family ID " + id + " is: " + response.toString());
            responseString= response.toString();
        } catch (Exception ex) {
            System.out.println("Error communication with Data Aggregator. ");
            System.out.println(ex);
            System.exit(0);
        }
        return responseString;
    }



    public String post (String xml, String daServer) throws IOException {
        String responseString = new String();
        try {
            URL url = new URL("http://"+daServer+":8581/rest/vendorpriorities/filtered");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            httpCon.setRequestProperty("Content-Type", "application/xml");
            OutputStreamWriter out = new OutputStreamWriter( httpCon.getOutputStream());
            out.write(xml);
            out.close();

            InputStream is = httpCon.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            responseString= response.toString();

        } catch (Exception ex){
            System.out.println("Error communication with Data Aggregator. ");
            System.out.println(ex);
            System.exit(0);
        }
        return responseString;
    }


    public String get (String path, String daServer) throws IOException {
        String responseString = new String();
        try {
            URL url = new URL("http://"+daServer+":8581/rest/vendorpriorities/filtered");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            url = new URL("http://"+daServer+":8581/rest/vendorpriorities/"+path);
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("GET");
            httpCon.setRequestProperty("Content-Type", "application/xml");

            InputStream is = httpCon.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            is = httpCon.getInputStream();
            rd = new BufferedReader(new InputStreamReader(is));
            StringBuffer response = new StringBuffer();
            response = new StringBuffer();
            String line;
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            responseString= response.toString();

        } catch (Exception ex){
            System.out.println("Error communication with Data Aggregator. ");
            System.out.println(ex);
            System.exit(0);
        }
        return responseString;

    }




}
