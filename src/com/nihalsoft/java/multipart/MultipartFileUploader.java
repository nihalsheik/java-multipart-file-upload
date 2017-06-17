package com.nihalsoft.java.multipart;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MultipartFileUploader {

    private final static String LINE_END = "\r\n";
    private final static String TWO_HYPHEN = "--";
    private final static String BOUNDARY = "*****";

    private String url = "";
    private Map<String, String> params = new HashMap<String, String>();
    private Map<String, String> header = new HashMap<String, String>();

    private List<String> fileNames = new ArrayList<String>();
    private List<File> files = new ArrayList<File>();

    public MultipartFileUploader(String url) {
        this.url = url;
    }

    public MultipartFileUploader params(String key, String value) {
        params.put(key, value);
        return this;
    }

    public MultipartFileUploader file(String name, File file) {
        fileNames.add(name);
        files.add(file);
        return this;
    }

    public MultipartFileUploader addHeader(String key, String value) {
        header.put(key, value);
        return this;
    }

    public String upload() {
        HttpURLConnection con = null;
        String content = "";
        try {

            con = (HttpURLConnection) new URL(this.url).openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);

            DataOutputStream dos = new DataOutputStream(con.getOutputStream());

            this._addHeaders(con);
            this._addFiles(dos);
            this._addParams(dos);

            dos.writeBytes(TWO_HYPHEN + BOUNDARY + TWO_HYPHEN + LINE_END);
            dos.flush();

            InputStream is = null;
            if (con.getResponseCode() != 200) {
                System.out.println("Error Stream");
                is = con.getErrorStream();
            } else {
                System.out.println("Success Stream");
                is = con.getInputStream();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int count = 0;
            while ((count = is.read(data)) != -1) {
                out.write(data, 0, count);
            }
            is.close();
            out.flush();

            content = out.toString();

            System.out.println(content);

            out.close();
            dos.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return content;
    }

    private void _addHeaders(HttpURLConnection con) {
        Set<Entry<String, String>> headerSet = header.entrySet();
        for (Entry<String, String> entry : headerSet) {
            con.addRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private void _addParams(DataOutputStream dos) throws IOException {
        Set<Entry<String, String>> pset = params.entrySet();
        for (Entry<String, String> entry : pset) {
            dos.writeBytes(TWO_HYPHEN + BOUNDARY + LINE_END);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);
            dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + LINE_END);
            dos.writeBytes(LINE_END);
            dos.writeBytes(entry.getValue());
            dos.writeBytes(LINE_END);
        }
    }

    private void _addFiles(DataOutputStream dos) throws IOException {
        int i = 0;
        for (File file : files) {
            String fName = fileNames.get(i++);

            dos.writeBytes(TWO_HYPHEN + BOUNDARY + LINE_END);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + fName + "\"; filename=\"" + file.getName() + "\""
                    + LINE_END);
            // dos.writeBytes("Content-Type: image/png" + LINE_END);
            dos.writeBytes(LINE_END);

            FileInputStream fis = new FileInputStream(file);

            byte[] data = new byte[1024];
            int count = 0;
            while ((count = fis.read(data)) != -1) {
                dos.write(data, 0, count);
            }

            dos.writeBytes(LINE_END);
            dos.flush();
            fis.close();
        }
    }

}
