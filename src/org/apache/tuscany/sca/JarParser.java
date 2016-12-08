package org.apache.tuscany.sca;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by zhou on 16-11-23.
 */
public class JarParser {
    public String namespace;
    public String name;
    public String jarName;

    public JarParser(){
        this.namespace="";
        this.name="";
        this.jarName = "";
    }
    public JarParser parse(String path){
        JarFile jarFile = null;
        InputStream input = null;
        try {
            jarFile = new JarFile(path);
            this.jarName=jarFile.getName().replaceAll(".jar","");
            Enumeration enums = jarFile.entries();
            while(enums.hasMoreElements()){
                JarEntry entry = (JarEntry)enums.nextElement();
                if(entry.getName().endsWith(".composite")){
                    input=jarFile.getInputStream(entry);
                    break;
                }
            }
            SAXReader reader = new SAXReader();
            if(input!=null) {
                Document document = reader.read(input);
                Element root = document.getRootElement();
                this.namespace = root.attributeValue("targetNamespace");
                this.name = root.attributeValue("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}
