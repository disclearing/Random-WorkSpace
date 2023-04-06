package com.br.guilhermematthew.nowly.commons.common.utility.system;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.CommonsGeneral;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Machine {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String USER_HOME = System.getProperty("user.home");
    private static String machineIP = "NOT LOADED";

    public static void initialize() {
        machineIP = getRealIP();
        CommonsGeneral.console("§a[Machine] -> Address: " + machineIP);
    }

    public static Long getRamUsage() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2L / 1048576L;
    }

    public static double getCPUUse() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});

            if (list.isEmpty()) {
                return Double.NaN;
            }

            Attribute att = (Attribute) list.get(0);
            Double value = (Double) att.getValue();

            if (value == -1.0) {
                return Double.NaN;
            }

            return (int) (value * 1000) / 10.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static String getMachineIP() {
        return machineIP;
    }

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }

    public static String getOS() {
        if (isWindows()) {
            return "win";
        } else if (isMac()) {
            return "osx";
        } else if (isUnix()) {
            return "uni";
        } else if (isSolaris()) {
            return "sol";
        } else {
            return "err";
        }
    }

    public static String getDiretorio() {
        return (isUnix() ? "/" + getUserHome() + "/" : "C:\\") + CommonsConst.DIR_CONFIG_NAME;
    }

    public static String getSeparador() {
        return isUnix() ? "/" : "\\";
    }

    private static String getRealIP() {
        try {
            URLConnection connect = new URL("http://checkip.amazonaws.com/").openConnection();
            connect.addRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

            Scanner scan = new Scanner(connect.getInputStream());
            StringBuilder sb = new StringBuilder();

            while (scan.hasNext()) {
                sb.append(scan.next());
            }

            scan.close();
            scan = null;

            connect = null;

            return sb.toString();
        } catch (Exception ex) {
            return "Erro";
        }
    }

    public static String getUserHome() {
        return USER_HOME;
    }
}