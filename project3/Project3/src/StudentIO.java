import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Student Name: [Leonardo Velasquez]
 * Date: [12/10/2025]
 * Program Name: Student Management System - JavaFX UI
 * Class Name: StudentIO
 * Description: StudentIO - saves and loads roster to/from a text file.
 * Record formats:
 * S|id|name|major|gpa
 * G|id|name|major|gpa|thesis|advisor
 */

public class StudentIO {
    public static void saveToFile(Collection<Student> students, File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            if (students != null) {
                for (Student s : students) {
                    pw.println(s.toRecord());
                }
            }
        }
    }

    public static ArrayList<Student> loadFromFile(File file) throws IOException {
        ArrayList<Student> list = new ArrayList<>();
        if (file == null || !file.exists()) return list;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(Pattern.quote("|"), -1);
                String type = parts[0];
                try {
                    if ("S".equalsIgnoreCase(type)) {
                        // Expect at least 5 parts: S|id|name|major|gpa
                        if (parts.length < 5) continue;
                        int id = Integer.parseInt(parts[1].trim());
                        String name = unescape(parts[2]);
                        String major = unescape(parts[3]);
                        double gpa = Double.parseDouble(parts[4]);
                        list.add(new Student(name, id, major, gpa));
                    } else if ("G".equalsIgnoreCase(type)) {
                        // Expect at least 7 parts: G|id|name|major|gpa|thesis|advisor
                        if (parts.length < 7) continue;
                        int id = Integer.parseInt(parts[1].trim());
                        String name = unescape(parts[2]);
                        String major = unescape(parts[3]);
                        double gpa = Double.parseDouble(parts[4]);
                        String thesis = unescape(parts[5]);
                        String advisor = unescape(parts[6]);
                        list.add(new GraduateStudent(name, id, major, gpa, thesis, advisor));
                    }
                } catch (NumberFormatException ex) {
                    // skip malformed line but continue loading others
                    continue;
                }
            }
        }
        return list;
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("/", "|");
    }
}