/**
 * Student Name: [Leonardo Velasquez]
 * Date: [12/10/2025]
 * Program Name: Student Management System - JavaFX UI
 * Class Name: Student
 * Description: Base class for undergraduate students. Contains id, name, major and gpa.
 */

public class Student {
	
	private String name;
	private int studentID;
	private String major;
	private double gpa;

	public Student(String name, int studentID, String major, double gpa) {
		this.name = name;
	    this.studentID = studentID;
	    this.major = major;
	    this.gpa = gpa;
	}

	    // Getters / setters (TableView uses these)
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public int getStudentID() { return studentID; }
	public void setStudentID(int studentID) { this.studentID = studentID; }
	
	public String getMajor() { return major; }
	public void setMajor(String major) { this.major = major; }
	
	public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    // Type for table display
    public String getStudentType() { return "Undergraduate"; }

    // Record format used for file I/O: S|id|name|major|gpa
    public String toRecord() {
    	return String.format("S|%d|%s|%s|%.2f", studentID, escape(name), escape(major), gpa);
    }

	    // Useful toString for debugging / report generation
    @Override
    public String toString() {
    	return String.format("%d - %s - %s - GPA: %.2f", studentID, name, major, gpa);
	}

	    // Simple escaping for pipes (keep file format robust)
    private String escape(String s) {
    	return s == null ? "" : s.replace("|", "/"); // replace pipes if present
	}
	

}
