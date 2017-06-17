Test :

   ```Java
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
    ```
    
-----------------------------------

Receiver : 

    @RequestMapping(value = "/uploadTest", method = RequestMethod.POST)
    public @ResponseBody Response uploadTest(MultipartHttpServletRequest req) throws ApiException {

        System.out.println("Parameter---------------");
        for (Entry<String, String[]> h : req.getParameterMap().entrySet()) {
            System.out.println(h.getKey() + " - " + h.getValue());
        }

        System.out.println("Header-----------------");
        for (Entry<String, List<String>> h : req.getRequestHeaders().entrySet()) {
            System.out.println(h.getKey() + " - " + h.getValue());
        }

        System.out.println("Files------------------");
        for (Entry<String, List<MultipartFile>> h : req.getMultiFileMap().entrySet()) {
            System.out.println(h.getKey() + " - " + h.getValue().size());

            for (MultipartFile t : h.getValue()) {
                System.out.println(t.getName() + " - " + t.getOriginalFilename());
            }
        }

        System.out.println("Done----------------------");
        
        return Response.success();
    }
