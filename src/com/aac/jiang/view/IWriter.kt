package cn.yzl.kotlin.ex.click

import com.aac.jiang.modle.MethodBeam
import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.caches.resolve.resolveImportReference
import org.jetbrains.kotlin.idea.util.ImportInsertHelper
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * Created by YZL on 2017/8/14.
 */
class IWriter(protected var mProject: Project, protected var mFile: PsiFile, protected var mClass: KtClass,
              private val methodBeam: MethodBeam)
    : WriteCommandAction.Simple<Any>(mProject, mFile) {
    var ktPsiFactory: KtPsiFactory

    init {
        ktPsiFactory = KtPsiFactory(mProject, false)
    }

    @Throws(Throwable::class)
    override fun run() {
        createCode()
//       重新格式化代码
        ReformatCodeProcessor(mProject, mFile, null, true).run()
    }

    private fun createCode() {
        run {
            //onclick(v:View)
            val creatIntentSB = StringBuffer()
            creatIntentSB.append("fun ")
            creatIntentSB.append(methodBeam.methodName)
            creatIntentSB.append("(context: Context,string: String):")
            if (methodBeam.rxType == 0) {
                creatIntentSB.append("LiveData<")
                creatIntentSB.append(methodBeam.beanSName)
                creatIntentSB.append(">")
            } else {
                creatIntentSB.append("Flowable<")
                creatIntentSB.append(methodBeam.beanSName)
                creatIntentSB.append(">{\n")
            }
            creatIntentSB.append("{\n\t return OkGo.get<")
            creatIntentSB.append(methodBeam.beanSName)
            creatIntentSB.append(">(")
            creatIntentSB.append(methodBeam.uriName)
            creatIntentSB.append(")\n.tag(context)\n")
            creatIntentSB.append(".params(\"string\",string)\n")
            if (methodBeam.converterType == 0) {//bean类型
                creatIntentSB.append(".converter(BeanConverter(\"" + methodBeam.keyName + "\",")
                creatIntentSB.append(methodBeam.beanSName)
                creatIntentSB.append("::class.java))\n")
            } else if (methodBeam.converterType == 1) {
                creatIntentSB.append(".converter(BeanListConverters(\"\"+methodBeam.getKeyName()+\"\",")
                creatIntentSB.append(methodBeam.beanSName)
                creatIntentSB.append("::class.java))\n")
            } else {
                creatIntentSB.append(".converter(JsonObjectConverter())\t")
            }
            if (methodBeam.rxType == 0) {
                creatIntentSB.append(".adapt(LiveDataAdapter())")
            } else {
                creatIntentSB.append(".adapt(RxDataAdapter())\n")
                creatIntentSB.append(".compose(defaultSchedulers())")
            }
            creatIntentSB.append("\n}")
            if (mClass.getBody() != null && mClass.getBody()!!.lastChild != null) {
                try {
                    mClass.getBody()!!.addBefore(ktPsiFactory.createFunction(creatIntentSB.toString()),
                            mClass.getBody()!!.lastChild)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        insertImports(mClass.containingKtFile, "com.lzy.okgo.OkGo")
        insertImports(mClass.containingKtFile, "android.content.Context")
        if (methodBeam.rxType == 0) {
            insertImports(mClass.containingKtFile, "android.arch.lifecycle.LiveData")
        }else{
            insertImports(mClass.containingKtFile, "io.reactivex.Flowable")
            insertImports(mClass.containingKtFile, "io.reactivex.functions.Function")
        }
    }

    fun insertImports(ktFile: KtFile, path: String) {
        ktFile.importList
        val importList = ktFile.importDirectives
        for (importDirective in importList) {
            val importPath = importDirective.importPath
            if (importPath != null) {
                val pathStr = importPath.pathStr
                if (pathStr == path) {
                    return
                }
            }
        }
        ImportInsertHelper.getInstance(mProject)
                .importDescriptor(ktFile, ktFile.resolveImportReference(FqName(path)).iterator().next(), false)
    }


}
