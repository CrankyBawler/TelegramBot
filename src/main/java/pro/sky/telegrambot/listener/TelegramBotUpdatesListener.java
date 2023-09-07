package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.TaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private TaskRepository taskRepository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Process your updates here
            String startMessage = update.message().text();
            if (startMessage.equals("/start")) {
                long chatId = update.message().chat().id();
                SendResponse response = telegramBot.execute(new SendMessage(chatId, "Здравствуй!"));
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendMessage() {
        List<NotificationTask> allByDate = taskRepository.findAllByDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        logger.info("All message: {}", allByDate);
        allByDate.forEach(notificationTask -> {
            SendMessage message = new SendMessage(notificationTask.getUserId(), notificationTask.getText());
            SendResponse sendResponse = telegramBot.execute(message);
            if (sendResponse.isOk()) {
                taskRepository.deleteById(notificationTask.getId());
            } else {
                logger.info("Message can not sent, code has error: {}", sendResponse.errorCode());
            }

        });
    }
        @Transactional
        public List<NotificationTask> saveDateAndTime (List < Update > updates) {
            List<NotificationTask> list = new ArrayList<>();
            updates.forEach(update -> {
                NotificationTask notificationTask = new NotificationTask();
                Long id = update.message().chat().id();
                notificationTask.setUserId(id);

                String datePatternDate = "(\\d{2})\\.(\\d{2})\\.(\\d{4})\\s([0-2][0-9]):([0-9][0-9])?";
                Pattern pattern = Pattern.compile(datePatternDate);
                Matcher matcher = pattern.matcher(update.message().text());
                while (matcher.find()) {
                    int day = Integer.parseInt(matcher.group(1));
                    int month = Integer.parseInt(matcher.group(2));
                    int year = Integer.parseInt(matcher.group(3));
                    int hour = Integer.parseInt(matcher.group(4));
                    int minutes = Integer.parseInt(matcher.group(5));
                    LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minutes);
                    notificationTask.setDate(localDateTime);
                }
                String datePatternMessage = "\\D*[а-яА-Я]+";
                Pattern patternMessage = Pattern.compile(datePatternMessage);
                Matcher matcherMessage = patternMessage.matcher(update.message().text());
                while (matcherMessage.find()) {
                    String group = matcherMessage.group();
                    notificationTask.setText(group);

                }
                taskRepository.save(notificationTask);
                list.add(notificationTask);
            });
            return list;
        }

}



