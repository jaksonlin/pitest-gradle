package com.github.jaksonlin.pitestintellij.viewmodels;

import com.github.jaksonlin.pitestintellij.components.ObservableTree;
import com.github.jaksonlin.pitestintellij.mediators.IMutationMediator;
import com.github.jaksonlin.pitestintellij.mediators.MutationMediatorImpl;
import com.github.jaksonlin.pitestintellij.services.RunHistoryManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;

public class MutationToolWindowViewModel {
    private final RunHistoryManager runHistoryManager;
    private final IMutationMediator mutationReportMediator = new MutationMediatorImpl();
    private final MutationTreeMediatorViewModel mutationTreeMediatorVM;

    public MutationToolWindowViewModel(Project project, ObservableTree mutationTree) {
        this.runHistoryManager = ServiceManager.getService(project, RunHistoryManager.class);
        this.mutationTreeMediatorVM = new MutationTreeMediatorViewModel(project, mutationReportMediator);
        runHistoryManager.addObserver(mutationTree);
    }

    public void handleOpenSelectedNode(DefaultMutableTreeNode selectedNode) {
        mutationTreeMediatorVM.handleOpenSelectedNode(selectedNode);
    }

    public void handleTreeClear() {
        runHistoryManager.clearRunHistory();
    }

    @Nullable
    public TreePath handleSearchInTree(String searchText, DefaultMutableTreeNode rootNode) {
        if (searchText.isEmpty()) {
            return null;
        }
        DefaultMutableTreeNode node = findNode(rootNode, searchText);
        return node != null ? new TreePath(node.getPath()) : null;
    }

    @Nullable
    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode root, String searchText) {
        Enumeration enumeration = root.depthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)enumeration.nextElement();
            if (node.getUserObject().toString().toLowerCase().contains(searchText.toLowerCase())) {
                return node;
            }
        }
        return null;
    }
}