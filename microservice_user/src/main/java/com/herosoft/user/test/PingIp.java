package com.herosoft.user.test;

import java.io.*;

public class PingIp {
    public static void ping(String ip)  {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec("ping " + ip);

            inputStream = process.getInputStream();

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            String charLine = null;

            boolean isSuccess = false;

            while((charLine= bufferedReader.readLine())!=null){
                if(charLine.indexOf("TTL")!=-1){
                    isSuccess=true;
                    break;
                }
            }

            if(isSuccess){
                System.out.println(ip+"通");
            }
            else {
                System.out.println(ip+"不通");
            }
        }
        catch (IOException ioException){
            ioException.printStackTrace();
        }
        finally {

                try {
                    if(inputStream!=null) {
                        inputStream.close();
                    }
                    if(bufferedReader!=null){
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }
}
