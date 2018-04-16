package cn.harmonycloud.util;


import net.sf.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangyun on 3/21/18.
 */
public class FileUtil {
    public static byte[] FileReadByByte(String filepath) throws IOException {
        File file = new File(filepath);
        InputStream inputStream = null;
        byte[] bytes = null;

        inputStream = new FileInputStream(file);
        bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        return bytes;
    }

    public static void FileWriteByByte(String filepath,byte[] data) throws IOException {
        File file = new File(filepath);
        if (!file.exists()){
            file.createNewFile();
        }

        OutputStream outputStream = new FileOutputStream(file);

        outputStream.write(data);
        outputStream.flush();
        outputStream.close();
    }

    public static List<JSONObject> FileReadByJSON(String filepath){
        File file = new File(filepath);
        BufferedReader reader = null;
        String jsonContent="";
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        JSONObject jo = new JSONObject();
        try{
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null){
                jsonContent += tempString;
                jo = JSONObject.fromObject(tempString);
                jsonList.add(jo);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (reader != null){
                try{
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return jsonList;
        }
    }

    public static void FileWriteByJSON(String filepath, List<JSONObject> data) throws IOException {
        File file = new File(filepath);
        if (!file.exists()){
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        for (JSONObject elem : data){
            bw.write(elem.toString());
            bw.newLine();
        }
        bw.close();
    }

    public static HashMap<String,String> FileReadByLine(String filepath) throws IOException {
        FileInputStream inputStream = new FileInputStream(filepath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        HashMap<String, String> result = new HashMap<String, String>();
        String str = "";
        while((str = bufferedReader.readLine()) != null)
        {
            String[] _switch = str.split(":");
            result.put(_switch[0],_switch[1]);
        }

        //close
        inputStream.close();
        bufferedReader.close();
        return result;
    }

//    public static void main(String[] args) throws IOException {
//        String filepath = "./data/testWrite.json";
//        JSONObject jsob = new JSONObject();
//        jsob.put("name","wangyun");
//        jsob.put("id","21751039");
//        JSONObject jsob2 = new JSONObject();
//        jsob2.put("name","zhanghao");
//        jsob2.put("id","21751111");
//        List<JSONObject> jsonlist = new ArrayList<JSONObject>();
//        jsonlist.add(jsob);
//        jsonlist.add(jsob2);
//        FileUtil.FileWriteByJSON(filepath,jsonlist);
//    }
}
