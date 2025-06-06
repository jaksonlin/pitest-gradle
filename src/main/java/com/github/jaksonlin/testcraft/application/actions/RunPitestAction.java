package com.github.jaksonlin.testcraft.application.actions;

import com.github.jaksonlin.testcraft.domain.context.PitestContext;
import com.github.jaksonlin.testcraft.infrastructure.commands.CommandCancellationException;
import com.github.jaksonlin.testcraft.infrastructure.commands.pitest.*;
import com.github.jaksonlin.testcraft.infrastructure.services.business.PitestService;
import com.github.jaksonlin.testcraft.infrastructure.services.system.I18nService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class RunPitestAction extends AnAction {

    private final PitestService pitestService;

    public RunPitestAction() {
        pitestService = ApplicationManager.getApplication().getService(PitestService.class);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        // You can change the text here
        e.getPresentation().setText(I18nService.getInstance().message("action.RunPitestAction.text"));
        
        
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project targetProject = e.getProject();
        if (targetProject == null) {
            return;
        }

        VirtualFile testVirtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (testVirtualFile == null) {
            return;
        }

        runPitest(targetProject, testVirtualFile.getPath());
    }

    public void runPitest(Project targetProject, String testFilePath) {
        PitestContext context = new PitestContext(testFilePath, System.currentTimeMillis());

        List<PitestCommand> commands = Arrays.asList(
                new PrepareEnvironmentCommand(targetProject, context),
                new MethodToMutateCommand(targetProject, context),
                new BuildPitestCommandCommand(targetProject, context),
                new RunPitestCommand(targetProject, context),
                new HandlePitestResultCommand(targetProject, context),
                new StoreHistoryCommand(targetProject, context)
        );

        new Task.Backgroundable(targetProject, "Running pitest", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    for (PitestCommand command : commands) {
                        if (indicator.isCanceled()) {
                            ApplicationManager.getApplication().invokeLater(() ->
                                    Messages.showInfoMessage(I18nService.getInstance().message("pitest.run.canceled"), I18nService.getInstance().message("pitest.run.canceled.title"))
                            );
                            break;
                        }
                        command.execute();
                    }
                } catch (Exception e) {
                    if (e.getCause() instanceof CommandCancellationException) {
                        ApplicationManager.getApplication().invokeLater(() ->
                                Messages.showInfoMessage(I18nService.getInstance().message("pitest.run.canceled"), I18nService.getInstance().message("pitest.run.canceled.title"))
                        );
                    } else {
                        showErrorDialog(e, context);
                    }
                }
            }
        }.queue();
    }

    protected void showErrorDialog(Exception e, PitestContext context) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();
        String contextInformation = PitestContext.dumpPitestContext(context);
        String errorMessage = I18nService.getInstance().message("error.pitest.general.title") + ": " + e.getMessage() + "; " + contextInformation + "\n" + stackTrace;

        JTextArea textArea = new JTextArea(errorMessage);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JBScrollPane scrollPane = new JBScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(640, 480));

        ApplicationManager.getApplication().invokeLater(() ->
                Messages.showErrorDialog(scrollPane, errorMessage)
        );
    }
}