import ExternalSystem.DataRecords;
import Models.*;
import com.google.gson.*;
import com.opencsv.CSVWriter;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Rest {


    public static void main(String[] args) throws Exception {


        Unirest.config().defaultBaseUrl("https://vv-consulting-candidate-rd-exercise28.veevavault.com/api/v22.3");

        HttpResponse<String> response = Unirest.post("/auth")
                .header("Content-type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .field("username", "rd_candidate28@vv-consulting.com")
                .field("password", "V@ultGrind23")
                .asString();


        Gson gson = new Gson();
        Auth auth = gson.fromJson(response.getBody(), Auth.class);

        String dirFilePath = "/Users/abbassiddiqui-mbpr16/Documents/Interface";


        List<ExternalData> externalData = DataRecords.data();

        List<File> files = new ArrayList<>();

        for (ExternalData data : externalData){
            files.add(new File(dirFilePath + "/" + data.getFilename()));
        }

//        List<File> files = new ArrayList<>(Arrays.asList(
//                new File(dirFilePath + "/Test 1.docx"), new File(dirFilePath + "/Test 2.docx"),
//                new File(dirFilePath + "/Test 3.docx"), new File(dirFilePath + "/Test 4.docx")));

        // Create Files in File Staging Server
        // Have a list of File objects - run a while loop to create all files in File Staging Server



        for (File file: files) {
//            String path = "/Interface/" + file.getName();
//
            HttpResponse<String> fileStagingResponse = Unirest.post("/services/file_staging/items")
                    .header("Authorization", auth.getSessionId())
                    .header("Accept", "application/json")
                    .field("file", file)
                    .field("kind", "file")
                    .field("path", file.getName())
                    .asString();


//            FileStaging fileStaging = gson.fromJson(fileStagingResponse.getBody(), FileStaging.class);
//
//            Path path = gson.fromJson(fileStaging.getPath().toString(), Path.class);
//
//            System.out.println(path);


        }


//        Map<String, String> fieldMap = new HashMap<>();
//        fieldMap.put("externalID","external_id__v");
//        fieldMap.put("approvedDate","approved_date__c");
//        fieldMap.put("name","name__v");
//        fieldMap.put("doctype","subtype__v");
//        fieldMap.put("majorNumber", "major_version_number__v");
//        fieldMap.put("minorNumber", "minor_version_number__v");
//        fieldMap.put("trainingImpact", "training_impact__c");


        List<String[]> csvData = toStringArray(externalData);

        File file = new File(dirFilePath + "/data.csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file, StandardCharsets.UTF_8);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
            writer.writeAll(csvData);

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // From File staging Response extract path, maybe name


        // Retrieve Required doc fields
        // Filter response for required fields from property field

//        HttpResponse<String> requireDocFieldsresponse = Unirest.get("/metadata/objects/documents/types/{type}")
//                .header("Authorization", auth.getSessionId())
//                .header("Accept", "application/json")
//                .routeParam("type", "base_document__v")
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

//         Create Multiple Docs endpoint
        HttpResponse<String> createMultiDocResponse = Unirest.post("/objects/documents/batch")
                .header("Authorization", auth.getSessionId())
                .header("Accept", "application/json")
                .header("Content-Type", "text/csv")
                .body(new FileInputStream("/Users/abbassiddiqui-mbpr16/Documents/Interface/data.csv"))
                .asString();


        System.out.println(createMultiDocResponse.getBody());



    }

    private static List<String[]> toStringArray(List<ExternalData> externalData) {
        List<String[]> records = new ArrayList<String[]>();

        // adding header record
        records.add(new String[] { "name__v", "file", "type__v", "subtype__v", "lifecycle__v", "major_version_number__v",
        "minor_version_number__v", "external_id__v", "approved_date__c", "training_impact__c"});

        Iterator<ExternalData> it = externalData.iterator();
        while (it.hasNext()) {
            ExternalData data = it.next();

            records.add(new String[] { data.getName(), "u1129899/" + data.getFilename(), "Governance and Procedure", data.getDoctype(),
            "Draft to Effective Lifecycle", data.getMajorNumber(), data.getMinorNumber(), data.getExternalId(),
            data.getApprovedDate(), data.getTrainingImpact()});
        }
        return records;
    }


}
