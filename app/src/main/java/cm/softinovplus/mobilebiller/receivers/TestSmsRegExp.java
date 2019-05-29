package cm.softinovplus.mobilebiller.receivers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestSmsRegExp {

    public static void main(String [] args){
         String body =
                 "Retrait reussi du 699669940 mboza par 656118851 BLESSED ENTERPRISE. Informations detaillees: ID transaction: CO190515.2030.B98098 Montant: 750 FCFA, Frais : 0 FCFA, Commission: 7.42 FCFA, Montant net: 772.5 FCFA, Solde: 16034.09 FCFA.";
        Matcher matcher1 = Pattern.compile("Retrait", Pattern.CASE_INSENSITIVE).matcher(body);
        if (matcher1.find()){
            System.out.println("OK1");
            //

            Matcher matcher = Pattern.compile("(\\w+)\\s+(\\w+)\\s+du\\s+(\\d+)\\s+(\\w+)((\\s+\\w+)*)\\s+par\\s+(\\d+)\\s+(\\w+)((\\s+\\w+)*)\\s*\\.\\s+Informations\\s+detaillees\\s*:\\s+ID\\s+transaction\\s*:\\s+(\\w+\\.\\w+\\.\\w+)\\,?\\s+Montant\\s*:\\s+(\\d+(\\.\\d+)?)\\s+(\\w+)\\,?\\s+Frais\\s*:\\s+(\\d+(\\.\\d+)?)\\s+(\\w+)\\,?\\s+Commission\\s*:\\s+(\\d+(\\.\\d+)?)\\s+(\\w+)\\,?\\s+Montant net\\s*:\\s+(\\d+(\\.\\d+)?)\\s+(\\w+)\\,?\\s+Solde\\s*:\\s+(\\d+(\\.\\d+)?)\\s+(\\w+)\\.",
                    Pattern.CASE_INSENSITIVE).matcher(body);
            if (matcher.find()){
                System.out.println("OK2");
                String gpr0 = matcher.group(0);
                String gpr1 = matcher.group(1);
                String gpr2 = matcher.group(2);
                String gpr3 = matcher.group(3);
                String gpr4 = matcher.group(4);
                String gpr5 = matcher.group(5);
                String gpr6 = matcher.group(6);
                String gpr7 = matcher.group(7);
                String gpr8 = matcher.group(8);
                String gpr9 = matcher.group(9);
                String gpr10 = matcher.group(10);
                String gpr11 = matcher.group(11);
                String gpr12 = matcher.group(12);
                String gpr13 = matcher.group(13);
                String gpr14 = matcher.group(14);
                String gpr15 = matcher.group(15);
                String gpr16 = matcher.group(16);
                String gpr17 = matcher.group(17);
                String gpr18 = matcher.group(18);
                String gpr19 = matcher.group(19);
                String gpr20 = matcher.group(20);
                String gpr21 = matcher.group(21);
                String gpr22 = matcher.group(22);
                String gpr23 = matcher.group(23);
                String gpr24 = matcher.group(24);
                String gpr25 = matcher.group(25);
                String gpr26 = matcher.group(26);
                System.out.println(String.format(
                        "\n(g0= %s, \ng1= %s, \ng2= %s, \ng3= %s, \ng4= %s, \ng5= %s, \ng6= %s, \ng7= %s, \ng8= %s, \ng9= %s, \ng10= %s, \ng11= %s, \ng12= %s, \ng13= %s, \ng14= %s, \ng15= %s, \ng16= %s, \ng17= %s, \ng18= %s, \ng19= %s, \ng20= %s, \ng21= %s, \ng22= %s, \ng23= %s, \ng24= %s, \ng25= %s, \ng26= %s)",
                        gpr0, gpr1, gpr2, gpr3, gpr4, gpr5, gpr6, gpr7, gpr8, gpr9, gpr10, gpr11, gpr12, gpr13, gpr14, gpr15, gpr16, gpr17, gpr18, gpr19, gpr20, gpr21, gpr22, gpr23, gpr24, gpr25, gpr26));
            }else {
                System.out.println("KO2");
            }

        }else{
            System.out.println("KO1");
        }
    }

}




/*
package cm.softinovplus.mobilebiller.receivers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestSmsRegExp {

    public static void main(String [] args){
        String body =
                "Depot vers 699992396 ABDOU AZIZ reussi from 656118851 BLESSED ENTERPRISE. Informations detaillees : Montant transaction : 16000FCFA, ID de Transaction : CI190515.2032.B37161, Frais : 0FCFA, Commission : 32  FCFA, Montant Net Debite : 15968FCFA, Nouveau Solde : 66.09FCFA.";
        Matcher matcher1 = Pattern.compile("Depot", Pattern.CASE_INSENSITIVE).matcher(body);
        if (matcher1.find()){
            System.out.println("OK1");
            //

            Matcher matcher = Pattern.compile("(\\w+)\\s+(\\w+)\\s+(\\d+)\\s+(\\w+)((\\s+\\w+)*)\\s+(\\w+)\\s+(de|from)\\s+(\\d+)\\s+(\\w+)((\\s+\\w+)*)\\s*\\.\\s+Informations\\s+detaillees\\s+:\\s+Montant transaction\\s+:\\s+(\\d+(\\.\\d+)?)\\s*(\\w+)\\,\\s+ID\\s+de\\s+Transaction\\s+:\\s+(\\w+\\.\\w+\\.\\w+)\\,\\sFrais\\s+:\\s+(\\d+)\\s*(\\w+)\\,\\s+Commission\\s+:\\s+(\\d+(\\.\\d+)?)\\s*(\\w+)\\,\\s+Montant\\s+Net\\s+Debite\\s+:\\s+(\\d+(\\.\\d+)?)(\\w+)\\,\\s+Nouveau\\s+Solde\\s+:\\s+(\\d+(\\.\\d+)?)\\s*(\\w+)\\.",
                    Pattern.CASE_INSENSITIVE).matcher(body);
            if (matcher.find()){
                System.out.println("OK2");
                String gpr0 = matcher.group(0);
                String gpr1 = matcher.group(1);
                String gpr2 = matcher.group(2);
                String gpr3 = matcher.group(3);
                String gpr4 = matcher.group(4);
                String gpr5 = matcher.group(5);
                String gpr6 = matcher.group(6);
                String gpr7 = matcher.group(7);
                String gpr8 = matcher.group(8);
                String gpr9 = matcher.group(9);
                String gpr10 = matcher.group(10);
                String gpr11 = matcher.group(11);
                String gpr12 = matcher.group(12);
                String gpr13 = matcher.group(13);
                String gpr14 = matcher.group(14);
                String gpr15 = matcher.group(15);
                String gpr16 = matcher.group(16);
                String gpr17 = matcher.group(17);
                String gpr18 = matcher.group(18);
                String gpr19 = matcher.group(19);
                String gpr20 = matcher.group(20);
                String gpr21 = matcher.group(21);
                String gpr22 = matcher.group(22);
                String gpr23 = matcher.group(23);
                String gpr24 = matcher.group(24);
                String gpr25 = matcher.group(25);
                String gpr26 = matcher.group(26);
                System.out.println(String.format(
                        "\n(g0= %s, \ng1= %s, \ng2= %s, \ng3= %s, \ng4= %s, \ng5= %s, \ng6= %s, \ng7= %s, \ng8= %s, \ng9= %s, \ng10= %s, \ng11= %s, \ng12= %s, \ng13= %s, \ng14= %s, \ng15= %s, \ng16= %s, \ng17= %s, \ng18= %s, \ng19= %s, \ng20= %s, \ng21= %s, \ng22= %s, \ng23= %s, \ng24= %s, \ng25= %s, \ng26= %s)",
                        gpr0, gpr1, gpr2, gpr3, gpr4, gpr5, gpr6, gpr7, gpr8, gpr9, gpr10, gpr11, gpr12, gpr13, gpr14, gpr15, gpr16, gpr17, gpr18, gpr19, gpr20, gpr21, gpr22, gpr23, gpr24, gpr25, gpr26));
            }else {
                System.out.println("KO2");
            }

        }else{
            System.out.println("KO1");
        }
    }

}
*/
