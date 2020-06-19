package quickFixes;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.QuickFixFactory;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static inspections.LombokAnnotationInspection.VALUE_ANNOTATION;

public class ReplaceWithInlineFix extends LocalQuickFixOnPsiElement {
    private final String FAMILY_NAME = "Replace this class with an inline";
    private final String InlineAnnotation = "java.lang.__inline__";
    private PsiClass aClass;

    public ReplaceWithInlineFix(@NotNull PsiClass aClass) {
        super(aClass);
        this.aClass = aClass;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getText() {
        return FAMILY_NAME;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        PsiFile containingFile = aClass.getContainingFile();
        WriteCommandAction.runWriteCommandAction(project, FAMILY_NAME, null, () -> {
            aClass.getModifierList().addAnnotation(InlineAnnotation);
            PsiAnnotation annotation = Objects.requireNonNull(aClass.getModifierList()).findAnnotation(VALUE_ANNOTATION);
            if (annotation != null) {
                annotation.delete();
            }
            IntentionAction optimizeImportsFix = QuickFixFactory.getInstance().createOptimizeImportsFix(false);
            if (optimizeImportsFix.isAvailable(project, null, file)) {
                optimizeImportsFix.invoke(project, null, file);
            }
        }, containingFile);
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getFamilyName() {
        return FAMILY_NAME;
    }

}
