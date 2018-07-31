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
                creatIntentSB.append(">{\n")
                creatIntentSB.append("val params = HttpParams()\n")
                creatIntentSB.append("params.put(\"string\", string)")
                if(methodBeam.httpType==0){
                    creatIntentSB.append("\n\t return httpLiveGet<")
                }else{
                    creatIntentSB.append("\n\t return httpLivePost<")
                }
            } else {
                creatIntentSB.append("Flowable<")
                creatIntentSB.append(methodBeam.beanSName)
                creatIntentSB.append(">{\n")
                creatIntentSB.append("val params = HttpParams()\n")
                creatIntentSB.append("params.put(\"string\", string)")
                if(methodBeam.httpType==0){
                    creatIntentSB.append("\n\t return httpRxGet<")
                }else{
                    creatIntentSB.append("\n\t return httpRxPost<")
                }
            }
            creatIntentSB.append(methodBeam.beanSName)
            creatIntentSB.append(">(")
            creatIntentSB.append(methodBeam.uriName)
            creatIntentSB.append("\n,params,\n")
            if (methodBeam.converterType == 0) {//bean类型
                creatIntentSB.append("BeanConverter(\"" + methodBeam.keyName + "\",")
                creatIntentSB.append(methodBeam.beanSName)
                creatIntentSB.append("::class.java))\n")
            } else if (methodBeam.converterType == 1) {
                creatIntentSB.append("BeanListConverters(\"\"+methodBeam.getKeyName()+\"\",")
                creatIntentSB.append(methodBeam.beanSName)
                creatIntentSB.append("::class.java))\n")
            } else {
                creatIntentSB.append("JsonObjectConverter())\t")
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
        insertImports(mClass.containingKtFile, "android.content.Context")
        insertImports(mClass.containingKtFile, "com.lzy.okgo.model.HttpParams")
        if (methodBeam.rxType == 0) {
            insertImports(mClass.containingKtFile, "android.arch.lifecycle.LiveData")
            if(methodBeam.httpType==0){
                insertImports(mClass.containingKtFile, "com.aac.data.http.utils.httpLiveGet")
            }else{
                insertImports(mClass.containingKtFile, "com.aac.data.http.utils.httpLivePost")
            }
        }else{
            insertImports(mClass.containingKtFile, "io.reactivex.Flowable")
            if(methodBeam.httpType==0){
                insertImports(mClass.containingKtFile, "com.aac.data.http.utils.httpRxGet")
            }else{
                insertImports(mClass.containingKtFile, "com.aac.data.http.utils.httpRxPost")
            }
        }
        if (methodBeam.converterType == 2) {
            insertImports(mClass.containingKtFile, "com.aac.data.http.converter.JsonObjectConverter")
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
