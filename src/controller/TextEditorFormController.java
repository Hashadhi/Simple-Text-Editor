package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditorFormController {
    public BorderPane brdrpnArea;
    public TextArea txtAreaEditor;
    public TextField txtSource;
    public Clipboard clipboard;
    public Button btnNew;
    public Button btnOpen;
    public Button btnSave;
    public Button btnCut;
    public Button btnCopy;
    public Button btnPaste;
    public Button btnFind;
    public Button btnReplace;
    public Button btnDown;
    public Button btnUp;
    public TextField txtSearch;
    public boolean isEdited = false;
    public TextField txtTotWords;
    public TextField txtTotFoundWords;
    public Stage stage;
    public ToggleButton btnRegExp;
    public ToggleButton btnCaseSens;
    public Matcher matcher;
    public TextField txtReplaceWith;
    public Button btnReplaceWith;


    public void initialize() {
        btnNew.setTooltip(new Tooltip("New file"));
        btnOpen.setTooltip(new Tooltip("Open file"));
        btnSave.setTooltip(new Tooltip("Save file"));
        btnCut.setTooltip(new Tooltip("Cut"));
        btnCopy.setTooltip(new Tooltip("Copy"));
        btnPaste.setTooltip(new Tooltip("Paste"));
        btnFind.setTooltip(new Tooltip("Find text"));
        btnUp.setTooltip(new Tooltip("Move up"));
        btnDown.setTooltip(new Tooltip("Move down"));

        txtAreaEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            isEdited = true;
            txtTotWords.setText(String.valueOf(txtAreaEditor.getText().split(" ").length));
        });
    }

    public void searchOnAction(ActionEvent actionEvent) {
        txtAreaEditor.deselect();
        if (isEdited) {
            int flags = 0;
            if (!btnRegExp.isSelected()) flags = flags | Pattern.LITERAL;
            if (!btnCaseSens.isSelected()) flags = flags | Pattern.CASE_INSENSITIVE;

            matcher = Pattern.compile(txtSearch.getText(), flags).matcher(txtAreaEditor.getText());
            isEdited = false;
        }

        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            txtAreaEditor.selectRange(start, end);
        } else {
            matcher.reset();
        }
    }

    public void replaceOnAction(ActionEvent actionEvent) {
        btnFind.fire();
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            txtAreaEditor.replaceText(start, end, txtReplaceWith.getText());
        } else {
            matcher.reset();
        }
    }

    public void replaceAllOnAction(ActionEvent actionEvent) {
        String replaceWith = txtReplaceWith.getText();
        txtAreaEditor.setText(txtAreaEditor.getText().replace(txtSearch.getText(), replaceWith));
    }

    public void upOnAction(ActionEvent actionEvent) {
    }

    public void downOnAction(ActionEvent actionEvent) {
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            txtAreaEditor.selectRange(start, end);
        } else {
            matcher.reset();
        }
    }

    public void regExpOnAction(ActionEvent actionEvent) {
        isEdited = true;
        btnFind.fire();
    }

    public void caseSensitiveOnAction(ActionEvent actionEvent) {
        isEdited = true;
        btnFind.fire();
    }

    public void newDocOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Want to save the file?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        Optional<ButtonType> choice = alert.showAndWait();

        if (choice.get() == ButtonType.YES) {
            saveOnAction(actionEvent);
            txtAreaEditor.clear();
        } else if (choice.get() == ButtonType.NO) {
            txtAreaEditor.clear();
        }
    }

    public void openOnAction(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a text file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showOpenDialog(null);

        String location = file.getAbsolutePath();

        Path path = Paths.get(file.getAbsolutePath());

        byte[] bytes = Files.readAllBytes(path);
        txtAreaEditor.setText(new String(bytes));
    }

    public void saveOnAction(ActionEvent actionEvent) throws IOException {
        String content = txtAreaEditor.getText();
        byte[] fileBytes = content.getBytes();

        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setTitle("Open a directory");
        File file = directoryChooser.showDialog(null);
        String destinationPath = file.getAbsolutePath();

        String fileName = JOptionPane.showInputDialog("File name: ");
        Path path = Paths.get(destinationPath + "/" + fileName + ".txt");

        SeekableByteChannel byteChannel = Files.newByteChannel(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ByteBuffer buffer = ByteBuffer.wrap(fileBytes);
        byteChannel.write(buffer);
        byteChannel.close();
        Files.write(path, fileBytes);

        new Alert(Alert.AlertType.INFORMATION, "Saved!").show();
    }

    public void exitOnAction(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void cutOnAction(ActionEvent actionEvent) {
        setSelectedText();
        String[] texts = txtAreaEditor.getText().split(txtAreaEditor.getSelectedText());
        txtAreaEditor.clear();
        for (String text : texts) {
            txtAreaEditor.setText(txtAreaEditor.getText() + text);
        }
    }

    public void copyOnAction(ActionEvent actionEvent) {
        setSelectedText();
    }

    public void pasteOnAction(ActionEvent actionEvent) {
        int caretPosition = txtAreaEditor.getCaretPosition();
        txtAreaEditor.insertText(caretPosition, clipboard.getString());
    }

    public void selectAllOnAction(ActionEvent actionEvent) {
        txtAreaEditor.selectAll();
    }

    public void aboutUsOnAction(ActionEvent actionEvent) throws IOException {
        Parent load = FXMLLoader.load(getClass().getResource("/view/aboutUsForm.fxml"));
        Scene scene = new Scene(load);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    private void setSelectedText() {
        clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(txtAreaEditor.getSelectedText());
        clipboard.setContent(content);
    }

    public void newFileOnAction(ActionEvent actionEvent) throws IOException {
        newDocOnAction(actionEvent);
    }

    public void openFileOnAction(ActionEvent actionEvent) throws IOException {
        openOnAction(actionEvent);
    }

    public void saveFileOnAction(ActionEvent actionEvent) throws IOException {
        saveOnAction(actionEvent);
    }

    public void cutTextOnAction(ActionEvent actionEvent) {
        cutOnAction(actionEvent);
    }

    public void copyTextOnAction(ActionEvent actionEvent) {
        copyOnAction(actionEvent);

    }

    public void pasteFileOnAction(ActionEvent actionEvent) {
        pasteOnAction(actionEvent);
    }

}

