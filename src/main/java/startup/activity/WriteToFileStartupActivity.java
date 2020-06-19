package startup.activity;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class WriteToFileStartupActivity implements StartupActivity {
    public static boolean enableWrite = false;
    private Project project;

    @Override
    public void runActivity(@NotNull Project project) {
        this.project = project;
        DumbService.getInstance(project).runWhenSmart(runnable);
    }

    Runnable runnable = () -> suggestEnableWriting(project);

    private void suggestEnableWriting(Project project) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        HyperlinkListener hyperLinkListener = new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    enableWrite(project);
                }
            }
        };
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(
                        "Do you want to enable write inline candidates to file? <a href=\"enable\">Enable</a>",
                        MessageType.WARNING, hyperLinkListener)

                .setHideOnLinkClick(true)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }

    public static void setEnableWrite(boolean enableWrite) {
        WriteToFileStartupActivity.enableWrite = enableWrite;
    }

    private void enableWrite(Project project) {
        setEnableWrite(true);
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(
                        "Writing inline candidates to file annotation processing has been enabled",
                        MessageType.INFO,
                        null
                )
                .setFadeoutTime(3000)
                .createBalloon()
                .show(RelativePoint.getNorthEastOf(statusBar.getComponent()), Balloon.Position.atRight);
    }
}

