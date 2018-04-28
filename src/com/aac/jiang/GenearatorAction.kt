package com.aac.jiang

import cn.yzl.kotlin.ex.click.IWriter
import com.aac.jiang.util.Utils
import com.aac.jiang.view.EditDialog
import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.generation.actions.BaseGenerateAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilBase
import org.jetbrains.kotlin.idea.internal.Location
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import java.awt.Toolkit

/**
 * Created by YZL on 2017/8/13.
 */
class GenearatorAction : BaseGenerateAction {


    constructor() : super(null) {}

    constructor(handler: CodeInsightActionHandler) : super(handler) {}

    override fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {
        return file.name.endsWith(".kt")
    }

    override fun isValidForClass(targetClass: PsiClass): Boolean {
        return true
    }
    override fun actionPerformedImpl(project: Project, editor: Editor?) {
        val file = PsiUtilBase.getPsiFileInEditor(editor!!, project)
        // 获取编辑器中的文件
        val psiFile = PsiUtilBase.getPsiFileInEditor(editor, project) as KtFile
        // 获取当前类
        val targetClass = getPsiClassFromEvent(editor)
        // 获取元素操作的工厂类
        val dialog = EditDialog { beam -> IWriter(project, psiFile, targetClass!!, beam).execute() }
        dialog.pack()
        dialog.setSize(400, 250)
        val kit = Toolkit.getDefaultToolkit()    // 定义工具包
        val screenSize = kit.screenSize   // 获取屏幕的尺寸
        val screenWidth = screenSize.width / 2         // 获取屏幕的宽
        val screenHeight = screenSize.height / 2       // 获取屏幕的高
        val height = 500
        val width = 500
        dialog.setLocation(screenWidth - width / 2, screenHeight - height / 2)
        dialog.isVisible = true
    }

    private fun getPsiClassFromEvent(editor: Editor?): KtClass? {
        //        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor == null) {
            return null
        }
        val project = editor.project ?: return null

        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
        if (psiFile == null || psiFile !is KtFile)
            return null

        val location = Location.fromEditor(editor, project)
        val psiElement = psiFile.findElementAt(location.startOffset) ?: return null

        return Utils.getKtClassForElement(psiElement)
    }

}
