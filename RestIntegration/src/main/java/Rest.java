import ExternalSystem.DataRecords;
import Models.*;
import com.google.gson.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;


public class Rest {


    public static void main(String[] args) throws Exception {


        // Default base URL to be used for all requests that do not contain a full URL.
        Unirest.config().defaultBaseUrl("https://vv-consulting-candidate-rd-exercise28.veevavault.com/api/v22.3");

        System.out.print("Authentication: ");
        // Authentication
        HttpResponse<String> response = Unirest.post("/auth")
                .header("Content-type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .field("username", "rd_candidate28@vv-consulting.com")
                .field("password", "V@ultGrind23")
                .asString();

        System.out.println("Successful");

        // Use Gson to serialize to JSON and deserialize from JSON
        Gson gson = new Gson();
        Auth auth = gson.fromJson(response.getBody(), Auth.class);

        // Document Storage
        String dirFilePath = "/Users/abbassiddiqui-mbpr16/Documents/Interface";

        System.out.println("Extract External Data");

        // Extract External Data from csv
        BufferedReader reader = new BufferedReader(new FileReader(dirFilePath + "/ExternalData.csv", StandardCharsets.UTF_8));
        ArrayList<ExternalData> externalData = new ArrayList<>();// read line by line
        String record = null;

        while ((record = reader.readLine()) != null) {
            String[] recordSplit = record.split(",");
            ExternalData data= new ExternalData();
            data.setFilename(recordSplit[0]);
            data.setExternalId(recordSplit[1]);
            data.setApprovedDate(recordSplit[2]);
            data.setName(recordSplit[3]);
            data.setDoctype(recordSplit[4]);
            data.setTrainingImpact(Boolean.valueOf(recordSplit[5]));
            data.setCountry(recordSplit[6]);
            data.setDepartment(recordSplit[7]);
            data.setFacility(recordSplit[8]);
            externalData.add(data);
        }

        System.out.println("Extracted");

        // Beginning of csv (removing character)
        for (ExternalData data : externalData){

            data.setFilename(data.getFilename().replace("\uFEFF", ""));

        }


        // External Data
        //List<ExternalData> externalData = DataRecords.data();

        // Data Transformation

        System.out.println("Data Transformation");

        HttpResponse<String> queryDepartmentResponse = Unirest.post("/query")
                .header("Authorization", auth.getSessionId())
                .header("Accept", "application/json")
                .header("X-VaultAPI-DescribeQuery", "true")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("q", "SELECT name__v, id FROM department__v")
                .asString();


        QueryRecordData departmentData = gson.fromJson(queryDepartmentResponse.getBody(), QueryRecordData.class);

        Map<String, String> deptIds = new HashMap<>();
        Map<String, String> facilityIds = new HashMap<>();

        for (Object object : departmentData.getData()){
            VaultObject deptId = gson.fromJson(object.toString(), VaultObject.class);

            deptIds.put(deptId.getName__v(), deptId.getId());

        }

        HttpResponse<String> queryFacilityResponse = Unirest.post("/query")
                .header("Authorization", auth.getSessionId())
                .header("Accept", "application/json")
                .header("X-VaultAPI-DescribeQuery", "true")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("q", "SELECT name__v, id FROM facility__v")
                .asString();


        QueryRecordData facilityData = gson.fromJson(queryFacilityResponse.getBody(), QueryRecordData.class);

        for (Object object : facilityData.getData()){
            VaultObject facilityId = gson.fromJson(object.toString(), VaultObject.class);

            facilityIds.put(facilityId.getName__v(), facilityId.getId());

        }






        for (ExternalData data : externalData){
            String[] splits = data.getCountry().split("\\s");

            if (splits.length > 1){
                splits[0] = splits[0].toLowerCase();
                data.setCountry(String.join("", splits));
            } else {
                splits[0] = splits[0].toLowerCase();
                data.setCountry(splits[0]);
            }
//            data.setCountry(data.getCountry().toLowerCase().replaceAll("\\s", ""));
            data.setDepartment(deptIds.get(data.getDepartment()));
            data.setFacility(facilityIds.get(data.getFacility()));

        }

        System.out.println("Data Transformed");

        // Document List
        List<File> files = new ArrayList<>();

        for (ExternalData data : externalData){
            files.add(new File(dirFilePath + "/" + data.getFilename()));
        }


        // Create Files in File Staging Server
        // Have a list of File objects - run a while loop to create all files in File Staging Server


        System.out.println("Load docs into Staging Server");
        for (File file: files) {

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
        // TODO: FIGURE out training_impact__c field


//        Map<String, String> fieldMap = new HashMap<>();
//        fieldMap.put("externalID","external_id__v");
//        fieldMap.put("approvedDate","approved_date__c");
//        fieldMap.put("name","name__v");
//        fieldMap.put("doctype","subtype__v");
//        fieldMap.put("majorNumber", "major_version_number__v");
//        fieldMap.put("minorNumber", "minor_version_number__v");
//        fieldMap.put("trainingImpact", "training_impact__c");

        System.out.println("Write new data to CSV");

        // Format External Data into String Arrays assist with CSV Input
        List<String[]> csvData = toStringArray(externalData);

        // Write data to CSV
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







        System.out.println("Create Docs in Vault");


//      Create Multiple Docs endpoint
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
        records.add(new String[] { "name__v", "file", "type__v", "subtype__v", "lifecycle__v", "external_id__v", "approved_date__c", "training_impact__c", "country__v",
                "owning_department__v", "owning_facility__v" });

        Iterator<ExternalData> it = externalData.iterator();
        while (it.hasNext()) {
            ExternalData data = it.next();

            records.add(new String[] { data.getName(), "u1129899/" + data.getFilename(), "Governance and Procedure",
                    data.getDoctype(), "Draft to Effective Lifecycle", data.getExternalId(), data.getApprovedDate(),
                    data.getTrainingImpact().toString(), data.getCountry(), data.getDepartment(), data.getFacility()});
        }
        return records;
    }


}
