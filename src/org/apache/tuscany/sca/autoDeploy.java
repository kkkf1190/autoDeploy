package org.apache.tuscany.sca;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.*;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Created by zhou on 16-11-23.
 */
public class autoDeploy {

    public static void main(String[] args){
        addJar("domain/rest.jar");
    }

    private static void writeXml(String path,Document document){
        try {
            FileOutputStream fos = new FileOutputStream(path);
            OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
            OutputFormat of = new OutputFormat();
            of.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(osw, of);
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void addJar(String path){
        JarParser parser = new JarParser();
        File jarfile = new File(path);
        String location = jarfile.getAbsolutePath().toString();
        parser = parser.parse(path);
        SAXReader reader = new SAXReader();
        File file = new File("domain/workspace.xml");
        Document document = null;
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = document.getRootElement();
        Element newEle = root.addElement("contribution");
        newEle.addAttribute("location","file:"+location);
        String uri = "http://"+parser.jarName;
        newEle.addAttribute("uri",uri);
        writeXml("domain/workspace.xml",document);

        document = null;
        file = new File("domain/domain.composite");
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        root = document.getRootElement();
        newEle = root.addElement("include");
        newEle.addAttribute("name","ns2:"+parser.name);
        newEle.addAttribute("uri",uri);
        newEle.addAttribute("xmlns:ns2",parser.namespace);
        writeXml("domain/domain.composite",document);

        document = null;
        file = new File("domain/cloud.composite");
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        root = document.getRootElement();
        newEle = root.addElement("include");
        int nodeSize = document.getRootElement().elements().size();
        newEle.addAttribute("name","ns2:node"+nodeSize);
        newEle.addAttribute("uri","http://tuscany.apache.org/cloud");
        newEle.addAttribute("xmlns:ns2","http://tuscany.apache.org/cloud");
        writeXml("domain/cloud.composite",document);

        document = null;
        file = new File("domain/cloud/node1.composite");
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        root = document.getRootElement();
        root.setAttributeValue("name","node"+nodeSize);
        List contents = root.content();
        for(Object content:contents){
            if(content instanceof Namespace && ((Namespace) content).getPrefix()=="c"){
                contents.remove(content);
                root.remove((Namespace)content);
                break;
            }
        }
        Namespace newSpace = new Namespace("c",parser.namespace);
        contents.add(newSpace);
        Element componentEle = root.element("component");
        componentEle.setAttributeValue("name","node"+nodeSize);
        Element tEle = componentEle.element("implementation.node");
        tEle.setAttributeValue("uri",uri);
        tEle.setAttributeValue("composite","c:"+parser.name);
        Element serviceEle = componentEle.element("service");
        List<Element> wsEles = serviceEle.elements();
        for(Element ele:wsEles){
            ele.setAttributeValue("uri","http://localhost:"+(8989+nodeSize-1));
        }

        writeXml("domain/cloud/node"+nodeSize+".composite",document);
    }

}
