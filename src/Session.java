import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javafx.stage.Stage;
import javafx.application.Platform;

public class Session {
    public static final Map<String, Session> sessions = new HashMap<>();
    private static Timer timer;
    private Stage stage;

    private String username;
    private LocalDateTime lastActivity;

    public Session(String username) {
        this.username = username;
        updateLastActivity();
    }

    public void updateLastActivity() {
        lastActivity = LocalDateTime.now();
    }

    public boolean isExpired() {
        return lastActivity.plusMinutes(30).isBefore(LocalDateTime.now());
    }

    public static void startSession(String username) {
        Session session = new Session(username);
        sessions.put(username, session);
    }

    public static void startSessionExpirationTimer() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Checking session expiration");
                checkSessionExpiration();
            }
        }, 0, TimeUnit.MINUTES.toMillis(1)); // Check every minute
    }

    public static void checkSessionExpiration() {
        Platform.runLater(() -> {
            for (Map.Entry<String, Session> entry : sessions.entrySet()) {
                Session session = entry.getValue();
                if (session.isExpired()) {

                    System.out.println("Session expired for user: " + session.username);
                    sessions.remove(entry.getKey());

                    break; 
                }
            }
        });
    }
}
