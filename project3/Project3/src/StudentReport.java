import java.util.*;
import java.util.stream.Collectors;
/**
 *  Student Name: Leonardo Velasquez
 * Date: [12/10/2025]
 * Program Name: Student Management System - JavaFX UI
 * Class Name: StudentReport 
 * Description: generates text reports (returns Strings) for GUI display.
 */

public class StudentReport {

    public static String generateCompleteReport(Collection<Student> roster) {
        StringBuilder sb = new StringBuilder();
        sb.append("COMPLETE STUDENT REPORT\n");
        sb.append("========================\n");
        if (roster == null || roster.isEmpty()) {
            sb.append("No students found.\n");
            return sb.toString();
        }
        for (Student s : roster) {
            sb.append(formatLine(s)).append("\n");
        }
        return sb.toString();
    }

    public static String generateGPAReport(Collection<Student> roster) {
        StringBuilder sb = new StringBuilder();
        sb.append("GPA REPORT (Highest to Lowest)\n");
        sb.append("==============================\n");
        if (roster == null || roster.isEmpty()) {
            sb.append("No students found.\n");
            return sb.toString();
        }
        roster.stream()
                .sorted(Comparator.comparingDouble(Student::getGpa).reversed())
                .forEach(s -> sb.append(formatLine(s)).append("\n"));
        return sb.toString();
    }

    public static String generateStatistics(Collection<Student> roster) {
        StringBuilder sb = new StringBuilder();
        sb.append("STATISTICS\n");
        sb.append("==========\n");
        if (roster == null || roster.isEmpty()) {
            sb.append("No students.\n");
            return sb.toString();
        }
        int total = roster.size();
        long grads = roster.stream().filter(s -> s instanceof GraduateStudent).count();
        long undergrads = total - grads;
        double avgGpa = roster.stream().mapToDouble(Student::getGpa).average().orElse(0.0);

        sb.append("Total students: ").append(total).append("\n");
        sb.append("Undergraduates: ").append(undergrads).append("\n");
        sb.append("Graduates: ").append(grads).append("\n");
        sb.append(String.format("Average GPA: %.2f\n", avgGpa));

        // Major distribution
        Map<String, Long> majorCounts = roster.stream()
                .collect(Collectors.groupingBy(s -> s.getMajor() == null ? "Unknown" : s.getMajor(), Collectors.counting()));
        sb.append("\nMajor distribution:\n");
        majorCounts.forEach((major, count) -> sb.append(String.format("%s : %d\n", major, count)));

        return sb.toString();
    }

    private static String formatLine(Student s) {
        if (s instanceof GraduateStudent) {
            GraduateStudent g = (GraduateStudent) s;
            return String.format("%d | %s | %s | GPA: %.2f | Thesis: %s | Advisor: %s",
                    g.getStudentID(), g.getName(), g.getMajor(), g.getGpa(),
                    safe(g.getThesisTopic()), safe(g.getAdvisor()));
        } else {
            return String.format("%d | %s | %s | GPA: %.2f",
                    s.getStudentID(), s.getName(), s.getMajor(), s.getGpa());
        }
    }

    private static String safe(String s) { return s == null ? "" : s; }
}