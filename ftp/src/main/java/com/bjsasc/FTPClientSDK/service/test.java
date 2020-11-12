package com.bjsasc.FTPClientSDK.service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class test {
    public static void main(String[] args) throws ScriptException {
//        ScriptEngineManager scriptManager = new ScriptEngineManager();
//        ScriptEngine scriptEngine = scriptManager.getEngineByName("js");
//        String scriptStr = "([0,1,2,3] || []).indexOf(0 + 1) >= 0";
//        System.out.println(scriptEngine.eval(scriptStr));
        String sql = "slecet * FROM 'asc'.sdf_sdf";
//        String names[] = sql.split("/from/i");

        System.out.println(sql.toLowerCase().split("from")[sql.toLowerCase().split("from").length-1].trim().split("\\.")[0]);//
        System.out.println(sql.toLowerCase().split("from")[sql.toLowerCase().split("from").length-1].trim().split("\\.")[1]);//
    }
}
