import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Student Name: Leonardo Velasquez
 * Date: [12/10/2025]
 * Program Name: Student Management System - JavaFX UI
 * Class Name: StudentManagementUI
 * Description: JavaFX GUI for the Student Management System.
 *              Provides tabs for CRUD operations, search/filter,
 *              sorting, reports display, and file operations.
 *              Uses Student, GraduateStudent, StudentIO, and StudentReport classes.
 */
public class StudentManagementUI extends Application {
	
	// Constants for repeated string literals (Coding Standards)
	private static final String TYPE_UNDERGRAD = "Undergraduate";
	private static final String TYPE_GRAD = "Graduate";

    private static final String SEARCH_ID = "ID";
    private static final String SEARCH_NAME = "Name";
    private static final String SEARCH_MAJOR = "Major";

    private static final String DEFAULT_STUDENTS_FILE = "students.txt";
    private static final String DEFAULT_REPORT_FILE = "student_report.txt";
	

    private StudentManager manager = new StudentManager();

    // TableView for CRUD tab
    private TableView<Student> table = new TableView<>();

    // Status bar labels
    private Label statusMsg = new Label("Ready");
    private Label totalCount = new Label("Total Students: 0");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Student Management System");

        BorderPane root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(buildTabs());
        root.setBottom(buildStatusBar());

        Scene scene = new Scene(root, 1100, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // -------------------- HEADER ------------------------------
    private VBox buildHeader() {
        VBox header = new VBox(5);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: linear-gradient(to right, #4b6cb7, #182848);");

        Label title = new Label("STUDENT MANAGEMENT SYSTEM");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: white; -fx-font-weight: bold;");

        Label subtitle = new Label("Manage Undergraduate and Graduate Student Records");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        Label date = new Label(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy  HH:mm")));
        date.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        header.getChildren().addAll(title, subtitle, date);

        return header;
    }

    // -------------------- TABS ------------------------------
    private TabPane buildTabs() {
        TabPane tabs = new TabPane();
        tabs.getTabs().add(buildCRUDTab());
        tabs.getTabs().add(buildSearchTab());
        tabs.getTabs().add(buildReportTab());
        tabs.getTabs().add(buildFileOpTab());
        return tabs;
    }

    // -------------------- TAB 1: CRUD -------------------------
    private Tab buildCRUDTab() {
        Tab tab = new Tab("CRUD Operations");
        tab.setClosable(false);

        BorderPane layout = new BorderPane();

        layout.setLeft(buildCRUDForm());
        layout.setCenter(buildStudentTable());

        tab.setContent(layout);
        return tab;
    }

    private VBox buildCRUDForm() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        TextField idField = new TextField();
        idField.setPromptText("Student ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField majorField = new TextField();
        majorField.setPromptText("Major");

        TextField gpaField = new TextField();
        gpaField.setPromptText("GPA (0.0 - 4.0)");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Undergraduate", "Graduate");
        typeBox.setValue("Undergraduate");

        TextField thesisField = new TextField();
        thesisField.setPromptText("Thesis Topic");
        thesisField.setDisable(true);

        TextField advisorField = new TextField();
        advisorField.setPromptText("Advisor");
        advisorField.setDisable(true);

        // Enable graduate fields only if "Graduate" is selected
        typeBox.setOnAction(e -> {
            boolean isGrad = typeBox.getValue().equals("Graduate");
            thesisField.setDisable(!isGrad);
            advisorField.setDisable(!isGrad);
        });

        Button addBtn = new Button("Add Student");
        addBtn.setOnAction(e -> {
            try {
            	int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                String major = majorField.getText().trim();
                double gpa = Double.parseDouble(gpaField.getText().trim());

                if (!StudentManager.validateId(id)) {
                    updateStatus("Error: ID must be between 10000 and 99999.");
                    return;
                }

                if (manager.hasDuplicateId(id)) {
                    updateStatus("Error: Duplicate student ID.");
                    return;
                }

                if (!StudentManager.validateGpa(gpa)) {
                    updateStatus("Error: GPA must be between 0.0 and 4.0.");
                    return;
                }

                if (name.isEmpty()) {
                    updateStatus("Error: Name cannot be empty.");
                    return;
                }

                if (major.isEmpty()) {
                    updateStatus("Error: Major cannot be empty.");
                    return;
                }

                boolean success;

                if (TYPE_UNDERGRAD.equals(typeBox.getValue())) {
                    success = manager.addUndergraduate(name, id, major, gpa);
                } else {
                    String thesis = thesisField.getText().trim();
                    String advisor = advisorField.getText().trim();

                    if (thesis.isEmpty()) {
                        updateStatus("Error: Thesis topic required for graduate students.");
                        return;
                    }

                    if (advisor.isEmpty()) {
                        updateStatus("Error: Advisor required for graduate students.");
                        return;
                    }

                    success = manager.addGraduate(name, id, major, gpa, thesis, advisor);
                }

                if (success) {
                    updateTable();
                    updateStatus("Student added successfully!");
                }

            } catch (NumberFormatException ex) {
                updateStatus("Error: ID and GPA must be numeric values.");
            }
        });

        TextField delField = new TextField();
        delField.setPromptText("ID to Delete");
        
        //

        Button delBtn = new Button("Delete");
        delBtn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(delField.getText().trim());
                boolean removed = manager.deleteById(id);
                if (removed) {
                    updateStatus("Student deleted.");
                } else {
                    updateStatus("Student ID not found.");
                }
                updateTable();
            } catch (Exception ex) {
                updateStatus("Invalid ID.");
            }
        });

        Button clearBtn = new Button("Clear All");
        clearBtn.setOnAction(e -> {
            manager.clearAll();
            updateTable();
            updateStatus("All students cleared.");
        });

        box.getChildren().addAll(
                new Label("Add Student"),
                idField, nameField, majorField, gpaField,
                typeBox, thesisField, advisorField, addBtn,
                new Separator(),
                new Label("Delete Student"),
                delField, delBtn,
                new Separator(),
                clearBtn
        );

        return box;
    }

    // -------------------- TABLEVIEW --------------------------
    private TableView<Student> buildStudentTable() {
        TableColumn<Student, Integer> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        colID.setPrefWidth(90);

        TableColumn<Student, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(160);

        TableColumn<Student, String> colMajor = new TableColumn<>("Major");
        colMajor.setCellValueFactory(new PropertyValueFactory<>("major"));
        colMajor.setPrefWidth(140);

        TableColumn<Student, Double> colGpa = new TableColumn<>("GPA");
        colGpa.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        colGpa.setPrefWidth(60);

        TableColumn<Student, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("studentType"));
        colType.setPrefWidth(110);

        TableColumn<Student, String> colThesis = new TableColumn<>("Thesis");
        colThesis.setCellValueFactory(new PropertyValueFactory<>("thesisTopic"));
        colThesis.setPrefWidth(200);

        TableColumn<Student, String> colAdvisor = new TableColumn<>("Advisor");
        colAdvisor.setCellValueFactory(new PropertyValueFactory<>("advisor"));
        colAdvisor.setPrefWidth(160);

        table.getColumns().addAll(colID, colName, colMajor, colGpa, colType, colThesis, colAdvisor);

        updateTable();

        return table;
    }

    private void updateTable() {
        table.getItems().setAll(manager.getAllStudents());
        totalCount.setText("Total Students: " + manager.getTotalStudents());
    }

    // -------------------- TAB 2: SEARCH -----------------------
    private Tab buildSearchTab() {
        Tab tab = new Tab("Search Students");
        tab.setClosable(false);

        BorderPane pane = new BorderPane();

        VBox controls = new VBox(10);
        controls.setPadding(new Insets(10));

        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("ID", "Name", "Major");
        searchType.setValue("ID");

        TextField input = new TextField();
        input.setPromptText("Search Value");

        TableView<Student> searchTable = new TableView<>();
        searchTable.getColumns().addAll(table.getColumns());

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> {
            String type = searchType.getValue();
            String value = input.getText().trim();

            if (value.isEmpty()) {
                updateStatus("Enter search text.");
                return;
            }

            List<Student> results;

            switch (type) {
                case "ID":
                    try {
                        Student s = manager.findById(Integer.parseInt(value));
                        results = (s == null) ? List.of() : List.of(s);
                    } catch (Exception ex) {
                        results = List.of();
                    }
                    break;
                case "Name":
                    results = manager.searchByName(value);
                    break;
                default:
                    results = manager.searchByMajor(value);
            }

            searchTable.getItems().setAll(results);
            updateStatus("Found " + results.size() + " result(s).");
        });

        controls.getChildren().addAll(new Label("Search By"), searchType, input, searchBtn);
        pane.setLeft(controls);
        pane.setCenter(searchTable);

        tab.setContent(pane);
        return tab;
    }

    // -------------------- TAB 3: REPORTS -----------------------
    private Tab buildReportTab() {
        Tab tab = new Tab("Reports");
        tab.setClosable(false);

        BorderPane pane = new BorderPane();

        VBox buttons = new VBox(10);
        buttons.setPadding(new Insets(10));

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);

        Button fullBtn = new Button("Complete Report");
        fullBtn.setOnAction(e ->
                reportArea.setText(StudentReport.generateCompleteReport(manager.getAllStudents())));

        Button gpaBtn = new Button("GPA Report");
        gpaBtn.setOnAction(e ->
                reportArea.setText(StudentReport.generateGPAReport(manager.getAllStudents())));

        Button statsBtn = new Button("Statistics");
        statsBtn.setOnAction(e ->
                reportArea.setText(StudentReport.generateStatistics(manager.getAllStudents())));

        buttons.getChildren().addAll(fullBtn, gpaBtn, statsBtn);

        pane.setLeft(buttons);
        pane.setCenter(reportArea);

        tab.setContent(pane);

        return tab;
    }

    // -------------------- TAB 4: FILE OPS ----------------------
    private Tab buildFileOpTab() {
        Tab tab = new Tab("File Operations");
        tab.setClosable(false);

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        TextArea info = new TextArea("""
                File Format:
                Undergraduate: S|id|name|major|gpa
                Graduate: G|id|name|major|gpa|thesis|advisor
                
                Default filenames:
                students.txt
                student_report.txt
                """);
        info.setEditable(false);

        Button saveBtn = new Button("Save Students");
        saveBtn.setOnAction(e -> {
            try {
                FileChooser fc = new FileChooser();
                fc.setInitialFileName("students.txt");
                File file = fc.showSaveDialog(null);
                if (file != null) {
                    StudentIO.saveToFile(manager.getAllStudents(), file);
                    updateStatus("Saved successfully.");
                }
            } catch (Exception ex) {
                updateStatus("Error saving file.");
            }
        });

        Button loadBtn = new Button("Load Students");
        loadBtn.setOnAction(e -> {
            try {
                FileChooser fc = new FileChooser();
                File file = fc.showOpenDialog(null);
                if (file != null) {
                    manager.setRoster(StudentIO.loadFromFile(file));
                    updateTable();
                    updateStatus("Loaded successfully.");
                }
            } catch (Exception ex) {
                updateStatus("Error loading file.");
            }
        });

        Button exportBtn = new Button("Export Report");
        exportBtn.setOnAction(e -> {
            try {
                FileChooser fc = new FileChooser();
                fc.setInitialFileName("student_report.txt");
                File file = fc.showSaveDialog(null);
                if (file != null) {
                    String text = StudentReport.generateCompleteReport(manager.getAllStudents());
                    java.nio.file.Files.writeString(file.toPath(), text);
                    updateStatus("Report exported.");
                }
            } catch (Exception ex) {
                updateStatus("Error exporting report.");
            }
        });

        box.getChildren().addAll(saveBtn, loadBtn, exportBtn, new Separator(), info);
        tab.setContent(box);
        return tab;
    }

    // -------------------- STATUS BAR ----------------------------
    private HBox buildStatusBar() {
        HBox bar = new HBox(20);
        bar.setPadding(new Insets(5));
        bar.setStyle("-fx-background-color: #eeeeee; -fx-border-color: #cccccc;");

        bar.getChildren().addAll(new Label("Status:"), statusMsg, new Separator(), totalCount);
        bar.setAlignment(Pos.CENTER_LEFT);

        return bar;
    }

    private void updateStatus(String msg) {
        statusMsg.setText(msg);
    }

    public static void main(String[] args) {
        launch(args);
    }
}