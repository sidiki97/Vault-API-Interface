package Models;

public class ExternalData {

    private String filename;
    private String externalId;
    private String approvedDate;
    private String name;
    private String doctype;
    private String majorNumber;
    private String minorNumber;
    private String trainingImpact;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getMajorNumber() {
        return majorNumber;
    }

    public void setMajorNumber(String majorNumber) {
        this.majorNumber = majorNumber;
    }

    public String getMinorNumber() {
        return minorNumber;
    }

    public void setMinorNumber(String minorNumber) {
        this.minorNumber = minorNumber;
    }

    public String getTrainingImpact() {
        return trainingImpact;
    }

    public void setTrainingImpact(String trainingImpact) {
        this.trainingImpact = trainingImpact;
    }
}
