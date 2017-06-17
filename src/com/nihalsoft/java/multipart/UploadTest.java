package com.nihalsoft.java.multipart;

import java.io.File;

public class UploadTest {

    public static void main(String[] args) {

        MultipartFileUploader fu = new MultipartFileUploader("http://localhost:9093/api/dealer/uploadTest");
        //@formatter:off
        fu
            .header("Header-Test", "Header Value")
            .params("name", "Sheik Mohideen")
            .params("age", "40")
            .file("file_content[]", new File("d:/diagram.png"))
            .file("file_content[]", new File("d:/diagram2.png"))
            .file("photo", new File("d:/diagram2.png"))
            .upload();
        //@formatter:on

    }
}