package coolstuf;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class Controller {
    Connection dbc;
    PreparedStatement queryClassnamesForTeach;

    @FXML ListView<Teacher> teachersListView;
    @FXML TextField idField;
    @FXML TextField fnField;
    @FXML TextField lnField;
    @FXML TextField emField;

    @FXML ListView<String> teacherClassesListView;

    @FXML
    public void initialize() {
        dbc = getDbConnection();
        initTeacherClassesQueryStmt();

        var ts = getTeachers();
        fillTeacherListview(ts);
        bindViewsToSelectedTeacher();
    }

    public void closeDb() {
        System.out.println("closing db stuff");
        try {
            queryClassnamesForTeach.close();
            dbc.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillTeacherListview(List<Teacher> teachers) {
        teachersListView.getItems().addAll(teachers);
    }

    private void bindViewsToSelectedTeacher() {
        teachersListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Teacher>) change -> {
            change.next();
            Teacher selected = change.getAddedSubList().get(0);
            idField.setText(Integer.toString(selected.getId()));
            fnField.setText(selected.getFirstName());
            lnField.setText(selected.getLastName());
            emField.setText(selected.getEmail());

            try {
                queryClassnamesForTeach.setInt(1, selected.getId());
                ResultSet r = queryClassnamesForTeach.executeQuery();
                List<String> classnames = new ArrayList<>();
                while(r.next()) {
                    classnames.add(r.getString("name"));
                }
                teacherClassesListView.getItems().setAll(classnames);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void initTeacherClassesQueryStmt() {
        String query = "select c.name from class c join class_teacher ct on c.id = ct.class_id where teacher_id = ?";
        try {
            queryClassnamesForTeach = dbc.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Teacher> getTeachers() {
        try {
            Statement s = dbc.createStatement();
            ResultSet r = s.executeQuery("select * from teacher");

            List<Teacher> teachers = new ArrayList<>();

            while (r.next()) {
                int    id = r.getInt("id");
                String fn = r.getString("first_name");
                String ln = r.getString("last_name");
                String em = r.getString("email");
                teachers.add(new Teacher(id, fn, ln, em));
            }

            s.close();
            return teachers;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Connection getDbConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/cr6_school",
                    "root",
                    "");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
