/**
 * Student Name: Leonardo Velasquez
 * Date: [12/10/2025]
 * Program Name: Student Management System - JavaFX UI
 * Class Name: GraduateStudent
 * Description: Graduate student extends Student with thesisTopic and advisor.
 */
public class GraduateStudent extends Student {
    private String thesisTopic;
    private String advisor;

    public GraduateStudent(String name, int studentID, String major, double gpa, String thesisTopic, String advisor) {
        super(name, studentID, major, gpa);
        this.thesisTopic = thesisTopic;
        this.advisor = advisor;
    }

    public String getThesisTopic() { return thesisTopic; }
    public void setThesisTopic(String thesisTopic) { this.thesisTopic = thesisTopic; }

    public String getAdvisor() { return advisor; }
    public void setAdvisor(String advisor) { this.advisor = advisor; }

    @Override
    public String getStudentType() { return "Graduate"; }

    // Record format: G|id|name|major|gpa|thesis|advisor
    @Override
    public String toRecord() {
        return String.format("G|%d|%s|%s|%.2f|%s|%s",
                getStudentID(),
                escape(getName()),
                escape(getMajor()),
                getGpa(),
                escape(thesisTopic),
                escape(advisor));
    }

    @Override
    public String toString() {
        return String.format("%d - %s - %s - GPA: %.2f - Thesis: %s - Advisor: %s",
                getStudentID(), getName(), getMajor(), getGpa(), thesisTopic, advisor);
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("|", "/");
    }
}