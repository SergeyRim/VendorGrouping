

public class Main {

   public static void main (String[] args) throws Exception {

       System.out.println("");
       System.out.println("This tool will create/modify vendor certification priority grouping according to priority.txt file. (c) CA 2018");
       System.out.println("");

       if ( args.length<1 || args.length>2 ) {
           System.out.println("Usage: java -jar VendorGrouping.jar <da_server_hostname/ip> <priority_file>");
           System.out.println("<priority_file> is optional. priority.txt will be used if unspecified.");
           System.out.println("");
           System.out.println("Ex.: java -jar VendorGrouping.jar 192.168.1.5");
           return;
       }

       String filename = new String();
       if(args.length==2)
           filename = args[1];
       else
           filename = "priority.txt";

       VendorCerts vc = new VendorCerts();
       vc.createModifyVPGroup(filename,args[0]);

   }

}
