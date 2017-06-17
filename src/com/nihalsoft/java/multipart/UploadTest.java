package com.nihalsoft.java.multipart;

import java.io.File;

public class UploadTest {

    public static void main(String[] args) {

        MultipartFileUploader fu = new MultipartFileUploader("http://localhost:9093/api/dealer/uploadTest");
        fu.params("name", "Sheik Mohideen");
        fu.params("age", "40");
        fu.file("file_content[]", new File("d:/diagram.png"));
        fu.file("file_content[]", new File("d:/diagram2.png"));
        fu.upload();

    }
}