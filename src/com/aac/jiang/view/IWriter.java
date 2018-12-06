package com.aac.jiang.view;

import com.aac.jiang.modle.MethodBeam;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.source.codeStyle.ImportHelper;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;

import java.util.List;


/**
 * Created by YZL on 2017/8/14.
 */
public class IWriter extends WriteCommandAction.Simple {

    private MethodBeam methodBeam;
    protected PsiFile mFile;
    protected Project mProject;
    protected PsiClass mClass;
    protected PsiElementFactory mFactory;

    public IWriter(Project project, PsiFile mFile, PsiClass mClass, MethodBeam types, PsiElementFactory mFactory, PsiFile... files) {
        super(project, files);
        this.mFile = mFile;
        this.mProject = project;
        this.mClass = mClass;
        this.mFactory = mFactory;
        this.methodBeam = types;
    }

    @Override
    protected void run() {
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
        addInitIntentMethod();
        addImport("android.content.Context", "Context");
        addImport("com.lzy.okgo.model.HttpParams", "HttpParams");
        addImport("com.aac.data.http.utils.AacUtils", "AacUtils");
        if (methodBeam.getRxType() == 0) {
            addImport("android.arch.lifecycle.LiveData", "LiveData");
            if (methodBeam.getHttpType() == 0) {
                addImport("com.aac.data.http.utils.httpLiveGet", "httpLiveGet");
            } else {
                addImport("com.aac.data.http.utils.httpLivePost", "httpLivePost");
            }
        } else {
            addImport("io.reactivex.Flowable", "Flowable");
            if (methodBeam.getHttpType() == 0) {
                addImport("com.aac.data.http.utils.httpRxGet", "httpRxGet");
            } else {
                addImport("com.aac.data.http.utils.httpRxPost", "httpRxPost");
            }
        }
        if (methodBeam.getConverterType() == 2) {
            addImport("com.aac.data.http.converter.JsonObjectConverter", "JsonObjectConverter");
            addImport("com.alibaba.fastjson.JSONObject", "JSONObject");
        } else if (methodBeam.getConverterType() == 1) {
            addImport("com.alibaba.fastjson.TypeReference", "TypeReference");
        }
        //重新格式化代码
        styleManager.optimizeImports(mFile);
        styleManager.shortenClassReferences(mClass);
        new ReformatCodeProcessor(mProject, mClass.getContainingFile(), null, false).runWithoutProgress();
    }

    private void addInitIntentMethod() {
        StringBuffer creatIntentSB = new StringBuffer();
        StringBuffer creatIntentSB2 = new StringBuffer();
        //相同部分
        if (methodBeam.getConverterType() == 0) {//bean类型
            creatIntentSB2.append(methodBeam.getBeanSName());
        } else if (methodBeam.getConverterType() == 1) {
            creatIntentSB2.append("List<");
            creatIntentSB2.append(methodBeam.getBeanSName());
            creatIntentSB2.append(">");
        } else {
            creatIntentSB2.append("JSONObject");
        }
        creatIntentSB2.append("> ");
        creatIntentSB2.append(methodBeam.getMethodName());
        creatIntentSB2.append("(Context mContext ,String string){\n");
        creatIntentSB2.append("HttpParams params =new  HttpParams();\n");
        creatIntentSB2.append("params.put(\"string\", string);");
        if (methodBeam.getConverterType() == 1) {
            creatIntentSB2.append("TypeReference typeReference = new TypeReference<List<");
            creatIntentSB2.append(methodBeam.getBeanSName());
            creatIntentSB2.append(">>() {};");
        }

        //开始方法
        creatIntentSB.append("public  ");
        if (methodBeam.getRxType() == 0) {
            creatIntentSB.append("LiveData<");
            creatIntentSB.append(creatIntentSB2.toString());
            if (methodBeam.getHttpType() == 0) {
                creatIntentSB.append("\n\t return AacUtils.httpLiveGet(");
            } else {
                creatIntentSB.append("\n\t return AacUtils.httpLivePost(");
            }
        } else {
            creatIntentSB.append("Flowable<");
            creatIntentSB.append(creatIntentSB2.toString());
            if (methodBeam.getHttpType() == 0) {
                creatIntentSB.append("\n\t return AacUtils.httpRxGet(");
            } else {
                creatIntentSB.append("\n\t return AacUtils.httpRxPost(");
            }
        }
        creatIntentSB.append(methodBeam.getUriName());
        creatIntentSB.append("\n\t\t,params,\n");
        if (methodBeam.getConverterType() == 0) {//bean类型
            creatIntentSB.append("\t\tnew BeanConverter<>(");
            creatIntentSB.append(methodBeam.getKeyName());
            creatIntentSB.append(",");
            creatIntentSB.append(methodBeam.getBeanSName());
            creatIntentSB.append(".class));\n");
        } else if (methodBeam.getConverterType() == 1) {
            creatIntentSB.append("\t\tnew BeanConverter<>(");
            creatIntentSB.append(methodBeam.getKeyName());
            creatIntentSB.append(",");
            creatIntentSB.append("typeReference.getType()));\n");
        } else {
            creatIntentSB.append("\t\tnew JsonObjectConverter());\t");
        }
        creatIntentSB.append("}\n");
        PsiMethod methodFromText = mFactory.createMethodFromText(creatIntentSB.toString(), mClass);
        mClass.addBefore(methodFromText, mClass.getLastChild());
    }


    /**
     * 添加 import
     *
     * @param fullyQualifiedName fullyQualifiedName
     * @param simpleName         simpleName
     */
    private void addImport(String fullyQualifiedName, String simpleName) {
        if (!(mFile instanceof PsiJavaFile)) {
            return;
        }
        final PsiJavaFile javaFile = (PsiJavaFile) mFile;

        PsiImportList importList = javaFile.getImportList();
        if (importList == null) {
            return;
        }

        // Check if already imported
//        for (PsiImportStatementBase is : importList.getAllImportStatements()) {
//            String impQualifiedName = is.getImportReference().getQualifiedName();
//            if (fullyQualifiedName.equals(impQualifiedName)) {
//                return; // Already imported so nothing neede
//            }
//
//        }
        if (ImportHelper.isAlreadyImported(javaFile, fullyQualifiedName)) {
            return;
        }
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(mClass.getProject());
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(mClass.getProject()).getClassesByName(simpleName, searchScope);
        for (int i = 0; i < psiClasses.length; i++) {
            if (psiClasses[i].getQualifiedName().equals(fullyQualifiedName)) {
                importList.add(mFactory.createImportStatement(psiClasses[i]));
                return;
            }
        }
    }


}
