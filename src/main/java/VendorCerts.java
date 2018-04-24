import sun.net.ConnectionResetException;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VendorCerts {


    public String[] getVendorCertPriorities (String mfFacetName, String daServer) throws IOException {

        String[] credentials = new String[2];
        System.out.println("Getting Vendor Certification Priorities for \""+mfFacetName+"\"");

        // Get MF ID by sending POST request
        String post_request= "<FilterSelect xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"filter.xsd\"> "
                + "<Filter>"
                + "<MetricFamilyVendorPriority.MetricFamilyID type=\"EQUAL\">{http://im.ca.com/normalizer}"+mfFacetName+"</MetricFamilyVendorPriority.MetricFamilyID>"
                + "</Filter>"
                + "<Select use=\"exclude\" isa=\"exclude\">"
                + "<MetricFamilyVendorPriority use=\"exclude\"/>"
                + "</Select>"
                + "</FilterSelect>";

        // Get MF ID by sending POST request
        Connection conn = new Connection();
        String response = conn.post(post_request, daServer);

        String path2 = new String();

        try {
            //Get Metric Family ID
            Matcher matcher = Pattern.compile("(?<=<ID>)(.+)(?=</ID>)").matcher(response.toString());
            matcher.find();
            path2 = matcher.group();
        } catch (Exception ex) {
            System.out.println("Unable to find " + mfFacetName + " in Data Aggregator response. May be forgot to apply on-demand first? Exiting.");
            return null;
        }

        //System.out.println("ID is "+path2);
        credentials[0]=path2;
        System.out.println("ID for "+mfFacetName+" is: "+path2);

        //Get current priority xml by sending GET request
        response = conn.get(path2,daServer);

        credentials[1]=response.toString();
        return credentials;
    }

    public void createModifyVPGroup(String priorityFile, String daServer) throws Exception {

        Parser parser = new Parser();
        VendorCerts vcmf = new VendorCerts ();
        String prevMF;
        String XMLandID[] = new String[2];
        Connection conn = new Connection();

        ArrayList<String[]> vpData = new ArrayList<String[]>();
        vpData = parser.getVP(priorityFile);
        if (vpData==null) {
            //System.out.println("No applicapable data parsed from priority file.");
            return;
        }

        prevMF="";

        for (int i=0;i<vpData.size();i++) {

            if (!vpData.get(i)[0].equals(prevMF) || i==0) {
                if (i!=0) {
                    //Make a PUT request
                    conn.put(XMLandID[0], XMLandID[1], daServer);
                }
                XMLandID = vcmf.getVendorCertPriorities(vpData.get(i)[0], daServer);
                if (XMLandID==null) {
                    return;
                }
                XMLandID[1] = XMLandID[1].replaceAll("<ID>(.*?)</ID>", "");
                XMLandID[1] = XMLandID[1].replaceAll("<MetricFamilyID>(.*?)</MetricFamilyID>", "");
            }

            //Check if VC exists in XML
            if (!XMLandID[1].contains("<VendorCertID>{"+vpData.get(i)[2]+"</VendorCertID>")) {
                System.out.println("ERROR: Vendor certification \""+vpData.get(i)[2]+"\" not found! Skipping.");
            } else {
                //Need to check if the group already exists for specific VC and if other groups are present
                Matcher matcher = Pattern.compile("(?<="+vpData.get(i)[2]+"</VendorCertID><PriorityGroup>)(.*?)(?=</PriorityGroup>)").matcher(XMLandID[1]);
                matcher.find();
                String existing_groups = matcher.group();

                //Get VcNameMib name from {http://im.ca.com/certifications/snmp}VcNameMib
                String vcName = vpData.get(i)[2].split("certifications/snmp}")[1];

                if (existing_groups.equals("")) {
                    System.out.println("\""+vcName+"\" has no groups. Adding new group \""+vpData.get(i)[1]+"\"");
                    XMLandID[1] = XMLandID[1].replaceAll(vpData.get(i)[2]+"</VendorCertID><PriorityGroup>", vpData.get(i)[2]+"</VendorCertID><PriorityGroup>"+vpData.get(i)[1]);
                } else if (existing_groups.contains(vpData.get(i)[1])) {
                    System.out.println("Priority group \""+vpData.get(i)[1]+"\" already exists for \""+vcName+"\"");
                } else {
                    System.out.println("Existing groups for \""+vcName+"\": "+existing_groups);
                    System.out.println ("Adding new group \""+vpData.get(i)[1]+"\" for \'"+vcName+"\"");
                    XMLandID[1] = XMLandID[1].replaceAll(vpData.get(i)[2]+"</VendorCertID><PriorityGroup>", vpData.get(i)[2]+"</VendorCertID><PriorityGroup>"+vpData.get(i)[1]+",");
                }
            }

            //If this is a last element, need to PUT data
            if (i==vpData.size()-1) {
                //Make a PUT Request
                conn.put(XMLandID[0], XMLandID[1], daServer);
            }

            prevMF=vpData.get(i)[0];
        }

    }


}
