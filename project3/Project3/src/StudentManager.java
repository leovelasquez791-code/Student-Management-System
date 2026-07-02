import java.util.*;
import java.util.stream.Collectors;

/** Student Name: Leonardo Velasquez
 * Date: [12/10/2025]
 * Program Name: Student Management System - JavaFX UI
 * Class Name: StudentManager:
 * Description: holds roster and provides CRUD/search/validation utilities for GUI to call.
 */

public class StudentManager {
    private final ArrayList<Student> roster = new ArrayList<>();

    public StudentManager() { }

    // Return copy to avoid external modification
    public ArrayList<Student> getAllStudents() {
        return new ArrayList<>(roster);
    }

    public int getTotalStudents() {
        return roster.size();
    }

    // Add undergraduate
    public boolean addStudent(Student s) {
        if (s == null) return false;
        if (hasDuplicateId(s.getStudentID())) return false;
        roster.add(s);
        return true;
    }

    // Convenience for creating and adding
    public boolean addUndergraduate(String name, int id, String major, double gpa) {
        if (!validateId(id) || !validateGpa(gpa) || isNullOrEmpty(name) || isNullOrEmpty(major)) return false;
        return addStudent(new Student(name, id, major, gpa));
    }

    public boolean addGraduate(String name, int id, String major, double gpa, String thesis, String advisor) {
        if (!validateId(id) || !validateGpa(gpa) || isNullOrEmpty(name) || isNullOrEmpty(major)) return false;
        if (isNullOrEmpty(thesis) || isNullOrEmpty(advisor)) return false;
        return addStudent(new GraduateStudent(name, id, major, gpa, thesis, advisor));
    }

    public boolean deleteById(int id) {
        return roster.removeIf(s -> s.getStudentID() == id);
    }

    public Student findById(int id) {
        for (Student s : roster) if (s.getStudentID() == id) return s;
        return null;
    }

    public List<Student> searchByName(String namePart) {
        if (namePart == null) return Collections.emptyList();
        String q = namePart.trim().toLowerCase();
        return roster.stream()
                .filter(s -> s.getName() != null && s.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Student> searchByMajor(String majorPart) {
        if (majorPart == null) return Collections.emptyList();
        String q = majorPart.trim().toLowerCase();
        return roster.stream()
                .filter(s -> s.getMajor() != null && s.getMajor().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public boolean hasDuplicateId(int id) {
        return roster.stream().anyMatch(s -> s.getStudentID() == id);
    }

    public void clearAll() {
        roster.clear();
    }

    // Validation helpers
    public static boolean validateId(int id) {
        return id >= 10000 && id <= 99999;
    }

    public static boolean validateGpa(double gpa) {
        return gpa >= 0.0 && gpa <= 4.0;
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Replace current roster entirely (useful after load)
    public void setRoster(Collection<Student> students) {
        roster.clear();
        if (students != null) roster.addAll(students);
    }

    // Return students sorted by GPA descending (helper for reports)
    public List<Student> getStudentsSortedByGpaDesc() {
        return roster.stream()
                .sorted(Comparator.comparingDouble(Student::getGpa).reversed())
                .collect(Collectors.toList());
    }
}