package cm.softinovplus.mobilebiller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegex{

    public TestRegex(){}

    public void display(){
        List<String> allMatches = new ArrayList<>();
        Matcher m = Pattern.compile("^(\\w+)\\s+de\\s+(\\d+)\\s+(\\w+)\\s+effectue\\s+avec\\s+(\\w+)\\s+a\\s+(\\w+)((\\s+\\w+)*)\\s+\\((\\d+)\\)\\s+le\\s+(\\d{4})\\-(\\d{2})\\-(\\d{2})\\s+(\\d{2}):(\\d{2}):(\\d{2})\\.\\s+FRAIS\\s+(\\d+)\\s+\\w+\\.\\s+Transaction\\s+Id:\\s+(\\d+)\\s+;\\s+Reference:\\s+(\\d+)\\.\\s+Nouveau\\s+solde\\s+est:\\s+(\\d+)\\s(\\w+)\\.$")
                .matcher("Transfert de 100000 FCFA effectue avec succes a DIDIER JUNIOR NKALLA EHAWE (237671747569) le 2018-09-26 09:30:45. FRAIS 250 FCFA. Transaction Id: 395587665 ; Reference: 123456789. Nouveau solde est: 33000 FCFA.");
        System.out.println("----------------");


            /*while (m.find()) {
                //allMatches.add(m.group());
                System.out.println(m.group());
            }*/

        if (m.find())
        {
            // get the two groups we were looking for
            String group1 = m.group(1);
            String group2 = m.group(2);
            String group3 = m.group(3);
            String group4 = m.group(4);
            String group5 = m.group(5);
            String group6 = m.group(6);

            // print the groups, with a wee bit of formatting
            System.out.format("'%s',  '%s', '%s',  '%s'\n", group1, group2, group3, group4, group5, group5);
        }

        System.out.println("----------------");
    }

    public static void main(String[] args){
        TestRegex testRegex = new TestRegex();
        testRegex.display();
    }
}
