package com.github.jaksonlin.pitestintellij.context;

import com.github.jaksonlin.pitestintellij.util.Mutation;
import com.github.jaksonlin.pitestintellij.util.MutationReportParser;
import com.github.jaksonlin.pitestintellij.util.ProcessResult;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class PitestContext {
    private String testFilePath;
    private String fullyQualifiedTargetTestClassName;
    private String javaHome;
    private List<String> sourceRoots;
    private String targetClassFullyQualifiedName;
    private String targetClassPackageName;
    private String targetClassSourceRoot;
    private String targetClassFilePath;
    private String targetClassName;
    private String reportDirectory;
    private String classpathFile;
    private String classpathFileDirectory;
    private List<String> command;
    private transient ProcessResult processResult;
    private String pitestDependencies;
    private List<String> resourceDirectories;
    private final long timestamp;
    private List<Mutation> mutationResults;

    // this will also init the fielid mutationResults
    public List<Mutation> collectMutationsResults() throws Exception {
        String mutationReportFilePath = this.getPitestReportXml();
        try {
            this.mutationResults = MutationReportParser.parseMutationsFromXml(mutationReportFilePath).getMutation();
            return this.mutationResults;
        } catch (IOException e) {
            throw new Exception("Error parsing mutation report: " + e.getMessage() + " at " + mutationReportFilePath);
        }
    }
    public List<Mutation> getMutationResults()  {
        return this.mutationResults;
    }

    public String getPitestReportXml() {
        return Paths.get(this.getReportDirectory(), "mutations.xml").toString();
    }

    public PitestContext(long timestamp) {
        this.timestamp = timestamp;
        this.command = java.util.Collections.emptyList();
    }

    public PitestContext(String testFilePath, long timestamp) {
        this.testFilePath = testFilePath;
        this.timestamp = timestamp;
        this.command = java.util.Collections.emptyList();
    }

    public String getTestFilePath() {
        return testFilePath;
    }

    public void setTestFilePath(String testFilePath) {
        this.testFilePath = testFilePath;
    }

    public String getFullyQualifiedTargetTestClassName() {
        return fullyQualifiedTargetTestClassName;
    }

    public void setFullyQualifiedTargetTestClassName(String fullyQualifiedTargetTestClassName) {
        this.fullyQualifiedTargetTestClassName = fullyQualifiedTargetTestClassName;
    }

    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public List<String> getSourceRoots() {
        return sourceRoots;
    }

    public void setSourceRoots(List<String> sourceRoots) {
        this.sourceRoots = sourceRoots;
    }

    public String getTargetClassFullyQualifiedName() {
        return targetClassFullyQualifiedName;
    }

    public void setTargetClassFullyQualifiedName(String targetClassFullyQualifiedName) {
        this.targetClassFullyQualifiedName = targetClassFullyQualifiedName;
    }

    public String getTargetClassPackageName() {
        return targetClassPackageName;
    }

    public void setTargetClassPackageName(String targetClassPackageName) {
        this.targetClassPackageName = targetClassPackageName;
    }

    public String getTargetClassSourceRoot() {
        return targetClassSourceRoot;
    }

    public void setTargetClassSourceRoot(String targetClassSourceRoot) {
        this.targetClassSourceRoot = targetClassSourceRoot;
    }

    public String getTargetClassFilePath() {
        return targetClassFilePath;
    }

    public void setTargetClassFilePath(String targetClassFilePath) {
        this.targetClassFilePath = targetClassFilePath;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    public void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    public String getReportDirectory() {
        return reportDirectory;
    }

    public void setReportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    public String getClasspathFile() {
        return classpathFile;
    }

    public void setClasspathFile(String classpathFile) {
        this.classpathFile = classpathFile;
    }

    public String getClasspathFileDirectory() {
        return classpathFileDirectory;
    }

    public void setClasspathFileDirectory(String classpathFileDirectory) {
        this.classpathFileDirectory = classpathFileDirectory;
    }

    public List<String> getCommand() {
        return command;
    }

    public void setCommand(List<String> command) {
        this.command = command;
    }

    public ProcessResult getProcessResult() {
        return processResult;
    }

    public void setProcessResult(ProcessResult processResult) {
        this.processResult = processResult;
    }

    public String getPitestDependencies() {
        return pitestDependencies;
    }

    public void setPitestDependencies(String pitestDependencies) {
        this.pitestDependencies = pitestDependencies;
    }

    public List<String> getResourceDirectories() {
        return resourceDirectories;
    }

    public void setResourceDirectories(List<String> resourceDirectories) {
        this.resourceDirectories = resourceDirectories;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static String dumpPitestContext(PitestContext context) {
        return String.format("""
                testVirtualFile: %s
                fullyQualifiedTargetTestClassName: %s
                javaHome: %s
                sourceRoots: %s
                fullyQualifiedTargetClassName: %s
                targetClassSourceRoot: %s
                reportDirectory: %s
                classpathFile: %s
                command: %s
                processResult: %s
                pitestDependencies: %s
                """,
                context.getTestFilePath(),
                context.getFullyQualifiedTargetTestClassName(),
                context.getJavaHome(),
                context.getSourceRoots(),
                context.getTargetClassFullyQualifiedName(),
                context.getTargetClassSourceRoot(),
                context.getReportDirectory(),
                context.getClasspathFile(),
                context.getCommand(),
                context.getProcessResult(),
                context.getPitestDependencies()
        );
    }
}