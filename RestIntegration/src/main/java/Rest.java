import Models.Auth;
import Models.DocFields;
import Models.VaultField;
import com.google.gson.*;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;


public class Rest {


    public static void main(String[] args) throws Exception {


        Unirest.config().defaultBaseUrl("https://supportvaults-platform-gr-vv1-1077-community.veevavault.com/api/v22.3");

        HttpResponse<String> response = Unirest.post("/auth")
                .header("Content-type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .field("username", "abbas.siddiqui@supportvaults.com")
                .field("password", "V@ultWork23")
                .asString();

//        System.out.println(response.getBody().toPrettyString());

        Gson gson = new Gson();
        Auth auth = gson.fromJson(response.getBody(), Auth.class);
        System.out.println(auth.getSessionId());

        String dirFilePath = "/Users/abbassiddiqui-mbpr16/Documents/Interface";



        List<File> files = new ArrayList<>(Arrays.asList(
                new File(dirFilePath + "/Test 1.docx"), new File(dirFilePath + "/Test 2.docx"),
                new File(dirFilePath + "/Test 3.docx"), new File(dirFilePath + "/Test 4.docx")));

        // Create Files in File Staging Server
        // Have a list of File objects - run a while loop to create all files in File Staging Server

        List<String> ftpPaths = new ArrayList<String>();


        for (File file: files) {
            String path = "u8790933/Integration/" + file.getName();
//            HttpResponse<String> fileStagingResponse = Unirest.post("/services/file_staging/items")
//                    .header("Authorization", auth.getSessionId())
//                    .header("Accept", "application/json")
//                    .field("file", file)
//                    .field("kind", "file")
//                    .field("path", path)
//                    .asString();

            ftpPaths.add(path);

        }


        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("externalID","external_id__v");
        fieldMap.put("approvedDate","approved_date__c");
        fieldMap.put("name","name__v");
        fieldMap.put("doctype","subtype__v");
        fieldMap.put("majorNumber", "major_version_number__v");
        fieldMap.put("minorNumber", "minor_version_number__v");
        fieldMap.put("docLifeycle", "lifecycle__v");
        fieldMap.put("trainingImpact", "training_impact__c");




        // From File staging Response extract path, maybe name


        // Retrieve Required doc fields
        // Filter response for required fields from property field

//        HttpResponse<String> requireDocFieldsresponse = Unirest.get("/metadata/objects/documents/types/{type}")
//                .header("Authorization", auth.getSessionId())
//                .header("Accept", "application/json")
//                .routeParam("type", "abbas_doc__c")
//                .asString();
//
//
//        DocFields docFields = gson.fromJson(requireDocFieldsresponse.getBody(), DocFields.class);
//
//
//
//        Collection<String> vaultFields = new ArrayList<>();
//
//        for (Object object : docFields.getProperties()){
//            VaultField vaultField = gson.fromJson(object.toString(), VaultField.class);
//
//            if (vaultField.getRequired()){
//
//                vaultFields.add(vaultField.getName());
//            }
//
//        }
//        System.out.println(vaultFields);
        // external_id__v
        // approved_date__c




        // governance_and_procedure__c

        //Create CSV with OpenCSV dependency taking required fields, file path (ftp), external system id field
        // add data to csv file

        // Create Multiple Docs endpoint
//        HttpResponse<String> createMultiDocResponse = Unirest.post("/objects/documents/batch")
//                .header("Authorization", auth.getSessionId())
//                .header("Accept", "application/json")
//                .header("Content-Type", "text/csv")
//                .body(new FileInputStream(new File("/Users/abbassiddiqui-mbpr16/Downloads/vault-create-documents-from-uploaded-files-sample-csv-input.csv")))
//                .asString();


//        System.out.println(createMultiDocResponse.getBody());



    }
}
