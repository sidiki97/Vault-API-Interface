
import Models.*;
import Models.Error;
import com.google.gson.*;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Rest {

//    static Logger logger = Logger.getLogger(Rest.class.getName());

    public static void main(String[] args) throws Exception {


        LogManager lgmngr = LogManager.getLogManager();
        Logger log = lgmngr.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // Default base URL to be used for all requests that do not contain a full URL.
        Unirest.config().defaultBaseUrl("https://vv-consulting-candidate-rd-exercise28.veevavault.com/api/v22.3");



        log.log(Level.INFO, "Authenticating to Vault ...");
//        System.out.print("Authentication: ");
        UserInfo userInfo = new UserInfo();


        // Authentication
        HttpResponse<String> response = Unirest.post("/auth")
                .header("Content-type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .header("X-VaultAPI-ClientID", "veeva-vault-create-docs-interface")
                .field("username", userInfo.getUSERNAME())
                .field("password", userInfo.getPASSWORD())
                .asString();




        // Use Gson to serialize to JSON and deserialize from JSON
        Gson gson = new Gson();
        Auth auth = gson.fromJson(response.getBody(), Auth.class);

        if (auth.getResponseStatus().equals("SUCCESS")){
            log.log(Level.INFO, "AUTHENTICATION SUCCESSFUL");
        }  else {
            log.log(Level.INFO, auth.getResponseMessage());
            System.exit(1);
        }

        // Document Storage
        String dirFilePath = "/Users/abbassiddiqui-mbpr16/Documents/Interface";

        System.out.println("Extract External Data");

        // Extract External Data from csv
        BufferedReader reader = new BufferedReader(new FileReader(dirFilePath + "/ExternalData.csv"));
        ArrayList<ExternalData> externalData = new ArrayList<>();// read line by line
        String record;

        List<String> headers = new ArrayList<>();

        int i = 0;
        while ((record = reader.readLine()) != null) {
            String[] recordSplit = record.split(",");
            if (i == 0){
                for (String header: recordSplit){
                    headers.add(header);
                }
                i++;
            } else {

                ExternalData data= new ExternalData();
                data.setFilename(recordSplit[0]);
                data.setName(recordSplit[1]);
                data.setDoctype(recordSplit[2]);
                data.setSubtype(recordSplit[3]);
                data.setLifecycle(recordSplit[4]);
                data.setExternalId(recordSplit[5]);
                data.setApprovedDate(recordSplit[6]);
                data.setTrainingImpact(Boolean.valueOf(recordSplit[7]));
                data.setCountry(recordSplit[8]);
                data.setDepartment(recordSplit[9]);
                data.setFacility(recordSplit[10]);
                externalData.add(data);
            }

        }

        System.out.println("Extracted");


        // Data Transformation

        System.out.println("Data Transformation");

        // Retrieve Required doc fields
        // Filter response for required fields from property field

        Map<String, String> headerData = new HashMap<>();
        String doctypeName = "";

        HttpResponse<String> requireDoctype = Unirest.get("/metadata/objects/documents/types")
                .header("Authorization", auth.getSessionId())
                .header("Accept", "application/json")
                .asString();

        DocType docType = gson.fromJson(requireDoctype.getBody(), DocType.class);

        if (docType.getResponseStatus().equals("SUCCESS")){
            log.log(Level.INFO, "DOCTYPE RETRIEVAL SUCCESSFUL");
        }  else {
            Errors e = gson.fromJson(docType.getErrors()[0].toString(), Errors.class);

            log.log(Level.INFO, e.getType() + ": " + e.getMessage());
            System.exit(1);
        }

        for (Object object : docType.getTypes()) {

            TypeData typeData = gson.fromJson(object.toString(), TypeData.class);
            if (typeData.getLabel().equals("Governance and Procedure")) {
                doctypeName = typeData.getValue().substring(typeData.getValue().lastIndexOf('/') + 1);
            }

        }


        HttpResponse<String> requireDocFieldsresponse = Unirest.get("/metadata/objects/documents/types/{type}/")
                .header("Authorization", auth.getSessionId())
                .header("Accept", "application/json")
                .routeParam("type", doctypeName)
                .asString()
                .ifFailure(stringHttpResponse -> {
                    log.log(Level.INFO, "ERROR IN GET REQUEST: " + stringHttpResponse.getStatusText());
                });


        DocFields docFields = gson.fromJson(requireDocFieldsresponse.getBody(), DocFields.class);

        if (docFields.getResponseStatus().equals("SUCCESS")){
            log.log(Level.INFO, "DOCTYPE FIELDS RETRIEVAL SUCCESSFUL");
        }  else {
//            Errors e = gson.fromJson(docFields.getErrors()[0].toString(), Errors.class);
//
//            log.log(Level.INFO, e.getType() + ": " + e.getMessage());
//            System.exit(1);
            errorHandler(docFields, gson, log);
        }


        Collection<String> vaultFields = new ArrayList<>();


        for (Object object : docFields.getProperties()){
            VaultField vaultField = gson.fromJson(object.toString(), VaultField.class);

            if (vaultField.getRequired()){

                vaultFields.add(vaultField.getName());
                if (headers.indexOf(vaultField.getLabel()) != -1){
                    headerData.put(vaultField.getLabel(), vaultField.getName());
                }

            } else{
                if (headers.indexOf(vaultField.getLabel()) != -1) {
                    headerData.put(vaultField.getLabel(), vaultField.getName());
                }
            }

        }




        HttpResponse<String> queryDepartmentResponse = Unirest.post("/query")
                .header("Authorization", auth.getSessionId())
                .header("Accept", "application/json")
                .header("X-VaultAPI-DescribeQuery", "true")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("q", "SELECT name__v, id FROM department__v")
                .asString();


        QueryRecordData departmentData = gson.fromJson(queryDepartmentResponse.getBody(), QueryRecordData.class);

        Map<String, String> deptIds = new HashMap<>();

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

        Map<String, String> facilityIds = new HashMap<>();

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

        Map<String, String> fileStaged = new HashMap<>();

        System.out.println("Load docs into Staging Server");
        for (File file: files) {

            HttpResponse<String> fileStagingResponse = Unirest.post("/services/file_staging/items")
                    .header("Authorization", auth.getSessionId())
                    .header("Accept", "application/json")
                    .field("file", file)
                    .field("kind", "file")
                    .field("path", file.getName())
                    .asString();

            FileStaging fileStaging = gson.fromJson(fileStagingResponse.getBody(), FileStaging.class);

            if (fileStaging.getResponseStatus().equals("SUCCESS")){
                Path path = gson.fromJson(fileStaging.getData().toString(), Path.class);

                if (path.getName().equals(file.getName())) {
                    fileStaged.put(path.getName(), path.getPath());
                }
                log.log(Level.INFO, path.getName() + " UPLOAD SUCCESSFUL TO FSS");
            }  else {
                errorHandler(docFields, gson, log);
            }





        }
        for (ExternalData data : externalData){
            data.setFilename("u" + auth.getUserId() + fileStaged.get(data.getFilename()));
        }



        System.out.println("Write new data to CSV");





        // Format External Data into String Arrays assist with CSV Input
        List<String[]> csvData = toStringArray(externalData, headers, headerData);

        // Write data to CSV
        File file = new File(dirFilePath + "/data.csv");

        try {

            BufferedWriter writer = Files.newBufferedWriter(Paths.get(dirFilePath + "/data.csv"), StandardCharsets.UTF_8);

            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.RFC4180);

            for (String[] row: csvData){
                printer.printRecord(row);
            }
            writer.close();
            printer.close();



        } catch (IOException e) {
            e.printStackTrace();
        }



        System.out.println("Create Docs in Vault");


//      Create Multiple Docs endpoint
        HttpResponse<String> createMultiDocResponse = Unirest.post("/objects/documents/batch")
                .header("Authorization", auth.getSessionId())
                .header("Accept", "application/json")
                .header("Content-Type", "text/csv")
                .body(new FileInputStream("/Users/abbassiddiqui-mbpr16/Documents/Interface/data.csv"))
                .asString();


        System.out.println(createMultiDocResponse.getBody());


        // Initiate Doc Workflow

        DocIds docIds = gson.fromJson(createMultiDocResponse.getBody() , DocIds.class);

        List<String> ids = new ArrayList<>();

        String document__sys = "";
        for (Object id : docIds.getData()) {
            Doc doc = gson.fromJson(id.toString(), Doc.class);

            ids.add(Integer.toString(doc.getDocId()));
            document__sys += doc.getDocId() + ",";
        }

        document__sys = document__sys.substring(0, document__sys.length() - 1);


        HttpResponse<String> startWF = Unirest.post("/objects/documents/actions/Objectworkflow.external_approved_quality_check__c")
                .header("Authorization", auth.getSessionId())
                .header("Accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("documents__sys", document__sys)
                .field("description__sys", "Externally Approved Documents")
                .asString();



    }

    public static void errorHandler(Error error, Gson gson, Logger log){
        Errors errors = gson.fromJson(error.getErrors()[0].toString(), Errors.class);

        log.log(Level.INFO, errors.getType() + ": " + errors.getMessage());
        System.exit(1);
    }


    private static List<String[]> toStringArray(List<ExternalData> externalData, List<String> headers, Map<String, String> headerData) {
        List<String[]> records = new ArrayList<String[]>();

        String[] top = new String[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            if (i == 0){
                top[i] = "file";
            } else {
                top[i] = headerData.get(headers.get(i));
            }

        }
        // adding header record
        records.add(top);

        Iterator<ExternalData> it = externalData.iterator();
        while (it.hasNext()) {
            ExternalData data = it.next();

            records.add(new String[] { data.getFilename(), data.getName(), data.getDoctype(), data.getSubtype(), data.getLifecycle(),
                    data.getExternalId(), data.getApprovedDate(), data.getTrainingImpact().toString(),
                    data.getCountry(), data.getDepartment(), data.getFacility()});
        }
        return records;
    }


}
