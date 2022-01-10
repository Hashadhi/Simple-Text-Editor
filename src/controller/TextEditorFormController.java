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
    public Button btnRegExp;
    public Button btnCaseSensitive;


    public void initialize(){
        btnNew.setTooltip(new Tooltip("New file"));
        btnOpen.setTooltip(new Tooltip("Open file"));
        btnSave.setTooltip(new Tooltip("Save file"));
        btnCut.setTooltip(new Tooltip("Cut"));
        btnCopy.setTooltip(new Tooltip("Copy"));
        btnPaste.setTooltip(new Tooltip("Paste"));
        btnFind.setTooltip(new Tooltip("Find text"));
        btnUp.setTooltip(new Tooltip("Move up"));
        btnDown.setTooltip(new Tooltip("Move down"));
    }

    public void newDocOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) brdrpnArea.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/textEditorForm.fxml"))));
        stage.show();

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
        String destinationPath=file.getAbsolutePath();

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
        for (String text:texts
        ) {
            txtAreaEditor.setText(txtAreaEditor.getText()+text);
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
    private void setSelectedText(){
        clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(txtAreaEditor.getSelectedText());
        clipboard.setContent(content);
    }
}
