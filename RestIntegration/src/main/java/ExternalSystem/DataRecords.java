package ExternalSystem;


import Models.ExternalData;

import java.util.ArrayList;

public class DataRecords {
//    private static String[] filenames = {"Test_1.docx", "Test_2.docx", "Test_3.docx", "Test_4.docx"};
//    private static String[] externalIds = {"DOC-FA", "DOC-FB", "DOC-FC", "DOC_FD"};
//    private static String[] approvedDates = {"2022-02-02", "2022-03-03", "2022-04-04", "2022-05-05"};
//    private static String[] names = {"Doc1", "Doc2", "Doc3", "Doc4"};
//    private static String[] doctypes = {"Directive", "Form", "Policy", "Guidance"};
////    private static String[] majorNumbers = {"1", "2", "3", "4"};
////    private static String[] minorNumbers = {"0", "0", "3", "1"};
//    private static String[] traingImpact = {"false", "true","false", "true"};
//    private static String[] countries = {"unitedStates", "germany", "japan", "canada"}; //tolowercase
//    private static String[] departments = {"00D000000000201", "00D000000000201", "00D000000000201", "00D000000000201"}; //Veeva Department A
//    private static String[] facilities = {"00F000000000101", "00F000000000101", "00F000000000101", "00F000000000101"}; //VeevaFacilityA




    public static ArrayList<ExternalData> data() {
        ArrayList<ExternalData> externalData = new ArrayList<>();
        for (int i = 0; i < 4; i++){
//            externalData.add(new ExternalData());
//            externalData.get(i).setFilename(filenames[i]);
//            externalData.get(i).setExternalId(externalIds[i]);
//            externalData.get(i).setApprovedDate(approvedDates[i]);
//            externalData.get(i).setName(names[i]);
//            externalData.get(i).setDoctype(doctypes[i]);
////            externalData.get(i).setMajorNumber(majorNumbers[i]);
////            externalData.get(i).setMinorNumber(minorNumbers[i]);
//            externalData.get(i).setTrainingImpact(traingImpact[i]);
//            externalData.get(i).setCountry(countries[i]);
//            externalData.get(i).setDepartment(departments[i]);
//            externalData.get(i).setFacility(facilities[i]);
        }
        return externalData;
    }

    public static void main(String[] args) {
//        for (int i = 0; i < 4; i++){
//            System.out.println(countries[i]);
//        }
    }





}

