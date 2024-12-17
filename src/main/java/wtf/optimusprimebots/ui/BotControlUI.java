package wtf.optimusprimebots.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import wtf.optimusprimebots.Bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BotControlUI {

    @FXML
    private TextArea chatLogArea;

    @FXML
    private ListView<String> botActivityList;

    @FXML
    private TextArea errorLogArea;

    @FXML
    private TextField botsValue;

    @FXML
    private TextField messageTextField;

    @FXML
    private Button sendMessageButton;

    @FXML
    private TextArea serverStatsArea;

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private CheckBox randomNames;

    @FXML
    private TextField nameField;

    @FXML
    private Button addBotButton;

    @FXML
    private CheckBox spamTheChatCheckBox;

    @FXML
    private TextArea textForSpam;

    private Bot bot;
    private final List<Bot> bots = new ArrayList<>();

    @FXML
    private void initialize() {
        bot = new Bot(this, "MrBeastDefault");
        sendMessageButton.setOnAction(e -> sendMessage());
        addBotButton.setOnAction(e -> addBot());
        spamTheChatCheckBox.setOnAction(e -> spamTheChat());
    }

    @FXML
    private void sendMessage() {
        String message = messageTextField.getText();
        if (message != null && !message.isEmpty()) {
            for (Bot currentBot : bots) {
                currentBot.sendMessage(message);
            }
            messageTextField.clear();
        }
    }

    @FXML
    private void addBot() {
        try {
            String host = hostField.getText().trim();
            String portText = portField.getText().trim();
            String name = nameField.getText().trim();
            int botCount;

            if (botsValue.getText().trim().isEmpty()) {
                botCount = 1;
            } else {
                botCount = Integer.parseInt(botsValue.getText().trim());
            }

            if (host.isEmpty() || portText.isEmpty() || name.isEmpty() || botCount <= 0) {
                updateErrorLog("All fields must be filled in, and bot count must be greater than 0.");
                return;
            }

            int port;
            try {
                port = Integer.parseInt(portText);
                if (port <= 0 || port > 65535) {
                    throw new NumberFormatException("Port out of range.");
                }
            } catch (NumberFormatException e) {
                updateErrorLog("Invalid port. Please enter a number between 1 and 65535.");
                return;
            }

            if (botCount > 1) {
                for (int i = 0; i < botCount; i++) {
                    String randomName = generateRandomName();
                    Bot newBot = new Bot(this, randomName);
                    newBot.startBot(host, port, randomName);
                    bots.add(newBot);
                }
            } else {
                Bot newBot = new Bot(this, name);
                newBot.startBot(host, port, name);
                bots.add(newBot);
            }

            updateBotActivity("Added " + botCount + " bot(s) at " + host + ":" + port);

        } catch (Exception e) {
            updateErrorLog("An error occurred while adding the bot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateRandomName() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder randomName = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            randomName.append(letters.charAt(random.nextInt(letters.length())));
        }
        randomName.append(random.nextInt(100));
        return randomName.toString();
    }



    public void updateChatLog(String message) {
        Platform.runLater(() -> chatLogArea.appendText(message + "\n"));
    }

    public void updateBotActivity(String activity) {
        Platform.runLater(() -> botActivityList.getItems().add(activity));
    }

    public void updateErrorLog(String error) {
        Platform.runLater(() -> errorLogArea.appendText(error + "\n"));
    }

    public void updateServerStats(String stats) {
        Platform.runLater(() -> serverStatsArea.appendText(stats + "\n"));
    }

    @FXML
    private void spamTheChat() {
        if (spamTheChatCheckBox.isSelected()) {
            String spamMessage = textForSpam.getText().trim();
            if (spamMessage.isEmpty()) {
                updateErrorLog("Spam cannot be empty");
                return;
            }
            new Thread(() -> {
                try {
                    while (spamTheChatCheckBox.isSelected()) {
                        for (Bot currentBot : bots) {
                            bot.sendMessage(spamMessage);
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    updateErrorLog("Spam was stopped: " + e.getMessage());
                }
            }).start();
        }
    }
}
