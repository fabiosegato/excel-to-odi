package OdiAtjar;

import OdiAtjar.OdiAC;
import OdiAtjar.OdiRD;

public class Main {
    private static String XlsFile = "";
    private static String LoginXml = "";
    private static String Url = "";
    private static String Driver = "";
    private static String MasterUser = "";
    private static String MasterPwd = "";
    private static String Workrep = "";
    private static String User = "";
    private static String Pwd = "";
    private static OdiAC odi;
    private static OdiRD odird;
    private static String interfacefile;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; ++i) {
            String[] aux;
            if (args[i].toUpperCase().contains("XLSFILE")) {
                aux = args[i].split("=");
                XlsFile = aux[1];
                continue;
            }
            if (args[i].toUpperCase().contains("LOGINXML")) {
                aux = args[i].split("=");
                LoginXml = aux[1];
                continue;
            }
            if (args[i].toUpperCase().contains("INTFILE")) {
                aux = args[i].split("=");
                interfacefile = aux[1];
                continue;
            }
            if (args[i].toUpperCase().contains("URL")) {
                aux = args[i].split("=");
                Url = aux[1];
                continue;
            }
            if (args[i].toUpperCase().contains("DRIVER")) {
                aux = args[i].split("=");
                Driver = aux[1];
                continue;
            }
            if (args[i].toUpperCase().contains("MASTERUSER")) {
                aux = args[i].split("=");
                MasterUser = aux[1];
                continue;
            }
            if (args[i].toUpperCase().contains("MASTERPWD")) {
                aux = args[i].split("=");
                MasterPwd = aux[1];
                continue;
            }
            if (args[i].toUpperCase().contains("WORKREP")) {
                aux = args[i].split("=");
                Workrep = aux[1];
                continue;
            }
            if (args[i].toUpperCase().contains("USER")) {
                aux = args[i].split("=");
                User = aux[1];
                continue;
            }
            if (args[i].toUpperCase().contains("PWD")) {
                aux = args[i].split("=");
                Pwd = aux[1];
                continue;
            }
            if (!args[i].toUpperCase().equals("--HELP")) continue;
            System.out.println("Interface Acelerator");
            System.out.println("");
            System.out.println("Use mode 1:");
            System.out.println("");
            System.out.println("XLSFILE=V && LOGINXML=V");
            System.out.println("");
            System.out.println("\tXLSFILE\t\t\tFile to process.");
            System.out.println("");
            System.out.println("\tLOGINXML\t\tFile containing the ODI'S credencials.");
            System.out.println("");
            System.out.println("\tV\t\t\tParameter's value.");
            System.out.println("");
            System.out.println("Use mode 2:");
            System.out.println("");
            System.out.println("XLSFILE=V && URL=V && DRIVER=V && MASTERUSER=V && MASTERPWD=V");
            System.out.println("WORKREP=V && USER=V && PWD=V");
            System.out.println("");
            System.out.println("\tXLSFILE\t\t\tFile to process.");
            System.out.println("");
            System.out.println("\tURL\t\t\tOdi's repository URL.");
            System.out.println("");
            System.out.println("\tDRIVER\t\t\tOdi's repository JDBC DRIVER.");
            System.out.println("");
            System.out.println("\tMASTERUSER\t\tOdi's repository Master user login.");
            System.out.println("");
            System.out.println("\tMASTERPWD\t\tOdi's repository Master user password.");
            System.out.println("");
            System.out.println("\tWORKREP\t\t\tOdi's Work repository.");
            System.out.println("");
            System.out.println("\tUSER\t\t\tOdi's user's login.");
            System.out.println("");
            System.out.println("\tPWD\t\t\tOdi's user's passsword.");
            System.out.println("");
            System.out.println("\tV\t\t\tParameter's value.");
        }
        if (!args[0].toUpperCase().equals("--HELP") && interfacefile == null) {
            Main.launch();
        } else {
            Main.read();
        }
    }

    private static void launch() throws Exception {
        if (LoginXml.equals("")) {
            odi = new OdiAC(XlsFile, Url, Driver, MasterUser, MasterPwd, Workrep, User, Pwd);
            System.out.println("Manual Connection");
        } else {
            odi = new OdiAC(XlsFile, LoginXml);
            System.out.println("Automatic Connection");
        }
        odi.CreateDDL();
        odi.RemovePk();
        odi.CreateModel();
        odi.CreateSequence();
        odi.RemovePackage();
        odi.RemoveInterface();
        odi.CreateInterface();
        odi.CreatePackage();
    }

    private static void read() throws Exception {
        if (LoginXml.equals("")) {
            odird = new OdiRD(XlsFile, Url, Driver, MasterUser, MasterPwd, Workrep, User, Pwd);
            System.out.println("Manual Connection");
        } else {
            odird = new OdiRD(XlsFile, LoginXml);
            System.out.println("Automatic Connection");
        }
        odird.GenMapping(interfacefile);
    }
}