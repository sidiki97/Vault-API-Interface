package Models;

public class ExternalData {

    private String filename;
    private String externalId;
    private String approvedDate;
    private String name;
    private String doctype;
    private int majorNumber;
    private int minorNumber;
    private Boolean trainingImpact;

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

    public int getMajorNumber() {
        return majorNumber;
    }

    public void setMajorNumber(int majorNumber) {
        this.majorNumber = majorNumber;
    }

    public int getMinorNumber() {
        return minorNumber;
    }

    public void setMinorNumber(int minorNumber) {
        this.minorNumber = minorNumber;
    }

    public Boolean getTrainingImpact() {
        return trainingImpact;
    }

    public void setTrainingImpact(Boolean trainingImpact) {
        this.trainingImpact = trainingImpact;
    }
}
