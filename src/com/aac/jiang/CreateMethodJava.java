package com.aac.jiang;

import com.aac.jiang.view.EditDialog;
import com.aac.jiang.view.IWriter;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class CreateMethodJava extends BaseGenerateAction {

    public CreateMethodJava() {
        super(null);
    }

    public CreateMethodJava(CodeInsightActionHandler handler) {
        super(handler);
    }
    @Override
    protected boolean isValidForClass(PsiClass targetClass) {
//        PsiClass[] supers = targetClass.getSupers();
//        for (int i = 0; i < supers.length; i++) {
//            if (supers[i].getQualifiedName().equals("android.app.Activity")) {
//                return true;
//            }
//        }
        return true;
    }
    @Override
    protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        return file.getName().endsWith(".java");
    }
    @Override
    public void actionPerformedImpl(@NotNull Project project, Editor editor) {
        PsiJavaFile psiFile = (PsiJavaFile) PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiClass psiClass = getPsiClassFromContext(psiFile, editor);
        EditDialog dialog=new EditDialog(beam -> {
           new IWriter(psiFile.getProject(), psiFile,psiClass, beam,JavaPsiFacade.getElementFactory(psiClass.getProject())).execute();
        });
        dialog.pack();
        dialog.setTitle("创建");
        dialog.setSize(450, 390);
        Toolkit kit = Toolkit.getDefaultToolkit();    // ;定义工具包
        Dimension screenSize = kit.getScreenSize() ;  // 获取屏幕的尺寸
        int screenWidth = screenSize.width / 2  ;       // 获取屏幕的宽
        int screenHeight = screenSize.height / 2   ;    // 获取屏幕的高
        int height = 650;
        int width = 500;
        dialog.setLocation(screenWidth - width / 2, screenHeight - height / 2);
        dialog.setVisible(true);

    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        actionPerformedImpl(project, editor);
    }

    /**
     * @param psiFile
     * @param editor
     * @return
     */
    private PsiClass getPsiClassFromContext(PsiFile psiFile, Editor editor) {
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(element, PsiClass.class);
    }
}
