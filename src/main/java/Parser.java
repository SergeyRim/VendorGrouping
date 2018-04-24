import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Parser {

    public ArrayList<String[]> getVP (String vpFileLocation) {

        ArrayList<String[]> vpData = new ArrayList<String[]>();
        int mfCount=0, groupCount=0, vcCount=0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(vpFileLocation), StandardCharsets.UTF_8))){

            String line, line_prev="";

            while ((line = reader.readLine()) != null) {
                line = line.replace(" ", "");
                line = line.trim();
                if (line.contains("Normalized")) {

                    // vpElement[0] = MF (NormalizedPortInfo)
                    // vpElement[1] = VP group name (DSLIFStatLS)
                    // vpElement[2] = VC ({http://im.ca.com/certifications/snmp}ADSLwithMIB2StatsMib)
                    mfCount++;
                    groupCount++;
                    vcCount++;
                    String[] vpElement = new String[3];

                    vpElement[0]=line.substring(0,line.length()-1);

                    line = reader.readLine().trim();
                    vpElement[1]=line.substring(0,line.length()-1);

                    line = reader.readLine().trim();
                    vpElement[2]=line.substring(1,line.length());

                    //Add String array to ArrayList
                    vpData.add(vpElement);
                    line_prev=line;
                    continue;
                }

                if (line.startsWith("{")) {
                    vcCount++;
                    String[] vpElement = new String[3];
                    vpElement[0]=vpData.get(vpData.size()-1)[0];
                    vpElement[1]=vpData.get(vpData.size()-1)[1];
                    vpElement[2]=line.substring(1,line.length());
                    //Add String array to ArrayList
                    vpData.add(vpElement);
                    line_prev=line;
                    continue;
                }

                if (line.contains(":") && !line.contains("Normalized") && line_prev.startsWith("{")){
                    groupCount++;
                    vcCount++;
                    String[] vpElement = new String[3];
                    vpElement[0]=vpData.get(vpData.size()-1)[0];
                    vpElement[1]=line.substring(0,line.length()-1);
                    line = reader.readLine().trim();
                    vpElement[2]=line.substring(1,line.length());
                    vpData.add(vpElement);
                    line_prev=line;
                }

            }

        } catch (IOException e) {
            System.out.println("Error reading from file "+vpFileLocation+". Exiting.");
            System.out.println(e);
            System.exit(0);

        } catch (Exception e) {
            System.out.println("Error parsing file "+vpFileLocation+": wrong format. Exiting.");
            System.exit(0);
        }


        System.out.println("Parsed "+mfCount+" metric family, "+groupCount+" priority groups and "+vcCount+" vendor certs from priority file.");
        if (mfCount==0 || groupCount==0 || vcCount==0)
            return null;
        else
            return vpData;
    }


}
