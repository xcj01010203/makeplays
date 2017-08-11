package com.xiaotu.makeplays.prepare.utils;
import net.sf.json.JSONArray;
public class Test {  
    public static void main(String[] args) {  
    	PrepareScriptUtil root = new PrepareScriptUtil();
    	root.setId("0");
    	root.setParentId("0");
    	root.setName("root");
    	PrepareScriptUtil node = null;  
        node = new PrepareScriptUtil();
        node.setId("1");
        node.setName("node1");
        node.setParentId("0");
        root.add(node);  
        node = new PrepareScriptUtil();
        node.setId("2");
        node.setName("node1");
        node.setParentId("1");
        root.add(node);
        node = new PrepareScriptUtil();
        node.setId("3");
        node.setName("node1");
        node.setParentId("1");
        root.add(node);
        JSONArray obj = JSONArray.fromObject(root.getChildren());// 不要根  
        System.out.println(obj.toString());  
    }  
} 