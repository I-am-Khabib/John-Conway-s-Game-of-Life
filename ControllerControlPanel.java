package main.java.com.wdhays.gol;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import java.sql.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import static main.java.com.wdhays.gol.GameSpeed.*;

public class ControllerControlPanel implements Initializable {

    private GameOfLife gameOfLife;

    public ControllerControlPanel(GameOfLife gameOfLife) {
        this.gameOfLife = gameOfLife;
    }

    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button NextButton;
    @FXML
    private Slider zoomSlider;
    @FXML
    private Slider speedSlider;
    @FXML
    private Button saveButton;
    @FXML
    private Button saveDBButton;
    @FXML
    private Button DeleteDBButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button loadButton;
    @FXML
    private Button loadDBButton;
    @FXML
    private Button viewDBButton;
    @FXML
    private Button randomButton;
    @FXML
    private TextField randomTextField;
    @FXML
    private ComboBox<String> patternsCombo;
    @FXML
    private ImageView patternImageView;
    @FXML
    private Button addPatternButton;
    @FXML
    private CheckBox useColorsCheckBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Set up speedSlider options and property change listener.
        initializeSpeedSlider();
        initializeZoomSlider();

        zoomSlider.valueProperty().addListener(getZoomSliderChangeListener());
        speedSlider.valueProperty().addListener(getSliderChangeListener());
        //Set up action listeners for the play, pause, and stop buttons.
        playButton.setOnAction(e -> playBtnOnAction());
        pauseButton.setOnAction(e -> pauseBtnOnAction());
        clearButton.setOnAction(e -> clearBtnOnAction());
        NextButton.setOnAction(e->nextBtnOnAction());
        //Set up action listeners for the save and load buttons.
        saveButton.setOnAction(this::saveBtnOnAction);
        saveDBButton.setOnAction(this::saveDBBtnOnAction);
        DeleteDBButton.setOnAction(this::deleteDBBtnOnAction);
        deleteButton.setOnAction(this::deleteBtnOnAction);
        loadButton.setOnAction(this::loadBtnOnAction);
        loadDBButton.setOnAction(this::loadDBBtnOnAction);
        viewDBButton.setOnAction(this::ViewDBBtnOnAction);
        //Set up the rules combo box to be populated by the RuleSet enum.
        //Set up the action listeners for the random button.
        randomButton.setOnAction(e -> randomBtnOnAction());
        //Set up the patterns combo box to be populated by the Pattern enum.
        patternsCombo.getItems().setAll(Pattern.getPatternNames());
        patternsCombo.getSelectionModel().selectFirst();
        patternsCombo.valueProperty().addListener(getPatternsComboChangeListener());
        //Set up the pattern image view with a default image.
        loadPatternPreviewImage();
        //Set up add pattern button.
        addPatternButton.setOnAction(e -> addPatternOnAction());
        //Set up the use cell age colors checkbox.
        useColorsCheckBox.selectedProperty().addListener(useCellAgeColorsChangeListener());
    }


    private ChangeListener<Boolean> useCellAgeColorsChangeListener() {
        return (observable, oldValue, newValue) -> {
            System.out.println("The draw colors checkbox value has changed to: " + newValue);
            gameOfLife.setUseCellAge(newValue);
            //Trigger redraw.
            gameOfLife.setNeedsRedraw(true);
        };
    }



    private ChangeListener<String> getPatternsComboChangeListener() {
        //Update the pattern image view to the currently selected patterns thumbnail.
        return (observable, oldValue, newValue) -> {
            System.out.println("The pattern combo value was changed!");
            System.out.println("The new value is " + patternsCombo.getValue());
            loadPatternPreviewImage();
        };
    }

    private void loadPatternPreviewImage() {
        try {
            Image previewImage = new Image(getClass().getResource("patterns/" + patternsCombo.getValue() + ".png").toString(), true);
            patternImageView.setImage(previewImage);
        } catch (NullPointerException | IllegalArgumentException e) {
            System.out.println("Failed to load pattern preview image!");
        }
    }

    private ChangeListener<Number> getZoomSliderChangeListener() {
        return(observable,oldValue,newValue)->{
            zoomSlider.setValue(newValue.intValue());
            if(newValue.intValue() != oldValue.intValue()) {
                ZoomSliderChangeAction();
            }
        };
    }


    private ChangeListener<Number> getSliderChangeListener() {
        return (observable, oldValue, newValue) -> {
            //This bit keeps to property listener from firing unless the slider in on a tick mark.
            //The slider value is a double, but we are only interested in the whole numbers at ticks.
            speedSlider.setValue(newValue.intValue());
            if(newValue.intValue() != oldValue.intValue()) {
                speedSliderChangeAction();
            }
        };
    }

    private void addPatternOnAction() {
        try {
            if (gameOfLife.loadGameBoardFromPatternFile(patternsCombo.getValue())) {
                System.out.println("Pattern successfully added!");
                //Trigger a redraw.
                gameOfLife.setNeedsRedraw(true);
                gameOfLife.setGeneration(0);
            } else {
                System.out.println("Pattern load failed!");
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void randomBtnOnAction() {
        System.out.println("The random button was pressed!");
        //Force the live chance value to be in our range.
        double randomValue;
        try {
            randomValue = Double.valueOf(randomTextField.getText());
            if(randomValue > 1) {
                randomValue = 1.0;
                randomTextField.setText(Double.toString(randomValue));
            } else if (randomValue < 0) {
                randomValue = 0.0;
                randomTextField.setText(Double.toString(randomValue));
            }
        } catch(NumberFormatException e) {
            randomValue = 0.1;
            randomTextField.setText(Double.toString(randomValue));
        }
        //Update the game board.
        System.out.println("The random text field value is " + randomValue);
        gameOfLife.generateRandomGrid(randomValue);
    }

    private void ViewDBBtnOnAction(ActionEvent actionEvent) {

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bilal","root","Itsme7");
             Statement stmt = con.createStatement();) {
            String SQL = "select* from grid";
            ResultSet rs = stmt.executeQuery(SQL);

            // Iterate through the data in the result set and display it.
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
            stmt.close();
            con.close();
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }

}

    private void saveDBBtnOnAction(ActionEvent actionEvent)  {
        Scanner myObj = new Scanner(System.in);
        String FileName;
        File mydbfile=new File("db.txt");
        try {
            gameOfLife.saveGameBoardToFile(mydbfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Enter username and press Enter
        System.out.println("Enter FileName");
        FileName = myObj.nextLine();

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bilal","root","Itsme7");
             PreparedStatement stmt = con.prepareStatement("insert into grid values(?,?)");) {
            stmt.setString(1,FileName);
            stmt.setString(2, gameOfLife.buildGridCSVString());

            int rs = stmt.executeUpdate();

            // Iterate through the data in the result set and display it.
            if(rs==0)
                System.out.println("Not Inserted\n");
            else
                System.out.println("Inserted\n");

            stmt.close();
            con.close();
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveBtnOnAction(ActionEvent event) {

        System.out.println("Save button was pressed!");
        Button eventSource = (Button) event.getSource();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save GOL File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GOL Files", "*.gol"));
        File saveFile = fileChooser.showSaveDialog(eventSource.getScene().getWindow());

        if (saveFile != null) {
            System.out.println("Attempting save to: " + saveFile);
            try {
                if (gameOfLife.saveGameBoardToFile(saveFile)) {
                    System.out.println("Save successful!");
                } else {
                    System.out.println("Save failed!");
                    System.out.println("The file could not be created or was not writable!");
                }
            } catch (IOException e) {
                System.out.println("There was an error saving the file!");
                e.printStackTrace();
            }
        }
    }

    private void deleteDBBtnOnAction(ActionEvent actionEvent) {
        Scanner myObj = new Scanner(System.in);
        String FileName;

        // Enter username and press Enter
        System.out.println("Enter FileName");
        FileName = myObj.nextLine();
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bilal","root","Itsme7");
             PreparedStatement stmt = con.prepareStatement("delete from grid where name=?");) {
            stmt.setString(1,FileName);
            int rs = stmt.executeUpdate();

            // Iterate through the data in the result set and display it.
            if(rs==0)
                System.out.println("Not Deleted\n");
            else
                System.out.println("Deleted\n");

            stmt.close();
            con.close();
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private void deleteBtnOnAction(ActionEvent Event) {
        System.out.println("Delete button was pressed!");
        Button eventSource = (Button) Event.getSource();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Delete GOL File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GOL Files", "*.gol"));
        File selectedFile = fileChooser.showOpenDialog(eventSource.getScene().getWindow());
        if (selectedFile != null) {
            System.out.println("Attempting load from: " + selectedFile);
            if(selectedFile.delete())
                System.out.println("Deleted Successfully");
        }
    }

    private void loadDBBtnOnAction(ActionEvent actionEvent) {
        Scanner myObj = new Scanner(System.in);
        String FileName;

        // Enter username and press Enter
        System.out.println("Enter FileName");
        FileName = myObj.nextLine();
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bilal","root","Itsme7");
             PreparedStatement stmt = con.prepareStatement("select * from grid where name=?");) {
            stmt.setString(1,FileName);
            ResultSet rs = stmt.executeQuery();

            // Iterate through the data in the result set and display it.
            if (rs.next()) {
                String Grids = rs.getString("state");
                gameOfLife.loadGameBoardFromDB(Grids);
                System.out.println("Loaded\n");
            }

            stmt.close();
            con.close();
        }
        // Handle any errors that may have occurred.
        catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    private void loadBtnOnAction(ActionEvent e) {

        System.out.println("Load button was pressed!");
        Button eventSource = (Button) e.getSource();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load GOL File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GOL Files", "*.gol"));
        File selectedFile = fileChooser.showOpenDialog(eventSource.getScene().getWindow());


        if (selectedFile != null) {
            System.out.println("Attempting load from: " + selectedFile);
            try {
                if (gameOfLife.loadGameBoardFromFile(selectedFile)) {
                    //Trigger a redraw.
                    gameOfLife.setNeedsRedraw(true);
                    gameOfLife.setGeneration(0);
                    System.out.println("Load successful!");
                } else {
                    System.out.println("Load failed!");
                    int gridSize = gameOfLife.getGridSize();
                    System.out.println("The file needs to be a " + gridSize + "x" + gridSize + " CSV of long ints!");
                }
            } catch (IOException e1) {
                System.out.println("There was an error loading the file!");
                e1.printStackTrace();
            } catch (NumberFormatException e1) {
                System.out.println("The file had bad data!");
                int gridSize = gameOfLife.getGridSize();
                System.out.println("The file needs to be a " + gridSize + "x" + gridSize + " CSV of long ints!");
            }
        }
    }

    private void playBtnOnAction() {
        System.out.println("Play button was pressed!");
        gameOfLife.play();
    }

    private void pauseBtnOnAction() {
        System.out.println("Pause button was pressed!");
        gameOfLife.pause();
    }

    private void clearBtnOnAction() {
        System.out.println("Clear button was pressed!");
        gameOfLife.clear();
        //Trigger a redraw.
        gameOfLife.setNeedsRedraw(true);
    }

    private void nextBtnOnAction() {
        System.out.println("Next button was pressed!");
        gameOfLife.next();
    }
    private void ZoomSliderChangeAction() {
        System.out.println("The speed slider value was changed!");
        System.out.println("The new value is " + zoomSlider.getValue());
        int ZoomSliderValue=(int)zoomSlider.getValue();
        if (ZoomSliderValue == 0) {
            GameOfLife g=new GameOfLife(GameZoom.SMALL);
        } else if (ZoomSliderValue == 1) {
            GameOfLife g=new GameOfLife(GameZoom.NORMAL);
        } else if (ZoomSliderValue == 2) {
            GameOfLife g=new GameOfLife(GameZoom.BIG);
        }
    }

    private void speedSliderChangeAction() {
        System.out.println("The speed slider value was changed!");
        System.out.println("The new value is " + speedSlider.getValue());
        int speedSliderValue = (int)speedSlider.getValue();
        if (speedSliderValue == 0) {
            gameOfLife.initializeTimeline(VERYSLOW);
        } else if (speedSliderValue == 1) {
            gameOfLife.initializeTimeline(SLOW);
        } else if (speedSliderValue == 2) {
            gameOfLife.initializeTimeline(MEDIUM);
        } else if (speedSliderValue == 3) {
            gameOfLife.initializeTimeline(FAST);
        } else if (speedSliderValue == 4) {
            gameOfLife.initializeTimeline(VERYFAST);
        }
    }
    private void initializeZoomSlider() {
        zoomSlider.setMin(0);
        zoomSlider.setMax(2);
        zoomSlider.setValue(1);
        zoomSlider.setShowTickLabels(true);
        zoomSlider.setShowTickMarks(true);
        zoomSlider.setMajorTickUnit(1);
        zoomSlider.setMinorTickCount(0);
        zoomSlider.setBlockIncrement(1);
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n == 0) return GameZoom.SMALL.getLabel();
                if (n == 1) return GameZoom.NORMAL.getLabel();
                if (n == 2) return GameZoom.BIG.getLabel();
                return GameZoom.NORMAL.toString();
            }
            @Override
            public Double fromString(String s) {
                if (s.equals(GameZoom.SMALL.getLabel())) return 0d;
                if (s.equals(GameZoom.NORMAL.getLabel())) return 1d;
                if (s.equals(GameZoom.BIG.getLabel())) return 2d;
                return 0d;
            }
        });
    }


    private void initializeSpeedSlider(){
        // Set up the speedSlider options
        speedSlider.setMin(0);
        speedSlider.setMax(4);
        speedSlider.setValue(0);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1);
        speedSlider.setMinorTickCount(0);
        speedSlider.setBlockIncrement(1);
        speedSlider.setSnapToTicks(true);
        speedSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n == 0) return GameSpeed.VERYSLOW.getLabel();
                if (n == 1) return GameSpeed.SLOW.getLabel();
                if (n == 2) return GameSpeed.MEDIUM.getLabel();
                if (n == 3) return GameSpeed.FAST.getLabel();
                if (n == 4) return GameSpeed.VERYFAST.getLabel();
                return GameSpeed.SLOW.toString();
            }
            @Override
            public Double fromString(String s) {
                if (s.equals(GameSpeed.VERYSLOW.getLabel())) return 0d;
                if (s.equals(GameSpeed.SLOW.getLabel())) return 1d;
                if (s.equals(GameSpeed.MEDIUM.getLabel())) return 2d;
                if (s.equals(GameSpeed.FAST.getLabel())) return 3d;
                if (s.equals(GameSpeed.VERYFAST.getLabel())) return 4d;
                return 0d;
            }
        });
    }
}
