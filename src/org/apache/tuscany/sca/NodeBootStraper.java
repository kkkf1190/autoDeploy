package org.apache.tuscany.sca;

/**
 * Created by zhou on 16-12-1.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NodeBootStraper {

    public int startNode(String node){
        Process process = null;
        List<String> processList = new ArrayList<String>();
        try {
            process = Runtime.getRuntime().exec("ps -aux");
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                processList.add(line);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String line : processList) {
            System.out.println(line);
        }
        return 1;
    }
    public static void main(String args[]) {
        NodeBootStraper starter = new NodeBootStraper();
        starter.startNode("node1");
    }
}
