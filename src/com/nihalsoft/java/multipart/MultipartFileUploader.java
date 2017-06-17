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

    private String LINE_END = "\r\n";
    private String TWO_HYPHEN = "--";
    private String BOUNDARY = "*****";

    private String url = "";
    private Map<String, String> params = new HashMap<String, String>();
    private Map<String, String> header = new HashMap<String, String>();

    private List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();

    public MultipartFileUploader() {
        this("");
    }

    public MultipartFileUploader(String url) {
        this.url = url;
        BOUNDARY = "--" + System.currentTimeMillis() + "--";
    }

    public MultipartFileUploader params(String key, String value) {
        params.put(key, value);
        return this;
    }

    public MultipartFileUploader file(String name, File file) {
        Map<String, Object> t = new HashMap<String, Object>();
        t.put("name", name);
        t.put("file", file);
        files.add(t);
        return this;
    }

    public MultipartFileUploader header(String key, String value) {
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

            this._addHeaders(con);

            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
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
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
        Set<Entry<String, String>> headerSet = header.entrySet();
        for (Entry<String, String> entry : headerSet) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
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

        for (Map<String, Object> file : files) {

            String fName = file.get("name").toString();
            File fileObj = (File) file.get("file");

            dos.writeBytes(TWO_HYPHEN + BOUNDARY + LINE_END);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + fName + "\"; filename=\"" + fileObj.getName()
                    + "\"" + LINE_END);
            // dos.writeBytes("Content-Type: image/png" + LINE_END);
            dos.writeBytes(LINE_END);

            FileInputStream fis = new FileInputStream(fileObj);

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
